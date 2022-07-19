package com.smartlogic;

import com.smartlogic.rdfdiff.DiffToSparqlInsertUpdateBuilder;
import com.smartlogic.rdfdiff.RDFDifference;
import com.smartlogic.rdfdiff.RDFDifferenceBuilder;
import com.smartlogic.tools.JenaUtil;
import org.apache.jena.ext.com.google.common.base.Preconditions;
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
  protected int batchThreshold = 100000;

  /**
   * Constructor for a OEBatchClient object. The OEModelEndpoint includes the OE base URL, the
   * access token, and the model IRI.
   *
   * @param endpoint
   */
  public OEBatchClient(OEModelEndpoint endpoint) {
    Preconditions.checkNotNull(endpoint);
    this.endpoint = endpoint;
  }

  /**
   * Gets the current OE endpoint.
   *
   * @return
   */
  public OEModelEndpoint getEndpoint() {
    return endpoint;
  }

  /**
   * Sets the current OE endpoint
   *
   * @param endpoint
   */
  public void setEndpoint(OEModelEndpoint endpoint) {
    this.endpoint = endpoint;
  }

  public void setBatchMode(BatchMode mode) {
    if (mode != null) {
      this.batchMode = mode;
    }
  }

  /**
   * Load the current model from OE and prepares to track changes. This method resets the baseline
   * for the batch changes collected. Use with caution, any changes will be lost.
   *
   * @throws OEConnectionException
   * @throws InterruptedException
   * @throws IOException
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
   * @return
   */
  public Model getCurrentModel() {
    return this.currentModel;
  }

  /**
   * Get a reference to the pending (modified) model. The getDiff call uses this as the "after" in
   * the diff.
   *
   * @return
   */
  public Model getPendingModel() {
    return this.pendingModel;
  }

  /**
   * Sets the current (unmodified, original to compare against) model to the specified model (by
   * reference)
   *
   * @param model
   */
  public void setCurrentModel(Model model) {
    this.currentModel = model;
  }

  /**
   * Sets the pending (modified, changed to compare current against) model.
   *
   * @param model
   */
  public void setPendingModel(Model model) {
    this.pendingModel = model;
  }

  /**
   * Get the SparqlUpdateOptions config object.
   *
   * @return
   */
  public SparqlUpdateOptions getSparqlUpdateOptions() {
    return this.sparqlUpdateOptions;
  }

  /**
   * Set the batch threshold
   *
   * @param batchThreshold
   */
  public void setBatchThreshold(int batchThreshold) {
    this.batchThreshold = batchThreshold;
  }

  public int getBatchThreshold() {
    return this.batchThreshold;
  }

  /**
   * Set the SparqlUpdateOptions config object.
   *
   * @param options
   */
  public void setSparqlUpdateOptions(SparqlUpdateOptions options) {
    this.sparqlUpdateOptions = options;
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

    JenaUtil.setStandardNsPrefixes(currentModel);
    JenaUtil.setStandardNsPrefixes(pendingModel);
  }

  /**
   * Commit the changes to KMM.
   *
   * @return
   * @throws IOException
   */
  public boolean commit() throws IOException {

    RDFDifference rdfDiff = getBatchDiff();

    if (rdfDiff.getInLeftOnly().size() == 0 && rdfDiff.getInRightOnly().size() == 0) {
      logger.info("No changes detected, no data sent");
    } else {
      logger.info("Building and sending SPARQL batches");

      long nDelete = rdfDiff.getInLeftOnly().size();
      long nAdd = rdfDiff.getInRightOnly().size();
      logger.info("  triples to delete: " + nDelete);
      logger.info("  triples to add   : " + nAdd);

      int nBatches = 0;

      if ((nDelete + nAdd) <= batchThreshold) {
        logger.info(
            "Total triples count [{}] within threshold, running in single DELETE/INSERT SPARQL command",
            nDelete + nAdd);
        endpoint.runSparqlUpdate(DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(rdfDiff),
            sparqlUpdateOptions);
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
          endpoint.runSparqlUpdate(batchStr, sparqlUpdateOptions);
          nBatches++;
          logger.info("Batch SPARQL completed");
          logger.info("{} of {} batches completed.", nBatches, batchSparqlList.size());
        }
      }
    }
    logger.info("SPARQL batch updates completed. Reset client before running another batch.");

    /*
    Always return true. If there's an issue encountered when running the SPARQL update, we throw an exception.
    This logic happens in runSparqlUpdate method on the endpoint object. So rather than break the signature,
    just return true always and client will have to catch exceptions if there are problems.
     */
    return true;
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
