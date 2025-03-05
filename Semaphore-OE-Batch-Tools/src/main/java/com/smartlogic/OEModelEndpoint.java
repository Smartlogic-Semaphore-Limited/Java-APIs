package com.smartlogic;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.Token;
import com.smartlogic.cloud.TokenFetcher;
import com.smartlogic.oebatch.beans.JobResult;
import com.smartlogic.oebatch.beans.KMMError;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTPBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;


/**
 * Created by stevenbiondi on 6/21/17. Semaphore Knowledge Model Manager (KMM). All RESTful commands
 * extend this URI endpoint client. (For now, used to execute SPARQL insert and update calls)
 * 2022-02-08 - SteveB: change format to NT to speed download. Cloud timeouts can occur waiting.
 * Also default the token fetcher URL. Almost never changed but can be overwritten by client.
 */
public class OEModelEndpoint {

  public static final String JOB_STATUS_ACCEPTED = "ACCEPTED";
  public static final String JOB_STATUS_RUNNING = "RUNNING";
  public static final String JOB_STATUS = "status";
  public static final String JOB_STATUS_FINISHED = "FINISHED";
  public static final int JOB_CHECK_SLEEP_INTERVAL_MILLIS = 1000;
  public static final String X_API_KEY = "X-Api-Key";
  static Logger logger = LoggerFactory.getLogger(OEModelEndpoint.class);

  protected boolean cloudAuthHeaderSet = false;
  protected boolean proxyServerConfigSet = false;

  protected String baseUrl;
  protected String accessToken;
  protected String cloudTokenFetchUrl = "https://cloud.smartlogic.com/token/";
  protected String cloudAPIKey;
  protected String modelIri;
  protected String proxyHost;
  protected Integer proxyPort;
  protected JobResult updateSparqlJobResult;
  protected HttpClient.Builder httpClientBuilder;
  protected HttpClient httpClient;

  public OEModelEndpoint() {
    // nothing to do here.
  }

  private synchronized void initHttpClient() {
    if (httpClient == null) {
      httpClient = getHttpClientBuilder().build();
    }
  }

  private synchronized HttpClient.Builder  getHttpClientBuilder() {
    if (httpClientBuilder == null) {
      httpClientBuilder = HttpClient.newBuilder();
      httpClientBuilder.connectTimeout(Duration.of(60, SECONDS));

      if (proxyHost != null && proxyPort != 0) {
        httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
      }
    }
    return httpClientBuilder;
  }

  /**
   * Build the api URI for Knowledge Model Manager (KMM). All RESTful commands extend this URI.
   *
   * @return the full KMM API URL string
   */
  public String buildApiUrl() {
    if (Strings.isNullOrEmpty(baseUrl)) {
      throw new RuntimeException("The Studio/KMM baseUrl is not set");
    }
    StringBuilder stringBuilder = new StringBuilder();
    if (!baseUrl.endsWith("/")) {
      baseUrl = baseUrl + "/";
    }
    stringBuilder.append(baseUrl);
    if (!baseUrl.endsWith("kmm/")) {
      stringBuilder.append("kmm/");
    }
    stringBuilder.append("api/");
    if (logger.isDebugEnabled()) {
      logger.debug("apiUrl: {}", stringBuilder);
    }
    return stringBuilder.toString();
  }

  /**
   * Returns SPARQL URL with default sparql options.
   *
   * @return the full SPARQL endpoint URL string
   */
  public String buildSPARQLUrl() {
    return buildSPARQLUrl(new SparqlUpdateOptions());
  }

  public String buildSPARQLUrl(SparqlUpdateOptions options) {

    String apiUrl = buildApiUrl() + modelIri + "/sparql";

    List<String> optionsList = new ArrayList<>();

    // always run SPARQL updates asynchronously.
    optionsList.add("async=true");

    if (null != options) {

      // default is false, don't set unless changed.
      if (options.isAcceptWarnings()) {
        optionsList.add("warningsAccepted=true");
      }

      if (options.isRunEditRules()) {
        optionsList.add("runEditRules=true");
      } else {
        optionsList.add("runEditRules=false");
      }

      if (options.isRunCheckConstraints()) {
        optionsList.add("checkConstraints=true");
      } else {
        optionsList.add("checkConstraints=false");
      }

      apiUrl += "?" + Joiner.on("&").join(optionsList);
    }
    return apiUrl;
  }

