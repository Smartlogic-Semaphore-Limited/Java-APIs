// ----------------------------------------------------------------------
// Product: Concepts Server Java API
//
// (c) 2025 Progress Software  Ltd
// ----------------------------------------------------------------------
package com.smartlogic.concepts.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogic.concepts.client.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptsClient implements AutoCloseable {
  Logger logger = LoggerFactory.getLogger(this.getClass());


  private String url;

  private HttpClient httpClient;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    if (url == null) {
      throw new IllegalArgumentException("url cannot be null");
    }
    this.url = url;
  }

  private long timeoutSeconds = 60;

  public long getTimeoutSeconds() {
    return timeoutSeconds;
  }

  public void setTimeoutSeconds(long timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  private String apiToken;

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  private String proxyHost;

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  private int proxyPort;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  private String language;

  public String getOntology() {
    return ontology;
  }

  public void setOntology(String ontology) {
    this.ontology = ontology;
  }

  private String ontology;

  private void initHttpClient()  {
    if (this.httpClient == null) {
      HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
      httpClientBuilder.connectTimeout(Duration.ofSeconds(60));

      if (proxyHost != null && proxyPort != 0) {
        httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
      }
      this.httpClient = httpClientBuilder.build();
    }
  }

  /**
   * Return the set of published models
   *
   * @return the set po publisheed models
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public List<Model> getAllModels() throws ConceptsException {
    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, Model.class);
    return getDataList("/models", null, null, javaType);
  }

  /**
   * Return the model structure
   *
   * @return the structure of the published ontology
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public Structure getStructure() throws ConceptsException {
    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, Concept.class);
    InputStream serverResponse = getData( "/models/" + getOntology() + "/structure", null, null);

    try {
      Structure structure =  (new ObjectMapper()).readValue(serverResponse, Structure.class);
      logger.debug("Structure parsed");
        return structure;
    } catch (IOException e) {
      throw new ConceptsException(e);
    }
  }

  /**
   * Return all concepts from the ontology
   *
   * @return All concepts in the published ontology
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public Map<String, Concept> getAllConcepts() throws ConceptsException {
    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, Concept.class);
    List<Concept> conceptsArray = getDataList( "/models/" + getOntology() + "/concepts", null, null, javaType);
    return mapFromList(conceptsArray);
  }

  /**
   * Return all concepts in the requested collection
   *
   * @return All concepts in the requested collection
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public Map<String, Concept> getConceptsInCollection(String identifier) throws ConceptsException {
    Map<String, String> parameters = new HashMap();
    parameters.put("filter", "memberOf[id=\"" + URLEncoder.encode(identifier,  StandardCharsets.UTF_8) + "\"]");

    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, Concept.class);
    List<Concept> conceptsArray = getDataList( "/models/" + getOntology() + "/concepts", parameters, null, javaType);
    return mapFromList(conceptsArray);
  }

  /**
   * Return all collections from the ontology
   *
   * @return All collections in the published ontology
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public Map<String, Collection> getAllCollections() throws ConceptsException {
    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, Collection.class);
    List<Collection> collections = getDataList( "/models/" + getOntology() + "/collections", null, null, javaType);
    return mapFromList(collections);
  }

  /**
   * Return all concept schemes from the ontology
   *
   * @return All concept schemes in the published ontology
   * @throws ConceptsException
   *          If there is a problem communicating with the server
   */
  public Map<String, ConceptScheme> getAllConceptSchemes() throws ConceptsException {
    JavaType javaType = new ObjectMapper().getTypeFactory().constructParametricType(List.class, ConceptScheme.class);
    List<ConceptScheme> conceptSchemes = getDataList( "/models/" + getOntology() + "/concept-schemes", null, null, javaType);
    return mapFromList(conceptSchemes);
  }

  private <T extends ObjectWithId> Map<String, T> mapFromList(List<T> objectList) {
    Map<String, T> objectMap = new HashMap<>();
    for (T objectWithId: objectList) {
      objectMap.put(objectWithId.getId(), objectWithId);
    }
    return objectMap;
  }

  private InputStream getData(String urlPath, Map<String, String> parameters, String body) throws ConceptsException {
    URI uri;
    try {
      StringBuilder stringBuilder = new StringBuilder(getUrl() + urlPath);
      if (parameters != null) {
        String separator = "?";
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
          stringBuilder.append(separator)
                        .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            separator = "&";
        }
      }
      uri = new URI(stringBuilder.toString());
    } catch (URISyntaxException e) {
      throw new ConceptsException(e);
    }

    if (logger.isInfoEnabled()) {
      logger.info("getData - entry: '" + uri.toASCIIString() + "'");
    }

    initHttpClient();

    HttpRequest.Builder builder = HttpRequest.newBuilder();
    builder.GET().timeout(Duration.ofSeconds(timeoutSeconds)).uri(uri);
    if (logger.isDebugEnabled()) {
      logger.debug("About to make HTTP request: {}", uri.toASCIIString());
    }

    if (getApiToken() != null) {
      builder.header("Authorization", getApiToken());
    }
    HttpRequest request = builder.build();

    HttpResponse<InputStream> response;
    try {
      response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
    } catch (IOException | InterruptedException e) {
      throw new ConceptsException(e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("HTTP request complete: " + uri.toASCIIString());
    }

    if (response == null) {
      throw new ConceptsException("Null response from http client: " + uri.toASCIIString());
    }

    int statusCode = response.statusCode();

    if (logger.isDebugEnabled()) {
      logger.debug("HTTP request complete: " + statusCode + " " + uri.toASCIIString());
    }

    if (statusCode != 200) {
      throw new ConceptsException(
              "Status code " + statusCode + " received from URL: " + uri.toASCIIString());
    }
    return response.body();
  }

  private <T> List<T> getDataList(String urlPath, Map<String, String> parameters, String body, JavaType javaType) throws ConceptsException {
    InputStream serverResponse = getData(urlPath, parameters, body);
    List<T> results;
    try {
      results = (new ObjectMapper()).readValue(serverResponse, javaType);
    } catch (IOException e) {
      throw new ConceptsException(e);
    }
    return results;
    }

  @Override
  public void close()  {

  }

}
