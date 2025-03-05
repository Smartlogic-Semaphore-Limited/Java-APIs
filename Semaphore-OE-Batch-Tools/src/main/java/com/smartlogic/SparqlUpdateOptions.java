package com.smartlogic;

/**
 * Options to send with SPARQL update calls. The default settings are
 * safe, indicating to KMM to run constraint checks and run edit rules,
 * but will fail the commit if there are constraint violations.
 * For fastest performance, set runEditRules and runCheckConstraints
 * flags to false. For safest results when using batching, the acceptWarnings
 * flag will always be true by the batch client.
 *
 */
public class SparqlUpdateOptions {

  /**
   * True to proactively accept constraint warnings and proceed with committing changes.
   */
  public boolean acceptWarnings;

  /**
   * False to not run check constraints when running SPARQL update.
   */
  public boolean runCheckConstraints;

  /**
   * False to not run edit rules when SPARQL Update is sent.
   */
  public boolean runEditRules;

  /**
   * Default constructor. AcceptWarnings is false, runEditRules is true,
   * and runCheckConstraints is true.
   */
  public SparqlUpdateOptions() {
    this(false, true, true);
  }

  /**
   * Constructor where parameters are specified.
   * @param acceptWarnings whether to accept KMM constraint warnings proactively.
   * @param runEditRules whether to have KMM run edit rules
   * @param runCheckConstraints whehter to have KMM run constraint checking.
   */
  public SparqlUpdateOptions(boolean acceptWarnings, boolean runEditRules, boolean runCheckConstraints) {
    this.acceptWarnings = acceptWarnings;
    this.runCheckConstraints = runCheckConstraints;
    this.runEditRules = runEditRules;
  }

  /**
   * Whether acceptWarnings option is enabled.
   * @return true if acceptWarnings option is enabled.
   */
  public boolean isAcceptWarnings() {
    return acceptWarnings;
  }

  /**
   * Sets whether the acceptWarnings option is enabled.
   * @param acceptWarnings whether acceptWarnings is enabled.
   */
  public void setAcceptWarnings(boolean acceptWarnings) {
    this.acceptWarnings = acceptWarnings;
  }

  /**
   * Whether runCheckConstraints option is enabled.
   * @return true if runCheckConstraints option is enabled.
   */
  public boolean isRunCheckConstraints() {
    return runCheckConstraints;
  }

  /**
   * Sets whether the runCheckConstraints option is enabled.
   * @param runCheckConstraints whether runCheckConstraints is enabled.
   */
  public void setRunCheckConstraints(boolean runCheckConstraints) {
    this.runCheckConstraints = runCheckConstraints;
  }

  /**
   * Whether runEditRules option is enabled.
   * @return true if runEditRules option is enabled.
   */
  public boolean isRunEditRules() {
    return runEditRules;
  }

  /**
   * Sets whether the runEditRules option is enabled.
   * @param runEditRules whether runEditRules is enabled.
   */
  public void setRunEditRules(boolean runEditRules) {
    this.runEditRules = runEditRules;
  }
}