  /**
   * Runs the SPARQL query and returns a detached ResultSet. If you have a large query, use the same
   * technique inline to stream results and save memory.
   *
   * @param sparql the SPARQL query text to run.
   * @return the ResultSet of results returned by the query.
   */
  public ResultSet runSparqlQuery(String sparql) {

    if (logger.isDebugEnabled()) {
      logger.debug("run SPARQL query: {}", sparql);
    }

    Query query = QueryFactory.create(sparql);
    ResultSet results;
    try {
      initHttpClient();
      QueryExecutionHTTPBuilder builder = QueryExecution.service(buildSPARQLUrl(null))
          .httpClient(httpClient).query(query);
      setHeaders(builder);
      try (QueryExecutionHTTP httpExecHttp = builder.build()) {
        results = ResultSetFactory.copyResults(httpExecHttp.execSelect());
      }
    } finally {
    }
    return results;
  }

  private Duration connectTimeout = Duration.of(60, SECONDS);

  /**
   * Returns the connect timeout duration.
   * @return the Duration
   */
  public Duration getConnectTimeout() {
    return connectTimeout;
  }

  /**
   * Sets the connect timeout duration.
   * @param connectTimeout the connect timeout duration.
   */
  public void setConnectTimeout(Duration connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  private Duration requestTimeout = Duration.of(60, MINUTES);

  /**
   * Gets the request timeout duration.
   * @return the request timeout duration
   */
  public Duration getRequestTimeout() {
    return requestTimeout;
  }

  /**
   * Set the request timeout duration.
   * @param requestTimeout the request timeout duration
   */
  public void setRequestTimeout(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  private Token cloudToken = null;
  private QueryExecutionHTTPBuilder setHeaders(QueryExecutionHTTPBuilder builder) {
    if (!Strings.isNullOrEmpty(accessToken)) {
      builder.httpHeader(X_API_KEY, accessToken);
    }
    if (Strings.isNullOrEmpty(cloudTokenFetchUrl) ||
            Strings.isNullOrEmpty(cloudAPIKey)) {
      return builder;
    }
    if ((cloudToken == null) || (cloudToken.isExpired())) {
      cloudToken = getCloudToken();
    }
    String cloudTokenString = cloudToken.getAccess_token();
    if (!Strings.isNullOrEmpty(cloudTokenString)) {
      builder.httpHeader(HttpHeaders.AUTHORIZATION, cloudTokenString);
    }
    return builder;
  }

  private HttpRequest.Builder setHeaders(HttpRequest.Builder builder) {

    if (!Strings.isNullOrEmpty(accessToken)) {
      builder.header(X_API_KEY, accessToken);
    }

    if (Strings.isNullOrEmpty(cloudTokenFetchUrl) ||
            Strings.isNullOrEmpty(cloudAPIKey)) {
      return builder;
    }
    if ((cloudToken == null) || (cloudToken.isExpired())) {
      cloudToken = getCloudToken();
    }
    String cloudTokenString = cloudToken.getAccess_token();
    if (!Strings.isNullOrEmpty(cloudTokenString)) {
      builder.header(HttpHeaders.AUTHORIZATION, cloudTokenString);
    }
    return builder;
  }

  /**
   * Run SPARQL update with specified SPARQL statement string and options.
   * The JobResult response will have details about the SPARQL update request.
   *
   * @param sparql the sparql update statement to execute
   * @param options the SPARQL options for the request.
   * @return true if the SPARQL update returned as HTTP 200 ok.
   */
  public boolean runSparqlUpdate(String sparql, SparqlUpdateOptions options) {

    if (logger.isDebugEnabled()) {
      logger.debug("run SPARQL update: {}", sparql);
    }

    int sleepCount = 0;
    try {
      String jobId = initiateSPARQLUpdateAsync(sparql, options);

      String sparqlUpdateJobStatus = getJobStatus(getJobCallbackUrl(jobId));

      while (!JOB_STATUS_FINISHED.equals(sparqlUpdateJobStatus)) {

        // break out if status from last job call was not ACCEPTED or not FINISHED.
        if (!sparqlUpdateJobStatus.equals(JOB_STATUS_ACCEPTED) &&
                !sparqlUpdateJobStatus.equals(JOB_STATUS_RUNNING)) {
          throw new OEConnectionException(
                  "Unexpected SPARQL update job status from server: " + sparqlUpdateJobStatus);
        }

        if (sleepCount % 10 == 0) {
          logger.info("SPARQL update job not finished, waiting...(waited {} seconds so far)", sleepCount);
        }

        sleepCount++;

        Thread.sleep(JOB_CHECK_SLEEP_INTERVAL_MILLIS);

        sparqlUpdateJobStatus = getJobStatus(getJobCallbackUrl(jobId));

      }

      JobResult result = getJobResult(jobId);
      logger.debug("Async SPARQL update job complete. Result: {}", result);
      this.updateSparqlJobResult = result;
      return result.httpStatusCode() == 200;

    } catch (Exception e) {
      throw new RuntimeException("SPARQL update failed.", e);
    }
  }

  public boolean isCloudAuthHeaderSet() {
    return cloudAuthHeaderSet;
  }

  public void setCloudAuthHeaderSet(boolean cloudAuthHeaderSet) {
    this.cloudAuthHeaderSet = cloudAuthHeaderSet;
  }

  public boolean isProxyServerConfigSet() {
    return proxyServerConfigSet;
  }

  public void setProxyServerConfigSet(boolean proxyServerConfigSet) {
    this.proxyServerConfigSet = proxyServerConfigSet;
  }

  /**
   * Given a Cloud API key, fetch a token.
   *
   * @return the cloud access token
   */
  public Token getCloudToken() {
    Token token = null;
    try {
      TokenFetcher tokenFetcher = new TokenFetcher(cloudTokenFetchUrl, cloudAPIKey);
      if (proxyHost != null && proxyPort != null) {
        tokenFetcher.setProxyHost(proxyHost);
        tokenFetcher.setProxyPort(proxyPort);
      }
      token = tokenFetcher.getAccessToken();
    } catch (CloudException e) {
      throw new RuntimeException("Failed to fetch cloud token.", e);
    }
    return token;
  }

  /**
   * Returns a string representation of this object's state.
   * @return the object string representation
   */
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
   * Gets the KMM base URL
   *
   * @return the KMM base URL.
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Sets the KMM base URL
   *
   * @param baseUrl the KMM base URL
   */
  public void setBaseUrl(String baseUrl) {
    if (!Strings.isNullOrEmpty(baseUrl) && !baseUrl.endsWith("/")) {
      baseUrl += "/";
    }

    this.baseUrl = baseUrl;
  }

  /**
   * Gets the Studio access token
   *
   * @return the Studio access token
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * Sets the Studio access token. For use with local, non-cloud Studio instances.
   *
   * @param accessToken the Studio access token
   */
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Gets the cloud API key set for cloud access.
   *
   * @return the cloud API key
   */
  public String getCloudAPIKey() {
    return cloudAPIKey;
  }

  /**
   * Sets the cloud API key for cloud access
   *
   * @param cloudAPIKey the cloud API key
   */
  public void setCloudAPIKey(String cloudAPIKey) {
    this.cloudAPIKey = cloudAPIKey;
  }

  /**
   * Gets the cloud token fetch URL
   *
   * @return the cloud token fetch url
   */
  public String getCloudTokenFetchUrl() {
    return cloudTokenFetchUrl;
  }

  /**
   * Sets the cloud token fetch URL
   *
   * @param cloudTokenFetchUrl the cloud token fetch url
   */
  public void setCloudTokenFetchUrl(String cloudTokenFetchUrl) {
    this.cloudTokenFetchUrl = cloudTokenFetchUrl;
  }

  /**
   * Gets the model IRI (e.g. model:myExample)
   *
   * @return the model IRI
   */
  public String getModelIri() {
    return modelIri;
  }

  /**
   * Sets the model IRI (e.g. model:myExample)
   *
   * @param modelIri the model IRI
   */
  public void setModelIRI(String modelIri) {
    this.modelIri = modelIri;
  }

  /**
   * Returns the proxy host
   * @return the proxy host
   */
  public String getProxyHost() {
    return proxyHost;
  }

  /**
   * Sets the proxy host
   * @param proxyHost the proxy host
   */
  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  /**
   * Returns the proxy port
   * @return the proxy port
   */
  public Integer getProxyPort() {
    return proxyPort;
  }

  /**
   * Sets the proxy port
   * @param proxyPort the proxy port
   */
  public void setProxyPort(Integer proxyPort) {
    this.proxyPort = proxyPort;
  }

  /**
   * Builds an export URI for a given model.
   *
   * @return the export URI for the model
   */
  private String buildModelExportApiUrl() {
    checkNotNull(baseUrl);
    checkNotNull(modelIri);

    String exportUrl = buildApiUrl() + "?path=backup%2F" + modelIri + "%2Fexport&async=true";

    if (logger.isDebugEnabled()) {
      logger.debug("KMM model export URL (async): {}", exportUrl);
    }

    return exportUrl;
  }

  /**
   * Builds an export URI for a given model.
   *
   * @return the export URI for the model
   */
  @Deprecated
  private String buildOEExportApiUrl() {
    return buildModelExportApiUrl();
  }

  /**
   * Initiate a SPARQL update asynchronously, return jobId.
   *
   * @param sparqlString the sparql update string to run.
   * @param options the SPARQL update options.
   * @return jobId
   * @throws IOException I/O exception
   * @throws OEConnectionException Studio connection exception
   */
  public String initiateSPARQLUpdateAsync(String sparqlString, SparqlUpdateOptions options) throws IOException, OEConnectionException {

    String jobId = null;

    String sparqlUpdateUrl = buildSPARQLUrl(options);

    String formData = "update=" + URLEncoder.encode(sparqlString, StandardCharsets.UTF_8);

    try {
      initHttpClient();
      HttpRequest.Builder builder = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(formData));
      setHeaders(builder);
      builder.setHeader(HttpHeaders.CONTENT_TYPE, WebContent.contentTypeHTMLForm);
      HttpRequest request = builder.uri(URI.create(sparqlUpdateUrl)).build();

      HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

      int statusCode = response.statusCode();

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP POST request returned, status code: {} {}", statusCode, sparqlUpdateUrl);
      }

      if (statusCode != HttpURLConnection.HTTP_ACCEPTED) {
        throw new OEConnectionException(
                 "Incorrect status code " + statusCode + " received from URL: " + sparqlUpdateUrl);
      }

      try (InputStream is = response.body()) {
        JsonObject responseJson = Json.createReader(is).readObject();
        if (responseJson == null) {
          throw new OEConnectionException("Invalid JSON response to callback for job status");
        }

          String jobStatus = responseJson.getString(JOB_STATUS);
          if (null == jobStatus) {
            throw new OEConnectionException("Invalid response JSON payload");
          }

          if (!jobStatus.equals(JOB_STATUS_ACCEPTED)) {
            throw new OEConnectionException("Export job not accepted, jobStatus was: " + jobStatus);
          }

          jobId = responseJson.getString("jobId");
        }
       } catch (InterruptedException e) {
        throw new RuntimeException(e);
    } finally {

    }
    return jobId;
  }

