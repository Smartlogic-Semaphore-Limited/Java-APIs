package com.smartlogic;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.Token;
import com.smartlogic.cloud.TokenFetcher;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.ext.com.google.common.base.Joiner;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WebContent;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.jena.ext.com.google.common.base.Preconditions.checkNotNull;

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

  protected HttpClientBuilder httpClientBuilder;

  public OEModelEndpoint() {

    httpClientBuilder = HttpClientBuilder.create();

    /*
     * SCB - make timeouts infinite for this app for all HTTP requests.
     */
    RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(0)
            .setConnectionRequestTimeout(0)
            .setSocketTimeout(0).build();
    httpClientBuilder.setDefaultRequestConfig(config);
  }

  /**
   * Build the api URI for Knowledge Model Manager (KMM). All RESTful commands extend this URI.
   *
   * @return
   */
  public StringBuffer buildApiUrl() {
    StringBuffer buf = new StringBuffer().append(baseUrl);
    if (!Strings.isNullOrEmpty(accessToken)) {
      buf.append("t/").append(accessToken).append("/");
    }
    buf.append("kmm/api");
    return buf;
  }

  /**
   * Returns SPARQL URL with default sparql options.
   *
   * @return
   */
  public String buildSPARQLUrl() {
    return buildSPARQLUrl(new SparqlUpdateOptions());
  }

  public String buildSPARQLUrl(SparqlUpdateOptions options) {

    StringBuffer buf = buildApiUrl().append("/").append(modelIri).append("/sparql");

    if (null != options) {
      List<String> optionsList = new ArrayList<>();

      // default is false, don't set unless changed.
      if (options.acceptWarnings) {
        optionsList.add("warningsAccepted=true");
      }

      if (options.runEditRules) {
        optionsList.add("runEditRules=true");
      } else {
        optionsList.add("runEditRules=false");
      }

      if (options.runCheckConstraints) {
        optionsList.add("checkConstraints=true");
      } else {
        optionsList.add("checkConstraints=false");
      }

      if (optionsList.size() > 0) {
        buf.append("?");
        buf.append(Joiner.on("&").join(optionsList));
      }
    }
    return buf.toString();
  }

  /**
   * Runs the SPARQL query and returns a detached ResultSet. If you have a large query, use the same
   * technique inline to stream results and save memory.
   *
   * @param sparql
   * @return
   */
  public ResultSet runSparqlQuery(String sparql) {

    if (logger.isDebugEnabled()) {
      logger.debug("run SPARQL query: {}", sparql);
    }

    Query query = QueryFactory.create(sparql);
    ResultSet results = null;

    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    setProxyHttpHost(clientBuilder);

    setCloudAuthHeaderIfConfigured(clientBuilder);

    try (CloseableHttpClient client = clientBuilder.build();
        QueryExecution qe =
            QueryExecutionFactory.sparqlService(buildSPARQLUrl(null).toString(), query, client)) {
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
  public boolean runSparqlUpdate(String sparql, SparqlUpdateOptions options) {

    if (logger.isDebugEnabled()) {
      logger.debug("run SPARQL update: {}", sparql);
    }

    setProxyHttpHost(httpClientBuilder);
    setCloudAuthHeaderIfConfigured(httpClientBuilder);

    try (CloseableHttpClient client = httpClientBuilder.build()) {
      UpdateRequest update = UpdateFactory.create(sparql, Syntax.syntaxARQ);
      UpdateProcessor processor = UpdateExecutionFactory.createRemoteForm(update,
              buildSPARQLUrl(options), client);
      processor.execute();
    } catch (IOException ioe) {
      throw new RuntimeException("IOException.", ioe);
    }
    return true;
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

  private void setProxyHttpHost(HttpClientBuilder clientBuilder) {
    if (proxyHost != null && proxyPort != null && !proxyServerConfigSet) {
      HttpHost proxy = new HttpHost(proxyHost, proxyPort);
      clientBuilder.setProxy(proxy);
      proxyServerConfigSet = true;
    }
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
    if (!Strings.isNullOrEmpty(cloudTokenFetchUrl) && !Strings.isNullOrEmpty(cloudAPIKey) && !cloudAuthHeaderSet) {
      Token cloudToken = getCloudToken();
      String cloudTokenString = cloudToken.getAccess_token();
      if (!Strings.isNullOrEmpty(cloudTokenString)) {
        Header header = new BasicHeader("Authorization", cloudTokenString);
        clientBuilder.setDefaultHeaders(ImmutableSet.of(header));
        cloudAuthHeaderSet = true;
      }
    }

  }

  /**
   * Builds an export URI for a given model.
   *
   * @return
   */
  private String buildOEExportApiUrl() {
    checkNotNull(baseUrl);
    checkNotNull(modelIri);

    String exportUrl = buildApiUrl()
            .append("?path=backup")
            .append("%2F")
            .append(modelIri)
            .append("%2F")
            .append("export")
            .append("&async=true")
            .toString();

    if (logger.isDebugEnabled()) {
      logger.debug("KMM model export URL (async): {}", exportUrl);
    }

    return exportUrl;
  }

  public String initiateExportAsyncDownload() throws IOException, OEConnectionException {

    String initiateExportUrl = buildOEExportApiUrl();

    setProxyHttpHost(httpClientBuilder);
    setCloudAuthHeaderIfConfigured(httpClientBuilder);

    String jobId = null;
    try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
      HttpGet httpGet = new HttpGet(initiateExportUrl);

      /* NT format for response */
      httpGet.setHeader(HttpHeaders.ACCEPT, WebContent.contentTypeNTriples);

      HttpResponse response = httpClient.execute(httpGet);
      if (response == null) {
        throw new OEConnectionException("Null response from http client: " + initiateExportUrl);
      }
      if (response.getStatusLine() == null) {
        throw new OEConnectionException("Null status line from http client: " + initiateExportUrl);
      }

      int statusCode = response.getStatusLine().getStatusCode();

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request returned, status code: " + statusCode + " " + initiateExportUrl);
      }

      if (statusCode != HttpStatus.SC_ACCEPTED) {
        throw new OEConnectionException(
                "Incorrect status code " + statusCode + " received from URL: " + initiateExportUrl);
      }

      HttpEntity entity = response.getEntity();

      try (InputStream is = entity.getContent()) {
        JsonObject responseJson = JSON.parse(is);
        if (responseJson == null)
          throw new OEConnectionException("Invalid JSON response to callback for job status");

        String jobStatus = responseJson.get(JOB_STATUS).getAsString().value();
        if (null == jobStatus) {
          throw new OEConnectionException("Invalid response JSON payload");
        }

        if (!jobStatus.equals(JOB_STATUS_ACCEPTED)) {
          throw new OEConnectionException("Export job not accepted, jobStatus was: " + jobStatus);
        }

        jobId = responseJson.get("jobId").getAsString().value();
      }
    }
    return jobId;
  }

  /**
   * Fetch model using connection information. Create new model instance if first argument is null,
   * or fill specified model. This method does NOT clear out existing triples in the model object.
   *
   * @param model
   * @return
   * @throws IOException
   * @throws OEConnectionException
   */
  public Model fetchData(Model model) throws IOException, OEConnectionException, InterruptedException {

    if (null == model)
      model = ModelFactory.createDefaultModel();

    String jobId = initiateExportAsyncDownload();
    String jobStatusFromServer = getJobStatus(getJobCallbackUrl(jobId));

    while (!JOB_STATUS_FINISHED.equals(jobStatusFromServer)) {

      // break out if status from last job call was not ACCEPTED or FINISHED.
      if (!jobStatusFromServer.equals(JOB_STATUS_FINISHED) &&
              !jobStatusFromServer.equals(JOB_STATUS_ACCEPTED) &&
                !jobStatusFromServer.equals(JOB_STATUS_RUNNING)) {
        throw new OEConnectionException("Unexpected export job status from server: " + jobStatusFromServer);
      }

      logger.debug("job not done, sleeping for 1 second...");

      Thread.sleep(1000);

      jobStatusFromServer = getJobStatus(getJobCallbackUrl(jobId));

    }

    logger.debug("Async job complete, downloading results.");

    String jobResultUrl = getJobCallbackUrl(jobId) + "/result";

    setProxyHttpHost(httpClientBuilder);
    setCloudAuthHeaderIfConfigured(httpClientBuilder);

    try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
      HttpGet httpGet = new HttpGet(jobResultUrl);
      /*
       * New way to specify format on export calls: use Accept: header. Leaving old param on URI for
       * now
       */
      httpGet.setHeader(HttpHeaders.ACCEPT, WebContent.contentTypeNTriples);

      HttpResponse response = httpClient.execute(httpGet);
      if (response == null) {
        throw new OEConnectionException("Null response from http client: " + jobResultUrl);
      }
      if (response.getStatusLine() == null) {
        throw new OEConnectionException("Null status line from http client: " + jobResultUrl);
      }

      int statusCode = response.getStatusLine().getStatusCode();

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request complete: " + statusCode + " " + jobResultUrl);
      }

      if (statusCode != HttpStatus.SC_OK) {
        throw new OEConnectionException(
                "Status code " + statusCode + " received from URL: " + jobResultUrl);
      }

      HttpEntity entity = response.getEntity();

      try (InputStream is = entity.getContent()) {
        RDFDataMgr.read(model, is, RDFFormat.NT.getLang());
      }
    }
    return model;
  }

  public String getJobCallbackUrl(String jobId) {
    return buildApiUrl().append("/async/jobs/").append(jobId).toString();
  }

  /**
   * Get the job status from OE using the specified job status callback URL.
   *
   * @param callbackUrl
   * @return
   */
  public String getJobStatus(String callbackUrl) {
    checkNotNull(callbackUrl);
    logger.debug("Running getJobStatus, callback url: {}", callbackUrl);

    try {
      JsonObject responseJson;
      HttpGet httpGet = new HttpGet(callbackUrl);
      try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
        try (CloseableHttpResponse resp = httpClient.execute(httpGet)) {
          HttpEntity ent = resp.getEntity();
          try (InputStream inStr = ent.getContent()) {
            responseJson = JSON.parse(inStr);
          }
        }
      }

      if (responseJson == null)
        throw new RuntimeException("Invalid JSON response to callback for job status");

      return responseJson.get(JOB_STATUS).getAsString().value();

    } catch (Exception e) {
      throw new RuntimeException("Exception caught while getting OE job status.", e);
    }
  }
}
