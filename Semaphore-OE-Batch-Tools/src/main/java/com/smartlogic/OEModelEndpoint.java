package com.smartlogic;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.Token;
import com.smartlogic.cloud.TokenFetcher;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.jena.ext.com.google.common.base.Preconditions;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.query.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by stevenbiondi on 6/21/17.
 * Semaphore Workbench Ontology Editor endpoint client.
 * (For now, used to execute SPARQL insert and update calls)
 */
public class OEModelEndpoint {

  static Logger logger = LoggerFactory.getLogger(OEModelEndpoint.class);

  protected String baseUrl;
  protected String accessToken;
  protected String cloudTokenFetchUrl;
  protected String cloudAPIKey;
  protected String modelIri;
  protected String proxyHost;
  protected Integer proxyPort;

  /**
   * Build the api URI for Ontology Editor. All RESTful commands extend this URI.
   *
   * @return
   */
  public StringBuffer buildApiUrl() {
    StringBuffer buf = new StringBuffer()
        .append(baseUrl)
        .append("api");
    if (!Strings.isNullOrEmpty(accessToken)) {
      buf.append("/t/").append(accessToken);
    }
    return buf;
  }

  public StringBuffer buildSPARQLUrl() {
    return buildApiUrl().append("/").append(modelIri).append("/sparql");
  }

  /**
   * Runs the SPARQL query and returns a detached ResultSet.
   * If you have a large query, use the same technique inline to stream results and save memory.
   *
   * @param sparql
   * @return
   */
  public ResultSet runSparqlQuery(String sparql) {

    if (logger.isDebugEnabled())
      logger.debug("run SPARQL query: {}", sparql);

    Query query = QueryFactory.create(sparql);
    ResultSet results = null;
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();

    setCloudAuthHeaderIfConfigured(clientBuilder);

    if (proxyHost != null && proxyPort != null) {
      HttpHost proxy = new HttpHost(proxyHost, proxyPort);
      clientBuilder.setProxy(proxy);
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

    setCloudAuthHeaderIfConfigured(clientBuilder);

    if (proxyHost != null && proxyPort != null) {
      HttpHost proxy = new HttpHost(proxyHost, proxyPort);
      clientBuilder.setProxy(proxy);
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
   *
   * @return
   */
  public Token getCloudToken() {
    Token token = null;
    try {
      TokenFetcher tokenFetcher = new TokenFetcher(cloudTokenFetchUrl, cloudAPIKey);
      token = tokenFetcher.getAccessToken();
    } catch (CloudException e) {
      throw new RuntimeException("Failed to fetch cloud token.", e);
    }
    return token;
  }

  @Override
  public String toString() {
    StringBuilder bldr = new StringBuilder();
    bldr.append("Base URL             : ").append(baseUrl).append("\n");
    bldr.append("Model IRI            : ").append(modelIri).append("\n");
    bldr.append("Access Token         : ").append(accessToken).append("\n");
    bldr.append("Cloud Key            : ").append(cloudAPIKey).append("\n");
    bldr.append("Cloud Token Fetch Url: ").append(cloudTokenFetchUrl).append("\n");
    return bldr.toString();
  }

  /**
   * Gets the OE base URL (e.g. http://server-name:8080/swoe/)
   *
   * @return
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Sets the OE base URL (e.g. http://server-name:8080/swoe/)
   *
   * @param baseUrl
   */
  public void setBaseUrl(String baseUrl) {
    if (!Strings.isNullOrEmpty(baseUrl) && !baseUrl.endsWith("/")) {
      baseUrl += "/";
    }

    this.baseUrl = baseUrl;
  }

  /**
   * Gets the OE access token
   *
   * @return
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * Sets the OE access token
   *
   * @param accessToken
   */
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Gets the cloud API key for cloud.smartlogic.com.
   *
   * @return
   */
  public String getCloudAPIKey() {
    return cloudAPIKey;
  }

  /**
   * Sets the cloud API key for cloud.smartlogic.com
   *
   * @param cloudAPIKey
   */
  public void setCloudAPIKey(String cloudAPIKey) {
    this.cloudAPIKey = cloudAPIKey;
  }

  /**
   * Gets the cloud token fetch URL
   *
   * @return
   */
  public String getCloudTokenFetchUrl() {
    return cloudTokenFetchUrl;
  }

  /**
   * Sets the cloud token fetch URL
   *
   * @param cloudTokenFetchUrl
   */
  public void setCloudTokenFetchUrl(String cloudTokenFetchUrl) {
    this.cloudTokenFetchUrl = cloudTokenFetchUrl;
  }

  /**
   * Gets the model IRI (e.g. model:myExample)
   *
   * @return
   */
  public String getModelIri() {
    return modelIri;
  }

  /**
   * Sets the model IRI (e.g. model:myExample)
   *
   * @param modelIri
   */
  public void setModelIRI(String modelIri) {
    this.modelIri = modelIri;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public Integer getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(Integer proxyPort) {
    this.proxyPort = proxyPort;
  }

  protected void setCloudAuthHeaderIfConfigured(HttpClientBuilder clientBuilder) {
    if (!Strings.isNullOrEmpty(cloudTokenFetchUrl) && !Strings.isNullOrEmpty(cloudAPIKey)) {
      Token cloudToken = getCloudToken();
      String cloudTokenString = cloudToken.getAccess_token();
      if (!Strings.isNullOrEmpty(cloudTokenString)) {
        Header header = new BasicHeader("Authorization", cloudTokenString);
        clientBuilder.setDefaultHeaders(ImmutableSet.of(header));
      }
    }

  }

  /**
   * Builds an export URI for a given model.
   *
   * @return
   */
  private String buildOEExportApiUrl() {
    Preconditions.checkNotNull(baseUrl);
    Preconditions.checkNotNull(modelIri);

    String exportUrl = buildApiUrl()
        .append("?path=backup%2F")
        .append(modelIri)
        .append("%2Fexport&serialization=http:%2F%2Ftopbraid.org%2Fsparqlmotionlib%23Turtle")
        .toString();

    if (logger.isDebugEnabled()) {
      logger.debug("OE Export URL: {}", exportUrl);
    }

    return exportUrl;
  }

  public String fetchData() throws IOException, OEConnectionException {
    String fetchUri = buildOEExportApiUrl();

    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    setCloudAuthHeaderIfConfigured(clientBuilder);
    try (CloseableHttpClient httpClient = clientBuilder.build()) {
      HttpGet httpGet = new HttpGet(fetchUri);

      HttpResponse response = httpClient.execute(httpGet);
      if (response == null) throw new OEConnectionException("Null response from http client: " + fetchUri);
      if (response.getStatusLine() == null)
        throw new OEConnectionException("Null status line from http client: " + fetchUri);


      int statusCode = response.getStatusLine().getStatusCode();

      if (logger.isDebugEnabled())
        logger.debug("HTTP request complete: " + statusCode + " " + fetchUri);

      if (statusCode != HttpStatus.SC_OK) {
        throw new OEConnectionException("Status code " + statusCode + " received from URL: " + fetchUri);
      }

      HttpEntity entity = response.getEntity();

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      entity.writeTo(byteArrayOutputStream);
      return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }
  }
}