  public String initiateExportAsyncDownload() throws IOException, OEConnectionException {

    String initiateExportUrl = buildOEExportApiUrl();

    String jobId;

    try {
      initHttpClient();
      HttpRequest.Builder builder = HttpRequest.newBuilder().header(HttpHeaders.ACCEPT, WebContent.contentTypeNTriples);
      setHeaders(builder);
      HttpRequest request = builder.uri(URI.create(initiateExportUrl)).build();

      HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response == null) {
        throw new OEConnectionException("Null response from http client: " + initiateExportUrl);
      }
      int statusCode = response.statusCode();
      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request returned, status code: " + statusCode + " " + initiateExportUrl);
      }

      if (statusCode != HttpURLConnection.HTTP_ACCEPTED) {
        throw new OEConnectionException(
                "Incorrect status code " + statusCode + " received from URL: " + initiateExportUrl);
      }


      try (InputStream is = response.body()) {
        JsonObject responseJson = Json.createReader(is).readObject();
        if (responseJson == null) {
          throw new OEConnectionException("Invalid JSON response to callback for job status");
        }

        String jobStatus = responseJson.getString(JOB_STATUS);
        if (null == jobStatus) {
          throw new OEConnectionException("Invalid response JSON payload");
        }

        if (!jobStatus.equals(JOB_STATUS_ACCEPTED)) {
          throw new OEConnectionException("Export job not accepted, jobStatus was: " + jobStatus);
        }

        jobId = responseJson.getString("jobId");
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return jobId;
  }

