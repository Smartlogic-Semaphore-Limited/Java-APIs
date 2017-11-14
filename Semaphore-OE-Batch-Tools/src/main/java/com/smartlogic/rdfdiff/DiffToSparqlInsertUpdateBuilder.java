package com.smartlogic.rdfdiff;

import com.smartlogic.tools.JenaUtil;
import org.apache.jena.ext.com.google.common.base.Preconditions;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class DiffToSparqlInsertUpdateBuilder {

  static Logger logger = LoggerFactory.getLogger(DiffToSparqlInsertUpdateBuilder.class);

  /**
   * Build a SPARQL INSERT/DELETE statement using the specified RDFDifference object.
   * The "right" model is the final state, the "left" model is the initial state.
   * Changes make the left look like the right.
   * @param diff
   * @return
   * @throws IOException
   */
  public static String buildSparqlInsertUpdate(RDFDifference diff) throws IOException {

    Preconditions.checkNotNull(diff);
    if (diff.inRightOnly.size() < 1 && diff.inLeftOnly.size() < 1)
      return null;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

        if (diff.inLeftOnly.size() > 0) {
          writer.append("DELETE {\n");
          StmtIterator it2 = diff.inLeftOnly.listStatements();
          while (it2.hasNext()) {
            Statement stmt = it2.nextStatement();
            JenaUtil.printSPARQLStatement(stmt, writer);
          }
          writer.append("}\n");
        }

        if (diff.inRightOnly.size() > 0) {
          writer.append("INSERT {\n");
          StmtIterator it = diff.inRightOnly.listStatements();
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
    } finally {

    }
  }
}
