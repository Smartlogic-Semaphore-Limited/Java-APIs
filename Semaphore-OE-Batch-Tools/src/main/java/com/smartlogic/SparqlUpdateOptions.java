package com.smartlogic;

/**
 * Options to send with SPARQL update calls.
 */
public class SparqlUpdateOptions {

  /**
   * Set to true to accept constraint warnings and proceed with changes.
   * Normally this is set to true when runCheckConstraints is set to false;
   */
  public boolean acceptWarnings = false;

  /**
   * Set to false to not run check constraints when running SPARQL update.
   */
  public boolean runCheckConstraints = true;

  /**
   * Set to false to not run edit rules when SPARQL Update is sent.
   */
  public boolean runEditRules = true;

  public boolean isAcceptWarnings() {
    return acceptWarnings;
  }

  public void setAcceptWarnings(boolean acceptWarnings) {
    this.acceptWarnings = acceptWarnings;
  }

  public boolean isRunCheckConstraints() {
    return runCheckConstraints;
  }

  public void setRunCheckConstraints(boolean runCheckConstraints) {
    this.runCheckConstraints = runCheckConstraints;
  }

  public boolean isRunEditRules() {
    return runEditRules;
  }

  public void setRunEditRules(boolean runEditRules) {
    this.runEditRules = runEditRules;
  }
}