  /**
   * Fetch model using connection information. Create new model instance if first argument is null,
   * or fill specified model. This method does NOT clear out existing triples in the model object.
   *
   * @param model
   * @return model object created from returned export triples.
   * @throws IOException
   * @throws OEConnectionException
   * @throws InterruptedException
   */
  public Model fetchData(Model model)
      throws IOException, OEConnectionException, InterruptedException {

    if (null == model) {
      model = ModelFactory.createDefaultModel();
    }

    String jobId = initiateExportAsyncDownload();
    String jobStatusFromServer = getJobStatus(getJobCallbackUrl(jobId));

    while (!JOB_STATUS_FINISHED.equals(jobStatusFromServer)) {

      // break out if status from last job call was not ACCEPTED or FINISHED.
      if (!jobStatusFromServer.equals(JOB_STATUS_FINISHED) &&
          !jobStatusFromServer.equals(JOB_STATUS_ACCEPTED) &&
          !jobStatusFromServer.equals(JOB_STATUS_RUNNING)) {
        throw new OEConnectionException(
            "Unexpected export job status from server: " + jobStatusFromServer);
      }

      logger.debug("job not done, sleeping for 1 second...");

      Thread.sleep(JOB_CHECK_SLEEP_INTERVAL_MILLIS);

      jobStatusFromServer = getJobStatus(getJobCallbackUrl(jobId));

    }

    logger.debug("Async job complete, downloading results.");

    String jobResultUrl = getJobCallbackUrl(jobId) + "/result";

    try {
      initHttpClient();
      HttpRequest.Builder builder = HttpRequest.newBuilder();
      setHeaders(builder);

      HttpRequest request = builder.uri(URI.create(jobResultUrl))
          .setHeader(HttpHeaders.ACCEPT, WebContent.contentTypeNTriples)
          .build();

      HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response == null) {
        throw new OEConnectionException("Null response from http client: " + jobResultUrl);
      }
      if (response.statusCode() != HttpURLConnection.HTTP_OK) {
        throw new OEConnectionException(
                "Status code " + response.statusCode() + " received from URL: " + jobResultUrl);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request complete: " + response.statusCode()  + " " + jobResultUrl);
      }

      try (InputStream is = response.body()) {
        RDFDataMgr.read(model, is, RDFFormat.NT.getLang());
      }
    } finally {

    }
    return model;
  }

  public String getJobCallbackUrl(String jobId) {
    if (Strings.isNullOrEmpty(jobId)) {
      return null;
    }
    return buildApiUrl() + "async/jobs/" + jobId;
  }

  public String getJobResultCallbackUrl(String jobId) {
    if (Strings.isNullOrEmpty(jobId)) {
      return null;
    }
    return getJobCallbackUrl(jobId) + "/result";
  }


  /**
   * Get the job status using the specified job status callback URL.
   *
   * @param callbackUrl the job callback URL
   * @return the job status string.
   */
  public String getJobStatus(String callbackUrl)  {
    checkNotNull(callbackUrl);
    logger.debug("Running getJobStatus, callback url: {}", callbackUrl);

    try {
      JsonObject responseJson;

      try {
        initHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        setHeaders(builder);
        HttpRequest request = builder.uri(URI.create(callbackUrl)).build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream inStr = response.body()) {
            responseJson = Json.createReader(inStr).readObject();
        }
      } finally {

      }

      if (responseJson == null) {
        throw new RuntimeException("Invalid JSON response to callback for job status");
      }

      return responseJson.getString(JOB_STATUS);

    } catch (Exception e) {
      throw new RuntimeException("Exception caught while getting OE job status.", e);
    }
  }

  /**
   * Get the job result from KMM using the specified job status callback URL.
   *
   * @param jobId the job id
   * @return the job result
   */
  public JobResult getJobResult(String jobId)  {
    checkNotNull(jobId);

    String callbackUrl = getJobResultCallbackUrl(jobId);
    logger.debug("Running getJobResult, jobId: {}, callback url: {}", jobId, callbackUrl);

    JsonObject responseJson;
    int httpStatusCode;
    List<KMMError> errorsList = Lists.newArrayList();

    try {
      initHttpClient();
      HttpRequest.Builder builder = HttpRequest.newBuilder();
      setHeaders(builder);
      HttpRequest request = builder.uri(URI.create(callbackUrl)).build();

      HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
      httpStatusCode = response.statusCode();
      if (httpStatusCode != 200) {
        try (InputStream inStr = response.body()) {
          responseJson = Json.createReader(inStr).readObject();
          JsonArray errorsArray = responseJson.getJsonArray("errors");
          errorsArray.forEach(error -> errorsList.add(new KMMError((JsonObject) error)));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Exception caught while getting OE job status.", e);
    }
    return new JobResult(jobId, httpStatusCode, errorsList);
  }

  /**
   * Return the JobResultRecord object from the last update SPARQL call.
   * @return the JobResultRecord
   */
  public JobResult getJobResultRecord()  {
    return this.updateSparqlJobResult;
  }
}
