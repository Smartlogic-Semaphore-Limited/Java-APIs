package com.smartlogic.rdfdiff;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * A structure that contains a left and right model, and two models that move the left
 * model to the right model in terms of triple adds and deletes.
 */
public class RDFDifference {

  Model leftModel;
  Model rightModel;
  Model inLeftOnly;
  Model inRightOnly;

  /**
   * Constructor
   * @param left - the "left" model
   * @param right - the "right" model
   * @param inLeftOnly - model of statements
   * @param inRightOnly
   */
  public RDFDifference(Model left, Model right, Model inLeftOnly, Model inRightOnly) {
    this.leftModel = left;
    this.rightModel = right;
    this.inLeftOnly = inLeftOnly;
    this.inRightOnly = inRightOnly;
  }

  public Model getInLeftOnly() {
    return this.inLeftOnly;
  }

  public Model getInRightOnly() {
    return this.inRightOnly;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
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
