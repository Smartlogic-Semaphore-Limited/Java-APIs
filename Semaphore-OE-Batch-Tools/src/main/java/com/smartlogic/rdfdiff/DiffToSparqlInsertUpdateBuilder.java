package com.smartlogic.rdfdiff;

import com.smartlogic.tools.JenaUtil;
import org.apache.jena.ext.com.google.common.base.Preconditions;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DiffToSparqlInsertUpdateBuilder {

  static Logger logger = LoggerFactory.getLogger(DiffToSparqlInsertUpdateBuilder.class);

  /**
   * Return a list of SPARQL statements to perform delete then insert batches where total number
   * of sent triples is less than batchSize argument.
   *
   * @diff the RDFDifference object
   * @param batchSize the maximum batch size for each statement.
   * @return a list of SPARQL statements.
   */
  public static List<String> buildSparqlInsertUpdateBatches(RDFDifference diff, long batchSize) throws IOException {

    List<String> sparqlList = Lists.newArrayList();

    // build left only to start (just deletes)
    // since we're going to
    Model tempInLeftOnly = ModelFactory.createDefaultModel();
    Model tempInRightOnly = ModelFactory.createDefaultModel();

    // deletes

    long nTriples = 0;
    StmtIterator it1 = diff.inLeftOnly.listStatements();
    while (it1.hasNext()) {

      Statement stmt = it1.nextStatement();
      tempInLeftOnly.add(stmt);
      nTriples++;

      if (nTriples % batchSize == 0) {
        sparqlList.add(DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(tempInLeftOnly, tempInRightOnly));
        tempInLeftOnly = ModelFactory.createDefaultModel();
      }
    }

    if (tempInLeftOnly.size() > 0) {
      sparqlList.add(DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(tempInLeftOnly, tempInRightOnly));
      tempInLeftOnly = ModelFactory.createDefaultModel();
    }

    // inserts

    nTriples = 0;
    StmtIterator it2 = diff.inRightOnly.listStatements();
    while (it2.hasNext()) {

      Statement stmt = it2.nextStatement();
      tempInRightOnly.add(stmt);
      nTriples++;

      if (nTriples % batchSize == 0) {
        sparqlList.add(DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(tempInLeftOnly, tempInRightOnly));
        tempInRightOnly = ModelFactory.createDefaultModel();
      }
    }

    if (tempInRightOnly.size() > 0) {
      sparqlList.add(DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(tempInLeftOnly, tempInRightOnly));
      tempInRightOnly = ModelFactory.createDefaultModel();
    }

    return sparqlList;
  }

  /**
   * Build a SPARQL INSERT/DELETE statement using the specified inLeftOnly and inRightOnly models.
   * The "right" model is the final state, the "left" model is the initial state.
   * Changes will make the left model look like the right.
   *
   * @param inLeftOnly  - triples that are in "left" model only and should be removed. (left is current model, right is updated model)
   * @param inRightOnly - triples that are in the "right" model only and should be added. (left is current model, right is updated model)
   * @return
   * @throws IOException
   */
  public static String buildSparqlInsertUpdate(Model inLeftOnly, Model inRightOnly) {

    Preconditions.checkNotNull(inLeftOnly);
    Preconditions.checkNotNull(inRightOnly);

    if (inRightOnly.size() < 1 && inLeftOnly.size() < 1)
      return null;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

        if (inLeftOnly.size() > 0) {
          writer.append("DELETE {\n");
          StmtIterator it2 = inLeftOnly.listStatements();
          while (it2.hasNext()) {
            Statement stmt = it2.nextStatement();
            JenaUtil.printSPARQLStatement(stmt, writer);
          }
          writer.append("}\n");
        }

        if (inRightOnly.size() > 0) {
          writer.append("INSERT {\n");
          StmtIterator it = inRightOnly.listStatements();
          while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            JenaUtil.printSPARQLStatement(stmt, writer);
          }
          writer.append("}\n");
        }

        writer.append("WHERE {}");
        writer.flush();
        String sparql = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        if (logger.isDebugEnabled())
          logger.debug("Change SPARQL: [{}]", sparql);
        return sparql;
      } finally {
      }
    } catch (IOException ioe) {
      throw new RuntimeException("ByteArray write failed.", ioe);
    } finally {
    }
  }


  /**
   * Build a SPARQL INSERT/DELETE statement using the specified RDFDifference object.
   * The "right" model is the final state, the "left" model is the initial state.
   * Changes make the left look like the right.
   * @param diff
   * @return
   * @throws IOException
   */
  public static String buildSparqlInsertUpdate(RDFDifference diff) {
    Preconditions.checkNotNull(diff);
    return buildSparqlInsertUpdate(diff.inLeftOnly, diff.inRightOnly);
  }
}
