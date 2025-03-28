package com.smartlogic.rdfdiff;

import com.google.common.collect.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.SKOSXL;

import java.util.*;

/**
 * Builds a difference between two models and returns a structure with differences.
 */
public class RDFDifferenceBuilder {

  /**
   * Build and return an RDFDifference between two models. All statements with bnodes are ignored.
   * @param modelLeft
   * @param modelRight
   * @return
   */
  public static RDFDifference buildDifference(Model modelLeft, Model modelRight) {

    Model inLeftOnlyModel = ModelFactory.createDefaultModel();
    Model inRightOnlyModel = ModelFactory.createDefaultModel();

    {
      StmtIterator it = modelLeft.listStatements();
      while (it.hasNext()) {
        Statement stmt = it.nextStatement();
        if (!stmt.getSubject().isAnon() && !stmt.getObject().isAnon() && !modelRight.contains(stmt)) {
          inLeftOnlyModel.add(stmt);
        }
      }
    }
    {
      StmtIterator it = modelRight.listStatements();
      while (it.hasNext()) {
        Statement stmt = it.nextStatement();
        if (!stmt.getSubject().isAnon() && !stmt.getObject().isAnon() && !modelLeft.contains(stmt)) {
          inRightOnlyModel.add(stmt);
        }
      }
    }

/*
    modelLeft.listStatements().toList()
        .stream()
        .filter(stmt -> !stmt.getObject().isAnon() && !stmt.getSubject().isAnon() && !modelRight.contains(stmt))
        .forEach(stmt -> inLeftOnlyModel.add(stmt));

    modelRight.listStatements().toList()
        .stream()
        .filter(stmt -> !stmt.getObject().isAnon() && !stmt.getSubject().isAnon() && !modelLeft.contains(stmt))
        .forEach(stmt -> inRightOnlyModel.add(stmt));
*/

    return new RDFDifference(modelLeft, modelRight, inLeftOnlyModel, inRightOnlyModel);
  }

  /**
   * Organizes the differences by subject URI and bundles them together into batches.
   * Optionally "chase" secondary subjects for given Properties and add them to the primary subject batch.
   * (to group prefLabel and altLabel changes for concepts into the same batch)
   * @param diff - the diff to batch by subject
   * @param chaseIncludeSubjectProperties - properties to traverse to include additional secondary subjects (i.e. prefLabels)
   */
  public static Collection<RDFSubjectDifference> buildSubjectBatches(RDFDifference diff, List<Property> chaseIncludeSubjectProperties) {

    if (diff == null) {
      throw new IllegalArgumentException("diff is null");
    };

    if (null == chaseIncludeSubjectProperties) {
      chaseIncludeSubjectProperties = Lists.newArrayList();
    }

    Map<Resource, RDFSubjectDifference> resourceToModel = Maps.newHashMap();
    Set<Resource> subjectResources = Sets.newHashSet();
    Multimap<Resource, Resource> extraIncludeResources = HashMultimap.create();

    /*
     * Build the set of unique Resource objects in this diff.
     */
    {
      ResIterator it = diff.inLeftOnly.listSubjects();
      while (it.hasNext()) {
        Resource res = it.nextResource();
        if (!subjectResources.contains(res)) {
          subjectResources.add(res);
        }
      }
    }
    {
      ResIterator it = diff.inRightOnly.listSubjects();
      while (it.hasNext()) {
        Resource res = it.nextResource();
        if (!subjectResources.contains(res)) {
          subjectResources.add(res);
        }
      }
    }

    /*
     * Iterate the set of subjects, and sort the diff into the right subject-based diff.
     */
    Iterator<Resource> it = subjectResources.iterator();
    while (it.hasNext()) {
      Resource r = it.next();
      if (!resourceToModel.containsKey(r)) {
        RDFSubjectDifference sd = new RDFSubjectDifference(r, diff.leftModel, diff.rightModel);
        resourceToModel.put(r, sd);
      }
      RDFSubjectDifference sd = resourceToModel.get(r);

      StmtIterator leftStmtIt = diff.inLeftOnly.listStatements(r, (Property) null, (RDFNode) null);
      while (leftStmtIt.hasNext()) {
        Statement stmt = leftStmtIt.nextStatement();
        sd.inLeftOnly.add(stmt);

        if (chaseIncludeSubjectProperties.contains(stmt.getPredicate()) &&
            !extraIncludeResources.containsEntry(r, stmt.getObject())) {
          extraIncludeResources.put(r, (Resource)stmt.getObject());
        }
      }

      StmtIterator rightStmtIt = diff.inRightOnly.listStatements(r, (Property) null, (RDFNode) null);
      while (rightStmtIt.hasNext()) {
        Statement stmt = rightStmtIt.nextStatement();
        sd.inRightOnly.add(stmt);

        if (chaseIncludeSubjectProperties.contains(stmt.getPredicate()) &&
            !extraIncludeResources.containsEntry(r, stmt.getObject())) {
          extraIncludeResources.put(r, (Resource)stmt.getObject());
        }
      }
    }

    /*
     * If we have extra subjects to include, sort and add those to the corresponding batch
     */
    if (extraIncludeResources.size() > 0) {

      /* Iterate the Multimap that has original subject mapped to secondary subjects set.
       * We then iterate those secondary subjects and add to primary batch.
       */
      for (Resource primarySubject : extraIncludeResources.keySet()) {

        RDFSubjectDifference sd = resourceToModel.get(primarySubject);

        for (Resource secondarySubject : extraIncludeResources.get(primarySubject)) {

          StmtIterator leftStmtIt = diff.inLeftOnly.listStatements(secondarySubject, (Property) null, (RDFNode) null);
          while (leftStmtIt.hasNext()) {
            Statement stmt = leftStmtIt.nextStatement();
            sd.inLeftOnly.add(stmt);
          }

          StmtIterator rightStmtIt = diff.inRightOnly.listStatements(secondarySubject, (Property) null, (RDFNode) null);
          while (rightStmtIt.hasNext()) {
            Statement stmt = rightStmtIt.nextStatement();
            sd.inRightOnly.add(stmt);
          }

          /* remove the batch entry for the secondary subject. now promoted to primary subject */
          resourceToModel.remove(secondarySubject);
        }
      }
    }

    return resourceToModel.values();
  }

  /**
   * Returns the default list of properties to chase and include subjects
   * when batching by primary subject. (altLabel, prefLabel)
   * @return
   */
  public static List<Property> getDefaultChaseIncludeProps() {
    List<Property> props = Lists.newArrayList();
    props.add(SKOSXL.prefLabel);
    props.add(SKOSXL.altLabel);
    return props;
  }
}
