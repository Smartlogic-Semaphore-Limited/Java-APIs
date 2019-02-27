package com.smartlogic.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import static org.apache.jena.ext.com.google.common.base.Preconditions.checkArgument;

/**
 *
 */
public class JenaUtil {

  static Logger logger = LoggerFactory.getLogger(JenaUtil.class);

  /**
   *
   */
  static SimpleDateFormat dtfmt = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Helper method to set standard namespace prefixes in Jena models.
   *
   * @param m
   */
  public static void setStandardNsPrefixes(Model m) {
    checkArgument(m != null);
    m.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
    m.setNsPrefix("skosxl", "http://www.w3.org/2008/05/skos-xl#");
    m.setNsPrefix("sem", "http://www.smartlogic.com/2014/08/semaphore-core#");
    m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

  }

  /**
   * @param stmt
   */
  public static void printSPARQLStatement(Statement stmt, OutputStreamWriter osw) {

    if (logger.isDebugEnabled()) {
      logger.debug("printSPARQLStatement: stmt: {}", stmt);
    }

    StringBuffer buf = new StringBuffer();
    buf.append("  <");
    buf.append(stmt.getSubject().getURI());
    buf.append("> <");
    buf.append(stmt.getPredicate().getURI());
    buf.append("> ");
    RDFNode objNode = stmt.getObject();
    if (objNode.isURIResource())
      buf.append("<").append(objNode.asResource().getURI()).append(">");
    else {
      RDFDatatype dt = objNode.asLiteral().getDatatype();
      if ( logger.isDebugEnabled()) {
        logger.debug("printSPARQLStatement: literal datatype URI: {}, java class: {}", dt.getURI(), dt.getJavaClass());
      }

      if (dt.getURI().equals(XSDDatatype.XSDstring.getURI())) {
        buf.append("\"").append(sparqlEscape(objNode.asLiteral().getString())).append("\"");
        if (StringUtils.isNotEmpty(objNode.asLiteral().getLanguage()))
          buf.append("@").append(objNode.asLiteral().getLanguage());
      } else if (dt.getURI().equals(RDF.langString.getURI())) {
        buf.append("\"").append(sparqlEscape(objNode.asLiteral().getString())).append("\"");
        if (StringUtils.isNotEmpty(objNode.asLiteral().getLanguage()))
          buf.append("@").append(objNode.asLiteral().getLanguage());
      } else if (dt.getURI().equals(XSDDatatype.XSDboolean.getURI())) {
        buf.append(objNode.asLiteral().getBoolean());
      } else if (dt.getURI().equals(XSDDatatype.XSDint.getURI()) ||
          dt.getURI().equals(XSDDatatype.XSDinteger.getURI())) {
        buf.append(objNode.asLiteral().getInt());
      } else if (dt.getURI().equals(XSDDatatype.XSDlong.getURI())) {
        buf.append(objNode.asLiteral().getLong());
      } else if (dt.getURI().equals(XSDDatatype.XSDanyURI.getURI())) {
        buf.append("\"").append(objNode.asLiteral().getString()).append("\"^^<").append(XSDDatatype.XSDanyURI.getURI()).append(">");
      } else if (dt.getURI().equals(XSDDatatype.XSDdate.getURI())) {
        buf.append("\"").append(objNode.asLiteral().getLexicalForm()).append("\"^^<").append(XSDDatatype.XSDdate.getURI()).append(">");
      } else if (dt.getJavaClass() != null && dt.getJavaClass().getName() != null &&
          dt.getJavaClass().getName().equals("java.util.Date")) {
        buf.append("\"").append(dtfmt.format(objNode.asLiteral().getValue())).append("\"^^<").append(XSDDatatype.XSDdate.getURI()).append(">");
      } else {
        logger.warn("printSPARQLStatement: unrecognized literal datatype: {}", dt.getURI());
        buf.append("\"").append(sparqlEscape(objNode.asLiteral().getString())).append("\"");
      }
    }

    try {
      osw.append(buf.toString() + " .").append("\n");
    } catch (IOException ioe) {
      logger.error("failed to write statement for SPARQL", ioe);
    }
  }

  /**
   * @param stmt
   * @param osw
   */
  public static void printStatement(Statement stmt, OutputStreamWriter osw) {
    try {
      osw.append(stmt.toString()).append("\n");
    } catch (IOException ioe) {
      logger.error("Failed to write statement", ioe);
    }
  }

  public static String labelInUriEscape(String value) {
    if (null == value)
      return null;
    return value.replaceAll(" ", "-");
  }

  /**
   * @param value
   * @return
   */
  public static String sparqlEscape(String value) {
    if (null == value)
      return null;

    value = value.replace("\\", "\\\\");
    value = value.replace("\"", "\\\"");
    value = value.replace("\'", "\\\'");
    value = value.replace("\n", "\\n");
    value = value.replace("\t", "\\t");
    value = value.replace("\r", "\\r");
    return value;
  }

  /**
   * @param model
   * @param outputFileName
   */
  public static void writeTTL(Model model, String outputFileName) {
    FileOutputStream fo = null;
    try {
      fo = new FileOutputStream(outputFileName);
      RDFDataMgr.write(fo, model, RDFFormat.TTL);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != fo)
          fo.close();
      } catch (IOException ioe) {
        logger.error("failed to close input stream", ioe);

      }
    }
  }
}
