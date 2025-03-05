package com.smartlogic;

import com.google.common.base.Preconditions;
import com.smartlogic.oebatch.beans.JobResult;
import com.smartlogic.rdfdiff.DiffToSparqlInsertUpdateBuilder;
import com.smartlogic.rdfdiff.RDFDifference;
import com.smartlogic.rdfdiff.RDFDifferenceBuilder;
import com.smartlogic.tools.JenaUtil;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Batch client for OE. Send addition and removal of triples in batch-fashion using SPARQL.
 */
public class OEBatchClient implements Closeable {

  static Logger logger = LoggerFactory.getLogger(OEBatchClient.class);

  protected OEModelEndpoint endpoint = null;
  protected Model currentModel = null;
  protected Model pendingModel = null;
  protected BatchMode batchMode = BatchMode.None;
  protected SparqlUpdateOptions sparqlUpdateOptions = new SparqlUpdateOptions();
  protected int batchThreshold = 1000000;
  protected boolean batchEnabled = false;

  /**
   * Constructor for a OEBatchClient object. The OEModelEndpoint includes the OE base URL, the
   * access token, and the model IRI.
   *
   * @param endpoint the OEModelEndpoint object
   */
  public OEBatchClient(OEModelEndpoint endpoint) {
    Preconditions.checkNotNull(endpoint);
    this.endpoint = endpoint;
  }

  /**
   * Gets the current OE endpoint.
   *
   * @return the OEModelEndpoint object.
   */
  public OEModelEndpoint getEndpoint() {
    return endpoint;
  }

  /**
   * Sets the current OE endpoint
   *
   * @param endpoint the OEModelEndpoint object
   */
  public void setEndpoint(OEModelEndpoint endpoint) {
    this.endpoint = endpoint;
  }

  /**
   * Sets the batch model (not currently implemented)
   * @param mode the BatchMode
   */
  @Deprecated
  public void setBatchMode(BatchMode mode) {
    if (mode != null) {
      this.batchMode = mode;
    }
  }

  /**
   * Load the current model from OE and prepares to track changes. This method resets the baseline
   * for the batch changes collected. Use with caution, any changes will be lost.
   *
   * @throws OEConnectionException OE communication exception
   * @throws InterruptedException thread interrupted
   * @throws IOException I/O exception
   */
  public void loadCurrentModelFromOE()
      throws IOException, OEConnectionException, InterruptedException {
    this.currentModel = ModelLoader.loadOEModelToMem(endpoint);
    this.pendingModel = ModelLoader.loadModelToMem(currentModel);

    /*
     * Unnecessary coupling?
     */
    JenaUtil.setStandardNsPrefixes(currentModel);
    JenaUtil.setStandardNsPrefixes(pendingModel);
  }

  /**
   * Get a reference to the current (original, unmodified) model. The getDiff call uses this as the
   * "before" in the diff.
   *
   * @return the current Model object
   */
  public Model getCurrentModel() {
    return this.currentModel;
  }

  /**
   * Get a reference to the pending (modified) model. The getDiff call uses this as the "after" in
   * the diff.
   * You make your changes to the pending model in most cases.
   *
   * @return the pending Model object.
   */
  public Model getPendingModel() {
    return this.pendingModel;
  }

  /**
   * Sets the current (unmodified, original to compare against) model to the specified model (by
   * reference)
   *
   * @param model the current Model object.
   */
  public void setCurrentModel(Model model) {
    this.currentModel = model;
  }

  /**
   * Sets the pending (modified, changed to compare current against) model.
   *
   * @param model the pending Model object
   */
  public void setPendingModel(Model model) {
    this.pendingModel = model;
  }

  /**
   * Get the SparqlUpdateOptions config object.
   *
   * @return the SparqlUpdateOptions object
   */
  public SparqlUpdateOptions getSparqlUpdateOptions() {
    return this.sparqlUpdateOptions;
  }

  /**
   * Set the batch threshold
   *
   * @param batchThreshold the batch threshold if batching is enabled.
   */
  public void setBatchThreshold(int batchThreshold) {
    this.batchThreshold = batchThreshold;
  }

  /**
   * Returns the batch threshold, used if batching is enabled.
   * @return the batch threshold
   */
  public int getBatchThreshold() {
    return this.batchThreshold;
  }

  /**
   * Set the SparqlUpdateOptions config object.
   *
   * @param options the SparqlUpdateOptions object
   */
  public void setSparqlUpdateOptions(SparqlUpdateOptions options) {
    this.sparqlUpdateOptions = options;
  }

  /**
   * Returns true if batching is enabled
   * @return true if batching is enabled
   */
  public boolean isBatchEnabled() {
    return batchEnabled;
  }

