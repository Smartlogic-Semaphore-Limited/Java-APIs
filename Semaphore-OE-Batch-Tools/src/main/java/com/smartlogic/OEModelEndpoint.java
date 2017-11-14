package com.smartlogic;

import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.iri.IRI;
import org.apache.jena.query.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by stevenbiondi on 6/21/17.
 * Semaphore Workbench Ontology Editor endpoint client.
 * (For now, used to execute SPARQL insert and update calls)
 *
 */
public class OEModelEndpoint {

  static Logger logger = LoggerFactory.getLogger(OEModelEndpoint.class);

  public String baseURL;
  public String accessToken;
  public String cloudAPIKey;
  public IRI modelIRI;

  /**
   * Build the api URI for Ontology Editor. All RESTful commands extend this URI.
   *
   * @return
   */
  public StringBuffer buildApiUrl() {
    return new StringBuffer()
        .append(baseURL)
        .append("api/t/")
        .append(accessToken);
  }

  public StringBuffer buildSPARQLUrl() {
    return buildApiUrl().append("/").append(modelIRI.toString()).append("/sparql");
  }

  /**
   * Runs the SPARQL query and returns a detached ResultSet.
   * If you have a large query, use the same technique inline to stream results and save memory.
   * @param sparql
   * @return
   */
  public ResultSet runSparqlQuery(String sparql) {

    if (logger.isDebugEnabled())
      logger.debug("run SPARQL query: {}", sparql);

    Query query = QueryFactory.create(sparql);
    ResultSet results = null;
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();

    String cloudToken = getCloudToken();
    if (!Strings.isNullOrEmpty(cloudToken)) {
      Header header = new BasicHeader("Authorization", cloudToken);
      clientBuilder.setDefaultHeaders(ImmutableSet.of(header));
    }

    try (CloseableHttpClient client = clientBuilder.build();
         QueryExecution qe = QueryExecutionFactory.sparqlService(buildSPARQLUrl().toString(), query, client)) {
      results = ResultSetFactory.copyResults(qe.execSelect());
    } catch (IOException ioe) {
      throw new RuntimeException("IOException.", ioe);
    }
    return results;
  }

  /**
   * @param sparql
   * @return
   */
  public boolean runSparqlUpdate(String sparql) {

    if (logger.isDebugEnabled())
      logger.debug("run SPARQL update: {}", sparql);

    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    String cloudToken = getCloudToken();
    if (!Strings.isNullOrEmpty(cloudToken)) {
      Header header = new BasicHeader("Authorization", cloudToken);
      clientBuilder.setDefaultHeaders(ImmutableSet.of(header));
    }
    try (CloseableHttpClient client = clientBuilder.build()) {
      UpdateRequest update = UpdateFactory.create(sparql, Syntax.syntaxARQ);
      UpdateProcessor processor = UpdateExecutionFactory.createRemoteForm(update, buildSPARQLUrl().toString(), client);
      processor.execute();
    } catch (IOException ioe) {
      throw new RuntimeException("IOException.", ioe);
    }
    return true;
  }

  /**
   * Given a Cloud API key, fetch a token (TODO)
   * @return
   */
  public String getCloudToken() {
    return null;
  }

  @Override
  public String toString() {
    StringBuilder bldr = new StringBuilder();
    bldr.append("Base URL    : ").append(baseURL).append("\n");
    bldr.append("Model IRI   : ").append(modelIRI.toString()).append("\n");
    bldr.append("Access Token: ").append(accessToken).append("\n");
    bldr.append("Cloud Key   : ").append(cloudAPIKey).append("\n");
    return bldr.toString();
  }
}
