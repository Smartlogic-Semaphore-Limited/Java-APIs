package com.smartlogic.rdfdiff;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Structure to hold differences for a particular subject.
 * I use this to group differences by subject so I can easily send
 * them as logical units to SPARQL endpoint.
 */
public class RDFSubjectDifference extends RDFDifference {
  public Resource subject;


  /**
   * Subject difference with left and right models. The inLeftOnly and inRightOnly
   * models are created using ModelFactory.createDefaultModel();
   * @param subject
   * @param left
   * @param right
   */
  public RDFSubjectDifference(Resource subject, Model left, Model right) {
    this(subject, left, right, ModelFactory.createDefaultModel(), ModelFactory.createDefaultModel());
  }

  /**
   * Constructor for RDFDifference for a particular subject that specifies left, right and
   * inLeftOnly and inRightOnly models.
   *
   * @param subject
   * @param left
   * @param right
   * @param inLeftOnly
   * @param inRightOnly
   */
  public RDFSubjectDifference(Resource subject, Model left, Model right, Model inLeftOnly, Model inRightOnly) {
    super(left, right, inLeftOnly, inRightOnly);
    this.subject = subject;
    this.inLeftOnly = inLeftOnly;
    this.inRightOnly = inRightOnly;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder().append("Subject:\n").append("  ").append(subject.toString()).append("\n");
    sb.append("inLeftOnly:\n");
    StmtIterator it = inLeftOnly.listStatements();
    while (it.hasNext()) {
      sb.append("  ").append(it.nextStatement().toString()).append("\n");
    }
    sb.append("inRightOnly:\n");
    it = inRightOnly.listStatements();
    while (it.hasNext()) {
      sb.append("  ").append(it.nextStatement().toString()).append("\n");
    }
    return sb.toString();
  }
}