  /**
   * Sets whether batching should be enabled.
   * Use batching with care. Each batch sent to KMM runs in a separate transaction.
   * @param batchEnabled whether batching should be enabled.
   */
  public void setBatchEnabled(boolean batchEnabled) {
    this.batchEnabled = batchEnabled;
  }
  /**
   * Reset the client by copying pending to current model. Use this if you've committed all pending
   * changes and want to start working on new changes without pulling a new copy of the model from
   * OE.
   *
   * @throws IOException
   */
  public void reset() throws IOException {
    this.currentModel.close();
    this.currentModel = null;
    this.currentModel = this.pendingModel;
    this.pendingModel = ModelLoader.loadModelToMem(currentModel);
    this.commitJobResult = null;
    JenaUtil.setStandardNsPrefixes(currentModel);
    JenaUtil.setStandardNsPrefixes(pendingModel);
  }

  /**
   * The failed job result record, used when a commit fails. This gets reset to null on every commit.
   */
  private JobResult commitJobResult;

  /**
   * Return the JobResult object generated during the last failed commit.
   * If there is an error, the error details will be contained
   * in the corresponding JobResult object.
   * @return the JobResult object with the failed job error information.
   */
  public JobResult getCommitJobResult() {
    return commitJobResult;
  }

  /**
   * Commit the model changes to KMM. Returns true of the commit was successful.
   * <p>
   * If the commit was unsuccessful, call {@link #getCommitJobResult} to get
   * the JobResult object containing the job id, HTTP error status code and
   * a list of error objects that contain the details from KMM about what caused the commit
   * to fail.
   * <p>
   * The commit method will create batches of triples to commit if batching is enabled the total number of
   * triples to add or delete is greater than the configured batch size. Be careful when using batching
   * as each batch runs as a separate transaction. If a batch in the middle fails, this will leave
   * KMM model data in an indeterminate state!
   *
   * @return true if the commit was success.
   * @throws IOException exception communicating with KMM
   */
  public boolean commit() throws IOException {

    commitJobResult = null;

    RDFDifference rdfDiff = getBatchDiff();

    if (rdfDiff.getInLeftOnly().isEmpty() && rdfDiff.getInRightOnly().isEmpty()) {
      logger.info("No changes detected, no data sent");
      return false;
    }

    logger.info("Building and sending SPARQL, possibly in batches");

    long nDelete = rdfDiff.getInLeftOnly().size();
    long nAdd = rdfDiff.getInRightOnly().size();
    logger.info("  triples to delete: {}", nDelete);
    logger.info("  triples to add   : {}", nAdd);

    int nBatches = 0;
    boolean success = true;
    logger.info("Batch enabled?: {}", batchEnabled);
    logger.info("Batch threshold: {}", batchThreshold);

    SparqlUpdateOptions localSparqlUpdateOptions = this.sparqlUpdateOptions;
    if (batchEnabled) {
      logger.warn("Batch enabled, resetting SPARQL options acceptWarnings to true to be safe.");
      localSparqlUpdateOptions = new SparqlUpdateOptions(
          true,
          sparqlUpdateOptions.isRunEditRules(),
          sparqlUpdateOptions.isRunCheckConstraints());
    }

    if (!batchEnabled || (nDelete + nAdd) <= batchThreshold) {
      logger.info(
          "Total triples count [{}] within threshold, running in single DELETE/INSERT SPARQL command",
          nDelete + nAdd);
      success = endpoint.runSparqlUpdate(
          DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(rdfDiff),
          localSparqlUpdateOptions);
      if (!success) {
        this.commitJobResult = endpoint.getJobResultRecord();
      }

    } else {

      // batch mode. Send deletes first, then adds. (maybe use an import command for inserts?)
      logger.info(
          "Total triples [{}] exceeds threshold [{}], running DELETE and INSERT SPARQL statements in batches",
          nDelete + nAdd, batchThreshold);

      List<String> batchSparqlList =
          DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdateBatches(rdfDiff, batchThreshold);
      logger.info("Number of batches: {}", batchSparqlList.size());

      for (String batchStr : batchSparqlList) {
        if (logger.isDebugEnabled()) {
          logger.debug("Batch SPARQL: [{}]", batchStr);
        }
        logger.info("Sending SPARQL batch ({} statements)", batchThreshold);
        success = endpoint.runSparqlUpdate(batchStr, localSparqlUpdateOptions);
        if (!success) {
          logger.warn("Batch SPARQL update failed. Fetch commit job result records for errors.");
          this.commitJobResult = endpoint.getJobResultRecord();
          break;
        }
        nBatches++;
        logger.info("Batch SPARQL completed successfully");
        logger.info("{} of {} batches completed.", nBatches, batchSparqlList.size());
      }
    }

    return success;
  }

  @Override
  public void close() throws IOException {
    if (this.currentModel != null) {
      this.currentModel.close();
      this.currentModel = null;
    }
    if (this.pendingModel != null) {
      this.pendingModel.close();
      this.pendingModel = null;
    }
    this.endpoint = null;
  }

  /**
   * Return changes diff between current and pending model.
   *
   * @return
   */
  public RDFDifference getBatchDiff() {
    return RDFDifferenceBuilder.buildDifference(currentModel, pendingModel);
  }

  enum BatchMode {
    None, ByConcept
  }
}
