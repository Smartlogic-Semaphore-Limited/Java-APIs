package com.smartlogic;

import org.apache.jena.ext.com.google.common.base.Preconditions;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for fetching and loading RDF models into Jena models.
 */
public class ModelLoader {

  /**
   * Logger
   */
  static Logger logger = LoggerFactory.getLogger(ModelLoader.class);

  /**
   * Fetch a model from Ontology Editor, load and return as TDB-backed Jena model.
   *
   * @param endpoint
   * @param tDbDirectoryPath
   * @return
   */
  public static Model loadOEModelToTdb(OEModelEndpoint endpoint, String tDbDirectoryPath) {
    Preconditions.checkNotNull(endpoint);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(tDbDirectoryPath));

    if (logger.isDebugEnabled()) {
      logger.debug("OEModelEndpoint: {}", endpoint);
      logger.debug("TDB Dir path   : {}", tDbDirectoryPath);
    }

    String rdfUri = buildOEExportApiUrl(endpoint);

    if (logger.isInfoEnabled()) {
      logger.info(" OE export URL: {}", rdfUri);
    }

    Dataset dataset = TDBFactory.createDataset(tDbDirectoryPath);
    Model model = dataset.getNamedModel(endpoint.modelIri);
    TDBLoader.loadModel(model, rdfUri );
    return model;
  }

  /**
   * Fetch a model from Ontology Editor, load and return as memory-backed Jena model.
   *
   * @param endpoint
   * @return
   */
  public static Model loadOEModelToMem(OEModelEndpoint endpoint) {

    Preconditions.checkNotNull(endpoint);

    if (logger.isDebugEnabled()) {
      logger.debug("OEModelEndpoint base URL: {}", endpoint.toString());
    }

    String loadUri = buildOEExportApiUrl(endpoint);

    if (logger.isInfoEnabled()) {
      logger.info(" OE export URL: {}", loadUri);
    }

    return RDFDataMgr.loadModel(loadUri);
  }

  /**
   * Fetch a model at the specified URI, load and return an TDB-backed Jena model.
   *
   * @param rdfUri
   * @param modelId
   * @param tDbDirectoryPath
   * @return
   */
  public static Model loadModelToTdb(String rdfUri, String modelId, String tDbDirectoryPath) {

    if (logger.isDebugEnabled()) {
      logger.debug("RDF URI : {}", rdfUri);
      logger.debug("Model ID: {}", modelId);
      logger.debug("TDB  DIR: {}", tDbDirectoryPath);
    }

    Dataset dataset = TDBFactory.createDataset(tDbDirectoryPath);
    Model model = dataset.getNamedModel(modelId);
    TDBLoader.loadModel(model, rdfUri );
    return model;
  }

  /**
   * Creates a new model, adds the specified model to it, and returns the new model.
   * @param inModel
   * @return
   */
  public static Model loadModelToMem(Model inModel) {
    Model newModel = ModelFactory.createDefaultModel();
    newModel.add(inModel);
    return newModel;
  }

  /**
   * Return an new TDB-backed Jena model with the specified model added.
   *
   * @param inModel
   * @param modelId
   * @param tDbDirectoryPath
   * @return
   */
  public static Model loadModelToTdb(Model inModel, String modelId, String tDbDirectoryPath) {

    if (logger.isDebugEnabled()) {
      logger.debug("Model  : <object>");
      logger.debug("Model ID: {}", modelId);
      logger.debug("TDB  DIR: {}", tDbDirectoryPath);
    }

    Dataset dataset = TDBFactory.createDataset(tDbDirectoryPath);
    Model model = dataset.getNamedModel(modelId);
    model.add(inModel);
    return model;
  }

  /**
   * Fetch a model at the specified URI, load, and return a memory-backed Jena model.
   * @param rdfUri
   * @return
   */
  public static Model loadModelToMem(String rdfUri) {

    Preconditions.checkArgument(!Strings.isNullOrEmpty(rdfUri));

    if (logger.isDebugEnabled()) {
      logger.debug("Model load URI: {}", rdfUri);
    }

    return RDFDataMgr.loadModel(rdfUri);
  }

  /**
   * Builds an export URI for a given model.
   * @param endpoint
   * @return
   */
  public static String buildOEExportApiUrl(OEModelEndpoint endpoint) {
    Preconditions.checkNotNull(endpoint);
    Preconditions.checkNotNull(endpoint.baseUrl);
    Preconditions.checkNotNull(endpoint.modelIri);

    String exportUrl = endpoint.buildApiUrl()
        .append("?path=backup%2F")
        .append(endpoint.modelIri)
        .append("%2Fexport&serialization=http:%2F%2Ftopbraid.org%2Fsparqlmotionlib%23Turtle")
        .toString();

    if (logger.isDebugEnabled()) {
      logger.debug("OE Export URL: {}", exportUrl);
    }

    return exportUrl;
  }
}
