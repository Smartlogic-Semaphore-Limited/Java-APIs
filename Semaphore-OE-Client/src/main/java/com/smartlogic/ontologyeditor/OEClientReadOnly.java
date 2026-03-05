package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.smartlogic.cloud.Token;
import com.smartlogic.ontologyeditor.beans.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.sparql.util.FmtUtils;
import org.apache.jena.vocabulary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OEClientReadOnly {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  /** The canonical label for KRT Newly Added concept scheme */
  public static final String CONCEPT_REVIEW_NEWLY_ADDED = "Concept Review - Newly Added";
  /** The canonical label for KRT Modified concept scheme */
  public static final String CONCEPT_REVIEW_MODIFIED = "Concept Review - Modified";

  public static final String JSON_LD_GRAPH = "@graph";
  public static final String PARAM_PROPERTIES = "properties";
  public static final String PARAM_FILTERS = "filters";
  public static final String PATH_SKOS_CONCEPT_META_TRANSITIVE_INSTANCE = "/skos:Concept/meta:transitiveInstance";

  private static final String BASIC_PROPERTIES = "sem:guid,skosxl:prefLabel/[]";
  private static final String CONCEPT_SCHEME_PROPERTIES = "rdf:type,rdfs:label,sem:guid,skos:hasTopConcept";

  private static final Map<String, String> prefixMapping = new HashMap<>();
  static {
    prefixMapping.put("rdfs:", RDFS.getURI());
    prefixMapping.put("rdf:", RDF.getURI());
    prefixMapping.put("dc:", DC_11.getURI());
    prefixMapping.put("owl:", OWL2.getURI());
    prefixMapping.put("xsd:", XSD.getURI());
    prefixMapping.put("skos:", SKOS.getURI());
    prefixMapping.put("skosxl:", SKOSXL.getURI());
  }

  /* KRT scheme URIs to cache values */
  protected String krtModifiedConceptSchemeUri = null;
  protected String krtNewlyAddedConceptSchemeUri = null;
  protected String krtToDoConceptSchemeUri = null;


  private String baseURL;

  private final Map<String, String> headers = new HashMap<>();

  public void setHeader(String key, String value) {
    headers.put(key, value);
  }

  public String getHeader(String key) {
    return headers.get(key);
  }

  public String getBaseURL() {
    return baseURL;
  }

  public void setBaseURL(String baseURL) {
    this.baseURL = baseURL;
  }

  private String modelUri;

  public String getModelUri() {
    return modelUri;
  }

  public void setModelUri(String modelUri) {
    this.modelUri = modelUri;
    this.modelURL = null;
  }

  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  private String headerToken;

  public String getHeaderToken() {
    return headerToken;
  }

  public void setHeaderToken(String headerToken) {
    this.headerToken = headerToken;
  }

  private Token cloudToken;

  public Token getCloudToken() {
    return cloudToken;
  }

  public void setCloudToken(Token cloudToken) {
    this.cloudToken = cloudToken;
  }

  protected String getCloudTokenValue() {
    if (cloudToken == null) {
      return "";
    }

    return cloudToken.getAccess_token();
  }

  private String proxyAddress;

  public String getProxyAddress() {
    return proxyAddress;
  }

  public void setProxyAddress(String proxyAddress) {
    this.proxyAddress = proxyAddress;
  }

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

  private String proxyHost;
  private int proxyPort;

  private boolean warningsAccepted = false;

  public boolean isWarningsAccepted() {
    return warningsAccepted;
  }

  public void setWarningsAccepted(boolean warningsAccepted) {
    this.warningsAccepted = warningsAccepted;
  }

  private boolean isKRTClient = false;
  public boolean isKRTClient() {
    return isKRTClient;
  }
  public void setKRTClient(boolean krtClient) {
    isKRTClient = krtClient;
  }

  protected String getWrappedUri(String uriString) throws OEClientException {
    try {
      URI uri = new URI(uriString);
      return (uri.getAuthority() == null) ? uri.toString() : "<" + uri + ">";
    } catch (URISyntaxException e) {
      throw new OEClientException(String.format("%s is not a valid URI", uriString));
    }
  }

  private String modelURL = null;

  protected String getModelAndConceptURL(String conceptUri) {
    StringBuilder stringBuilder = new StringBuilder(getModelURL());
    stringBuilder.append("/");
    stringBuilder.append(URLEncoder.encode(String.format("<%s>", conceptUri), StandardCharsets.UTF_8));
    if (logger.isDebugEnabled()) {
      logger.debug("modelAndConceptURI: {}", stringBuilder);
    }
    return stringBuilder.toString();
  }

  protected String getModelURL() {
    if (modelURL == null) {
      modelURL = getApiURL() + modelUri;

      if (logger.isDebugEnabled()) {
        logger.debug("modelURL: {}", modelURL);
      }

    }
    return modelURL;
  }

  private String apiURL = null;

  protected String getApiURL() {
    if (apiURL == null) {
      StringBuilder stringBuilder = new StringBuilder(baseURL);
      if (!baseURL.endsWith("/")) {
        stringBuilder.append("/");
      }
      if (!StringUtils.isEmpty(token)) {
        stringBuilder.append("t/");
        stringBuilder.append(token);
        stringBuilder.append("/");
      }
      stringBuilder.append("api/");

      apiURL = stringBuilder.toString();
      if (logger.isDebugEnabled()) {
        logger.debug("apiURL: {}", apiURL);
      }
    }
    return apiURL;
  }

  protected String getStudioURL() {
    return getApiURL().replace("/kmm", "");
  }

  private String modelSysURL = null;

  protected String getModelSysURL() {
    if (modelSysURL == null) {
      StringBuilder stringBuilder = new StringBuilder(getApiURL());
      stringBuilder.append("sys/");
      stringBuilder.append(modelUri);
      modelSysURL = stringBuilder.toString();

      if (logger.isDebugEnabled()) {
        logger.debug("modelSysURL: {}", modelSysURL);
      }

    }
    return modelSysURL;
  }

  protected String getTaskSysURL(Task task) {
    return getApiURL() + "sys/" + task.getGraphUri();
  }

  public Collection<Model> getAllModels() throws OEClientException {
    logger.info("getAllModels entry");

    String url = getApiURL() + "sys/sys:Model/rdf:instance";
    logger.info("getAllModels URL: {}", url);
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "meta:displayName,meta:graphUri");

    Date startDate = new Date();
    logger.info("getAllModels making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);
    logger.info("getAllModels call complete: {}", startDate.getTime());

    JsonObject jsonResponse = JSON.parse(response);

    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<Model> models = new ArrayList<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      JsonObject modelObject = jsonValueIterator.next().getAsObject();
      models.add(new Model(modelObject));
    }
    return models;
  }

  /**
   * getAllTasks
   *
   * @return all the tasks present for this model
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Collection<Task> getAllTasks() throws OEClientException {
    logger.info("getAllTasks entry");

    String url = getModelSysURL();
    logger.info("getAllTasks URL: {}", url);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "semfun:hasMainTag/(meta:graphUri|meta:displayName)");
    queryParameters.put(PARAM_FILTERS, "subject_hasTask(notExists rdf:type sem:ORTTask)");
    logger.info("getAllTasks queryParameters: {}", queryParameters);


    Date startDate = new Date();
    logger.info("getAllTasks making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<Task> tasks = new ArrayList<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      JsonObject modelData = jsonValueIterator.next().getAsObject();
      JsonValue hasMainTagVal = modelData.get("semfun:hasMainTag");
      if (hasMainTagVal != null) {
        JsonArray taskArray = hasMainTagVal.getAsArray();
        Iterator<JsonValue> jsonTaskIterator = taskArray.iterator();
        while (jsonTaskIterator.hasNext()) {
          tasks.add(new Task(jsonTaskIterator.next().getAsObject()));
        }
      }
    }
    return tasks;

  }

  public Collection<ChangeRecord> getChangesSince(Date date) throws OEClientException {

    StringBuilder stringBuilder = new StringBuilder(getApiURL());
    stringBuilder.append("tch");
    stringBuilder.append(modelUri);
    stringBuilder.append("/teamwork:Change/rdf:instance");

    String url = stringBuilder.toString();
    logger.debug("getChangesSince: '{}'", url);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES,
        "?triple/(teamwork:subject|teamwork:predicate|teamwork:object),sem:committed");
    queryParameters.put(PARAM_FILTERS, String.format("subject(sem:committed >= \"%s\"^^xsd:dateTime)",
        date.toInstant().toString()));
    queryParameters.put("sort", "sem:committed");

    Date startDate = new Date();
    logger.info("getChangesSince making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<ChangeRecord> changeRecords = new ArrayList<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      changeRecords.add(new ChangeRecord(this, jsonValueIterator.next().getAsObject()));
    }
    return changeRecords;

  }

  public Collection<ConceptClass> getConceptClasses() throws OEClientException {
    return getConceptClasses(1000);
  }

  public Collection<RelationshipType> getRelationshipTypes(String parentType)
      throws OEClientException {
    logger.info("getRelationshipTypes entry: {}", parentType);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES,
        "rdfs:label,owl:inverseOf,rdfs:subPropertyOf,owl:inverseOf/rdfs:label,owl:inverseOf/rdfs:subPropertyOf");

    String url = getModelURL() + "/" + parentType + "/meta:transitiveSubProperty";

    Date startDate = new Date();
    logger.info("getRelationshipTypes making call  : {}", url);
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse( response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<RelationshipType> relationshipTypes = new HashSet<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      relationshipTypes.add(new RelationshipType(this, jsonValueIterator.next().getAsObject()));
    }
    return relationshipTypes;

  }


  public Collection<RelationshipType> getLabelTypes()
          throws OEClientException {
    logger.info("getLabelTypes entry");

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES,
            "rdfs:label,rdfs:subPropertyOf");

    String url = getModelURL() + "/skosxl:altLabel/meta:transitiveSubProperty";

    Date startDate = new Date();
    logger.info("getLabelTypes  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse( response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<RelationshipType> relationshipTypes = new HashSet<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      relationshipTypes.add(new RelationshipType(this, jsonValueIterator.next().getAsObject()));
    }
    return relationshipTypes;

  }

  public Collection<MetadataType> getMetadataTypes()
          throws OEClientException {
    logger.info("getMetadataTypes entry");

    Collection<MetadataType> metadataTypes = new HashSet<>();

    addMetadataTypesForFilter(metadataTypes, "subject(rdfs:range/a=rdfs:Datatype),subject(rdfs:domain=?class),class(rdfs:subClassOf*=skos:Concept)");
    addMetadataTypesForFilter(metadataTypes, "subject(semfun:effectiveRange=?range),range(rdf:type=rdfs:Datatype),subject(a=sem:DefaultProperty)");

    return metadataTypes;

  }

  private void addMetadataTypesForFilter(Collection<MetadataType> metadataTypes, String filter)
          throws OEClientException {
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES,
            "rdfs:label,rdfs:domain,rdfs:range,rdfs:subPropertyOf");

    queryParameters.put(PARAM_FILTERS, filter);

    Date startDate = new Date();
    logger.info("getMetadataTypes making call  : {}", startDate.getTime());
    String response = getResponse(getModelURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse( response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      metadataTypes.add(new MetadataType(this, jsonValueIterator.next().getAsObject()));
    }

  }

  /**
   * Return all hierarchical relationships classes
   *
   * @return - a collection of all hierarchical relationship types
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Collection<RelationshipType> getHierarchicalRelationshipTypes() throws OEClientException {
    return getRelationshipTypes("skos:broader");
  }

  /**
   * Return all associative relationships classes
   *
   * @return - a collection of all associative relationship types
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Collection<RelationshipType> getAssociativeRelationshipTypes() throws OEClientException {
    return getRelationshipTypes("skos:related");
  }

  /**
   * Return all concept classes
   *
   * @return - a collection containing all concept classes
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Collection<ConceptClass> getConceptClasses(int limit) throws OEClientException {
    logger.info("getConceptClasses entry");

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "rdfs:label,rdfs:subClassOf");
    queryParameters.put("limit", Integer.toString(limit));
    String url = getModelURL() + "/skos:Concept/meta:transitiveSubClass";

    Date startDate = new Date();
    logger.info("getConceptClasses making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<ConceptClass> conceptClasses = new HashSet<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      conceptClasses.add(new ConceptClass(this, jsonValueIterator.next().getAsObject()));
    }
    return conceptClasses;

  }

  /**
   * Return the concept with the supplied URI with pref label, uri and type fields populated
   *
   * @param conceptUri
   *          - the concept to be returned
   * @return - the requested concept
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Concept getConcept(String conceptUri) throws OEClientException {
    logger.info("getConcept entry: {}", conceptUri);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, BASIC_PROPERTIES);
    queryParameters.put("path", getPathParameter(conceptUri));

    Date startDate = new Date();
    logger.info("getConcept making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse( response);
    return new Concept(this, jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());
  }

  public ConceptScheme getConceptScheme(String conceptSchemeUri) throws OEClientException {
    logger.info("getConceptScheme entry: {}", conceptSchemeUri);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, CONCEPT_SCHEME_PROPERTIES);
    queryParameters.put("path", getPathParameter(conceptSchemeUri));

    Date startDate = new Date();
    logger.info("getConceptScheme making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse( response);
    return new ConceptScheme(this, jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());
  }

  /**
   * Return the concept with the supplied GUID
   *
   * @param guid
   *          - the GUID of the concept to be retrieved
   * @return - the requested concept
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Concept getConceptByGuid(String guid) throws OEClientException {
    return getConceptByIdentifier(new Identifier("sem:guid", guid));
  }

  /**
   * Return the concept with the supplied identifier
   *
   * @param identifier
   *          - the unique identifier for the concept (not the URI)
   * @return - the requested concept
   * @throws OEClientException
   *           - an error has occurred contacting the server
   */
  public Concept getConceptByIdentifier(Identifier identifier) throws OEClientException {
    logger.info("getConceptByIdentifier entry: {}", identifier);

    String url = getModelURL() + PATH_SKOS_CONCEPT_META_TRANSITIVE_INSTANCE;
    logger.info("getConceptByIdentifier url: {}", url);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, BASIC_PROPERTIES);
    queryParameters.put(PARAM_FILTERS, String.format("subject(exists %s \"%s\")",
        getWrappedUri(identifier.getUri()), identifier.getValue()));
    logger.info("getConceptByIdentifier queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("getConceptByIdentifier making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    return new Concept(this, jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());
  }

  public Concept getConceptByName(String relationshipTypeUri, String labelValue)
      throws OEClientException {
    return getConceptByName(relationshipTypeUri, labelValue, null);
  }

  public Concept getConceptByName(String relationshipTypeUri, String labelValue, String language)
      throws OEClientException {
    JsonObject jsonResponse = getConceptByNameResponse(relationshipTypeUri, labelValue, language);
    if (jsonResponse.get(JSON_LD_GRAPH).getAsArray().isEmpty()) {
      throw new OEClientException(
          String.format("No concept found with label type '%s' and value '%s' (%s)",
              relationshipTypeUri, labelValue, language));
    } else if (jsonResponse.get(JSON_LD_GRAPH).getAsArray().size() > 1) {
      throw new OEClientException(
          String.format("Multiple concepts found with label type '%s' and value '%s'",
              relationshipTypeUri, labelValue));
    }
    return new Concept(this, jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());
  }

  public ConceptScheme getConceptSchemeByName(String labelValue,  String language) throws OEClientException {
    JsonObject jsonResponse = getConceptSchemeByNameResponse(labelValue, language);
    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    if (jsonArray.isEmpty()) {
      throw new OEClientException(String.format("Concept Scheme not found: \"%s\"@%s", labelValue, language));
    } else if (jsonArray.size() > 1) {
      throw new OEClientException(String.format("Multiple Concept Schemes found for: \"%s\"@%s", labelValue, language));
    }
    return new ConceptScheme(this, jsonArray.get(0).getAsObject());

  }
  public Collection<Concept> getConceptsByName(String relationshipTypeUri, String labelValue)
      throws OEClientException {
    return getConceptsByName(relationshipTypeUri, labelValue, null);
  }

  public Collection<Concept> getConceptsByName(String relationshipTypeUri, String labelValue,
      String language) throws OEClientException {
    JsonObject jsonResponse = getConceptByNameResponse(relationshipTypeUri, labelValue, language);
    Collection<Concept> concepts = new ArrayList<>();
    for (JsonValue jsonValue : jsonResponse.get(JSON_LD_GRAPH).getAsArray()) {
      concepts.add(new Concept(this, jsonValue.getAsObject()));
    }
    return concepts;
  }

  private JsonObject getConceptByNameResponse(String relationshipTypeUri, String labelValue,
      String language) throws OEClientException {
    logger.info("getConceptByNameResponse entry: {} {}", relationshipTypeUri, labelValue);

    String url = getModelURL() + PATH_SKOS_CONCEPT_META_TRANSITIVE_INSTANCE;
    logger.info("getConceptByNameResponse url: {}", url);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, BASIC_PROPERTIES);
    queryParameters.put(PARAM_FILTERS, String.format("subject(%s/skosxl:literalForm matches \"%s\"%s)",
        getWrappedUri(relationshipTypeUri), labelValue, language == null ? "" : "@" + language));
    if (language != null) {
      queryParameters.put("language", language);
    }
    logger.info("getConceptByNameResponse queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("getConceptByNameResponse making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    return JSON.parse(response);
  }

  private JsonObject getConceptSchemeByNameResponse(String labelValue, String language) throws OEClientException {
    logger.info("getConceptSchemeByNameResponse entry: {}", labelValue);

    String url = getModelURL() + "/skos:ConceptScheme/meta:transitiveInstance";
    logger.info("getConceptSchemeByNameResponse url: {}", url);

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "rdfs:label,sem:guid");
    queryParameters.put(PARAM_FILTERS, String.format("subject(rdfs:label matches \"%s\"%s)",
            labelValue, language == null ? "" : "@" + language));
    if (language != null) {
      queryParameters.put("language", language);
    }
    logger.info("getConceptSchemeByNameResponse queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("getConceptSchemeByNameResponse making call  : {}", startDate.getTime());
    String response = getResponse(url, queryParameters);

    return  JSON.parse( response);
  }

  public Collection<Concept> getAllConcepts() throws OEClientException {
    return getFilteredConcepts(new OEFilter());
  }

  public Collection<Concept> getFilteredConcepts(OEFilter oeFilter) throws OEClientException {
    logger.info("getFilteredConcepts entry: {}", oeFilter);

    String url = getModelURL() + PATH_SKOS_CONCEPT_META_TRANSITIVE_INSTANCE;

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, BASIC_PROPERTIES);

    StringJoiner filterParamJoiner = new StringJoiner(",");
    if (oeFilter.getConceptClass() != null) {
      filterParamJoiner.add(String.format("subject(rdf:type=%s)", getWrappedUri(oeFilter.getConceptClass())));
    }

    if (oeFilter.getAnyLabelFilter() != null) {
      LabelFilter filter =  oeFilter.getAnyLabelFilter();
      filterParamJoiner.add("subject(notExists rdf:type/rdfs:subClassOf sem:TargetSchemeInORT)");
      String langCodeForSearch = filter.getLangCodeForSearch();
      if (filter.isRegexSearch()) {
        //TODO: figure out how to search all alt labels if possible
        filterParamJoiner.add(String.format(
                "subject((skosxl:prefLabel|skosxl:altLabel)/skosxl:literalForm matches \"%s\"%s)",
                filter.getEscapedLeftSlashesValue(), langCodeForSearch));
      } else {
        filterParamJoiner.add(String.format(
                "subject(autocomplete prefix \"%s\"%s)",
                filter.getValue(), langCodeForSearch));
      }
    }

    if (oeFilter.getPrefLabelFilter() != null) {
      LabelFilter filter =  oeFilter.getPrefLabelFilter();
      filterParamJoiner.add("subject(notExists rdf:type/rdfs:subClassOf sem:TargetSchemeInORT)");
      String langCodeForSearch = filter.getLangCodeForSearch();
      if (filter.isRegexSearch()) {
        filterParamJoiner.add(String.format(
            "subject(skosxl:prefLabel/skosxl:literalForm matches \"%s\"%s)",
            filter.getEscapedLeftSlashesValue(), langCodeForSearch));
      } else {
        filterParamJoiner.add(String.format(
            "subject(skosxl:prefLabel/skosxl:literalForm autocomplete type skosxl:Label prefix \"%s\"%s)",
            filter.getValue(), langCodeForSearch));
      }
    }

    if (oeFilter.getAltLabelFilter() != null) {
      LabelFilter filter =  oeFilter.getAltLabelFilter();
      filterParamJoiner.add("subject(notExists rdf:type/rdfs:subClassOf sem:TargetSchemeInORT)");
      String langCodeForSearch = filter.getLangCodeForSearch();
      //TODO: figure out how to search all alt labels if possible
      if (filter.isRegexSearch()) {
        filterParamJoiner.add(String.format(
            "subject(%s/skosxl:literalForm matches \"%s\"%s)",
            filter.getAltLabelType() == null ? "skosxl:altLabel" : filter.getAltLabelType(),
            filter.getValue(), langCodeForSearch));
      } else {
        filterParamJoiner.add(String.format(
            "subject(%s/skosxl:literalForm autocomplete type skosxl:Label prefix \"%s\"%s)",
            filter.getAltLabelType() == null ? "skosxl:altLabel" : filter.getAltLabelType(),
            filter.getValue(), langCodeForSearch));
      }
    }

    if (filterParamJoiner.length() > 0)
      queryParameters.put(PARAM_FILTERS, filterParamJoiner.toString());

    if (logger.isDebugEnabled()) {
      logger.debug("getFilteredConcepts queryParameters: {}", queryParameters);
    }

    Date startDate = new Date();
    logger.info("getFilteredConcepts making call  : {} {}", startDate.getTime(), url);
    String response = getResponse(url, queryParameters);
    JsonObject jsonResponse = JSON.parse(response);

    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<Concept> concepts = new HashSet<>();
    for (JsonValue jsonValue : jsonArray) {
      concepts.add(new Concept(this, jsonValue.getAsObject()));
    }
    return concepts;
  }

  public Collection<ConceptScheme> getAllConceptSchemes() throws OEClientException {
    logger.info("getAllConceptSchemes entry");

    String url = getModelURL() + "/skos:ConceptScheme/rdf:instance";

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "sem:guid,rdfs:label,skos:hasTopConcept");

    Date startDate = new Date();
    logger.info("getAllConceptSchemes making call  : {} {}", startDate.getTime(), url);
    String response = getResponse(url, queryParameters);
    JsonObject jsonResponse = JSON.parse(response);

    JsonArray jsonArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();
    Collection<ConceptScheme> conceptSchemes = new HashSet<>();
    Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
    while (jsonValueIterator.hasNext()) {
      conceptSchemes.add(new ConceptScheme(this, jsonValueIterator.next().getAsObject()));
    }
    return conceptSchemes;
  }

  public void populateRelatedConceptUris(String relationshipUri, Concept concept)
      throws OEClientException {
    logger.info("populateRelatedConceptUris entry: {}", concept.getUri());

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put("path", getPathParameter(concept.getUri(), relationshipUri));

    Date startDate = new Date();
    logger.info("populateRelatedConceptUris making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);
    logger.info("populateRelatedConceptUris call complete: {}", startDate.getTime());

    JsonObject jsonResponse = JSON.parse(response);
    concept.populateRelatedConceptUris(relationshipUri, jsonResponse.get(JSON_LD_GRAPH));
  }

  public void populateAltLabels(String altLabelTypeUri, Concept concept) throws OEClientException {
    logger.info("populateAltLabels entry: {}", concept.getUri());

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put("path", getPathParameter(concept.getUri(), altLabelTypeUri));
    queryParameters.put(PARAM_PROPERTIES, "[]");

    Date startDate = new Date();
    logger.info("populateAltLabels making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    concept.populateAltLabels(altLabelTypeUri, jsonResponse.get(JSON_LD_GRAPH));
  }

  public void populateMetadata(String metadataUri, Concept concept) throws OEClientException {
    logger.info("populateMetadata entry: {}", concept.getUri());

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, getEscapedUri(getWrappedUri(metadataUri)));

    String path = new StringJoiner("/")
        .add(getModelUri())
        .add(getEscapedUri("<" + concept.getUri() + ">"))
        .toString();
    queryParameters.put("path", path);

    logger.info("populateMetadata uri: {}", getApiURL());
    logger.info("populateMetadata queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("populateMetadata making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    concept.populateMetadata(metadataUri,
        jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());
  }

  public void populateBooleanMetadata(String metadataUri, Concept concept)
      throws OEClientException {
    logger.info("populateBooleanMetadata entry: {}", concept.getUri());

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, getEscapedUri(getWrappedUri(metadataUri)));

    String path = new StringJoiner("/")
        .add(getModelUri())
        .add(getEscapedUri("<" + concept.getUri() + ">"))
        .toString();
    queryParameters.put("path", path);

    logger.info("populateBooleanMetadata uri: {}", getApiURL());
    logger.info("populateBooleanMetadata queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("populateBooleanMetadata making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    concept.populateBooleanMetadata(metadataUri,
        jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());

  }

  public void populateClasses(Concept concept) throws OEClientException {
    logger.info("populateClasses entry: {}", concept.getUri());

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, getEscapedUri(getWrappedUri("rdf:type")));
    queryParameters.put("path", getPathParameter(concept.getUri()));

    logger.info("populateClasses uri: {}", getApiURL());
    logger.info("populateClasses queryParameters: {}", queryParameters);

    Date startDate = new Date();
    logger.info("populateClasses making call  : {}", startDate.getTime());
    String response = getResponse(getApiURL(), queryParameters);
    logger.info("populateClasses call complete: {}", startDate.getTime());

    JsonObject jsonResponse = JSON.parse(response);
    concept.populateClasses(jsonResponse.get(JSON_LD_GRAPH).getAsArray().get(0).getAsObject());

  }

  public byte[] downloadPublisherConfiguration() throws OEClientException {
    try {
      return tryToGetPublisherConfiguration();
    } catch (NoPublisherConfigsException e) {
      // The model has no publisher configurations so we need to tell KMM to create them
      String url = getApiURL() + "/publisher/" + getModelUri() + "/configs";
      getResponse(url, null);
      return tryToGetPublisherConfiguration();
    }
  }

  private byte[] tryToGetPublisherConfiguration() throws OEClientException, NoPublisherConfigsException {
    String uri = getApiURL() + "/publisher/workspace/" + getModelUri() + "/config";
    logger.info("Attempting to get publisher configuration from URI: {}", uri);

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(uri));
    addHeaders(requestBuilder);
    HttpResponse<byte[]> response = null;
    try {
      response = getHttpClient().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException | InterruptedException e) {
      throw new OEClientException(e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    if (response.statusCode() == 404) {
      throw new NoPublisherConfigsException("No publisher configuration found for model: getModelUri()");
    }
    checkResponseStatus(response);

    return response.body();
  }

  private static class NoPublisherConfigsException extends OEClientException {
    public NoPublisherConfigsException(String message) {
      super(message);
    }
  }

  public Map<String, Environment> getEnvironments() throws OEClientException {
    logger.info("getEnvironments entry");

    String url = getStudioURL() + "graphql";

    String query = "{\"query\":\"{ environments { uri name } }\\n\",\"variables\":null,\"operationName\":null}";


    String responseBody = getResponse(url, null, query, RequestType.POST);
    JsonObject jsonResponse = JSON.parse(responseBody);

    JsonObject dataObject = jsonResponse.get("data").getAsObject();
    JsonArray environmentsArray = dataObject.get("environments").getAsArray();

    Map<String, Environment> environmentMap = new HashMap<>();
    for (JsonValue environmentValue : environmentsArray) {
      JsonObject environmentObject = environmentValue.getAsObject();
      Environment environment = new Environment();
      environment.setUri(environmentObject.get("uri").getAsString().value());
      environment.setName(environmentObject.get("name").getAsString().value());
      environmentMap.put(environment.getUri(), environment);
    }

    return environmentMap;
  }
  /**
   * Load the Newly Added and Modified concept schemes for KRT and cache them for the duration of the client lifetime.
   * If these URIs are not found, throw an exception. These URIs are used when the client is in "KRT" mode.
   * If the client is in KRT mode, the client must have these concept scheme URIs loaded.
   * We use the rdfs:label object to find the concept schemes, so the labels must be present in the model and must
   * match the canonical labels defined in this class as static final strings.
   *
   * @throws OEClientException exception if the concept schemes are not found
   */
  protected void loadKRTConceptSchemeURIs() throws OEClientException {

    Collection<ConceptScheme> schemes = getAllConceptSchemes();
    if (schemes.isEmpty()) {
      throw new OEClientException("No concept schemes found in the model. There must be at least 3 concept schemes in a KRT review task.");
    }

    for (ConceptScheme scheme : schemes) {
      if (scheme.getPrefLabels().stream()
              .anyMatch(prefLabel -> prefLabel.getValue().equals(CONCEPT_REVIEW_NEWLY_ADDED))) {
        krtNewlyAddedConceptSchemeUri = scheme.getUri();
      }
      if (scheme.getPrefLabels().stream()
              .anyMatch(prefLabel -> prefLabel.getValue().equals(CONCEPT_REVIEW_MODIFIED))) {
        krtModifiedConceptSchemeUri = scheme.getUri();
      }
    }

    if (krtModifiedConceptSchemeUri == null || krtModifiedConceptSchemeUri.isEmpty()) {
      throw new OEClientException("No URI found for required KRT concept scheme with label 'Concept Review - Modified'.");
    }

    if (krtNewlyAddedConceptSchemeUri == null || krtNewlyAddedConceptSchemeUri.isEmpty()) {
      throw new OEClientException("No URI found for required KRT concept scheme with label 'Concept Review - Newly Added'.");
    }

  }

  /**
   * Returns the KRT modified concept scheme. This returns a value when KRT is enabled
   * in a task.
   * @return null or the task-specific modified concept scheme for KRT
   * @throws OEClientException exception
   */
  protected String getKRTModifiedSchemeUri() throws OEClientException {
    if (null != krtModifiedConceptSchemeUri)
      return krtModifiedConceptSchemeUri;

    loadKRTConceptSchemeURIs();

    if (krtModifiedConceptSchemeUri != null && !krtModifiedConceptSchemeUri.isEmpty()) {
      return krtModifiedConceptSchemeUri;
    } else {
      throw new OEClientException("No URI found for required concept scheme with label 'Concept Review - Modified'.");
    }
  }

  /**
   * Returns the KRT modified concept scheme. This returns a value when KRT is enabled
   * in a task.
   * @return null or the task-specific modified concept scheme for KRT
   * @throws OEClientException exception
   */
  protected String getKRTNewlyAddedSchemeUri() throws OEClientException {
    if (null != krtNewlyAddedConceptSchemeUri)
      return krtNewlyAddedConceptSchemeUri;

    loadKRTConceptSchemeURIs();

    if (krtNewlyAddedConceptSchemeUri != null && !krtNewlyAddedConceptSchemeUri.isEmpty()) {
      return krtNewlyAddedConceptSchemeUri;
    } else {
      throw new OEClientException("No URI found for required concept scheme with label 'Concept Review - Newly Added'.");
    }
  }

  private String getPath(String resourceUri) {

    logger.info("getPath - entry: {}", resourceUri);
    boolean matching =
        prefixMapping.entrySet().stream().anyMatch(e -> resourceUri.startsWith(e.getKey()));

    String processedUri =
        (matching ? "" : "<") + FmtUtils.stringEsc(resourceUri) + (matching ? "" : ">");
    String escapedUri = URLEncoder.encode(processedUri, StandardCharsets.UTF_8);

    logger.info("getPath - exit: {}", escapedUri);
    return escapedUri;
  }

  protected String getPathParameter(String conceptUri) {
    logger.info("getPathParameter - entry: {}", conceptUri);
    String pathParameter = new StringJoiner("/").add(modelUri).add(getPath(conceptUri)).toString();
    logger.info("getPathParameter - exit: {}", pathParameter);
    return pathParameter;
  }

  protected String getPathParameter(String conceptUri, String relationshipUrl) {
    logger.info("getPathParameter (2 arg) - entry: {}", conceptUri);
    StringJoiner joiner = new StringJoiner("/");
    String pathParameter = joiner.add(modelUri).add(getPath(conceptUri)).add(getPath(relationshipUrl)).toString();
    logger.info("getPathParameter (2 arg) - exit: {}", pathParameter);
    return pathParameter;
  }

  /**
   * Return the string representation of a URI when it is to be used when generating the URL for a
   * request.
   *
   * @param uriToEscape
   *          - the URI that is to be escaped
   * @return - the URI supplied encoded so that it can be used as part of a URL
   */
  protected String getEscapedUri(String uriToEscape) throws OEClientException {
    try {
      return URLEncoder.encode(uriToEscape, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new OEClientException("Exception when encoding URI " + uriToEscape, e);
    }
  }

  /**
   * See https://tools.ietf.org/html/rfc6901 section 3 as to why we have to do this
   *
   * @param wrappedUri
   *          - the URI that is to be tildered
   * @return - the wrapped Uri encoded for sysetms that don't allow slashes
   */
  protected String getTildered(String wrappedUri) {
    return wrappedUri.replace("~", "~0").replace("/", "~1");
  }

  protected enum RequestType { POST, DELETE, PATCH }

  protected void makeRequest(String url, String payload, RequestType requestType) throws OEClientException {
    makeRequest(url, null, payload, requestType);
  }
    protected void makeRequest(String url, Map<String, String> queryParameters, String payload, RequestType requestType) throws OEClientException {

    String urlToUse = getURLwithParameters(url, queryParameters);

    HttpRequest.BodyPublisher bodyPublisher = StringUtils.isEmpty(payload)
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(payload);

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(urlToUse))
            .header("Accept", "application/ld+json,application/json");
    addHeaders(requestBuilder);

    if (RequestType.POST == requestType) {
      requestBuilder.method("POST", bodyPublisher)
              .header("Content-Type", "application/ld+json");
    } else if (RequestType.PATCH == requestType) {
      requestBuilder.method("PATCH", bodyPublisher)
              .header("Content-Type", "application/json-patch+json");
    } else if (RequestType.DELETE == requestType) {
      requestBuilder.method("DELETE", bodyPublisher);
    }

    HttpResponse<String> response = null;
    try {
      response = getHttpClient().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new OEClientException(e.getClass().getSimpleName() + ": " + url + " - " + e.getMessage());
    }

    checkResponseStatus(response);

  }


  protected String getResponse(String url, Map<String, String> queryParameters) throws OEClientException {
    return getResponse(url, queryParameters, null, null);
  }

  protected String getResponse(String url, Map<String, String> queryParameters, String payload, RequestType requestType) throws OEClientException {

    String urlToUse = getURLwithParameters(url, queryParameters);


    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(urlToUse)).header("Accept", "application/ld+json,application/json");
    if (RequestType.POST == requestType) {
      if (payload == null) {
        requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
      } else {
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload))
                .header("Content-Type", "application/json");
      }
    }
    addHeaders(requestBuilder);
    HttpResponse<String> response = null;
    try {
      response = getHttpClient().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new OEClientException(e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    checkResponseStatus(response);

    return response.body();
  }

  protected String getURLwithParameters(String url, Map<String, String> queryParameters) {
    StringBuilder stringBuilder = new StringBuilder(url);
    String separator = "?";
    if ((queryParameters != null) && !queryParameters.isEmpty()) {
      for (Map.Entry<String, String> parameter: queryParameters.entrySet()) {
        stringBuilder.append(separator)
                .append(URLEncoder.encode(parameter.getKey(), StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(parameter.getValue(), StandardCharsets.UTF_8));
        separator = "&";
      }
    }
    if (warningsAccepted) {
      stringBuilder.append(separator).append("warningsAccepted=true");
    }
    return stringBuilder.toString();
  }

  protected void addHeaders(HttpRequest.Builder requestBuilder) {
    if (getCloudToken() != null) {
      requestBuilder.header("Authorization", getCloudTokenValue());
    }
    if (getHeaderToken() != null) {
      requestBuilder.header("X-Api-Key", getHeaderToken() );
    }
    if (isKRTClient()) {
      requestBuilder.header("x-change-accepted", "false");
      requestBuilder.header("x-split-change", "true");
    }
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      requestBuilder.header(entry.getKey(), entry.getValue());
    }

  }

  private HttpClient httpClient = null;
  protected HttpClient getHttpClient() {
    if (httpClient == null) {
      HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
      if (getProxyHost() != null && getProxyPort() != 0) {
        httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(getProxyHost(), getProxyPort())));
      }
      httpClient = httpClientBuilder.build();
    }
    return httpClient;
  }

  protected <T> void checkResponseStatus(HttpResponse<T> response) throws OEClientException {
    if (isSuccess(response)) {
      logger.info("Call completed successfully");
    } else {
      String message = String.format("Call returned error %s: %s",
              response.statusCode(), response.body());
      logger.warn(message);
      throw new OEClientException(message);

    }
  }

  private <T> boolean isSuccess(HttpResponse<T> response) {
    return ((response.statusCode() > 199) && (response.statusCode() < 300));
  }

  /**
   * Get the count of concepts in the model
   *
   * @return - the count of skos:Concept instances in the model
   * @throws OEClientException - an error has occurred contacting the server
   */
  public long getConceptCount() throws OEClientException {
    logger.info("getConceptCount entry");

    String url = getModelURL() + "/skos:Concept?properties=meta:meta/(meta:localTransitiveInstance)/meta:count&language=en";

    Map<String, String> queryParameters = new HashMap<>();
    String responseBody = getResponse(url, queryParameters);
    logger.debug("getConceptCount response: {}", responseBody);

    try {
      JsonObject jsonResponse = JSON.parse(responseBody);
      JsonArray graphArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();

      if (graphArray.size() > 0) {
        JsonObject conceptObject = graphArray.get(0).getAsObject();
        JsonObject metaObject = conceptObject.get("meta:meta").getAsObject();
        JsonObject transitiveInstanceObject = metaObject.get("meta:localTransitiveInstance").getAsObject();
        long count = transitiveInstanceObject.get("meta:count").getAsNumber().value().longValue();

        logger.info("getConceptCount result: {}", count);
        return count;
      }
    } catch (Exception e) {
      logger.error("Error parsing concept count response", e);
      throw new OEClientException("Failed to parse concept count response: " + e.getMessage(), e);
    }

    logger.warn("getConceptCount: No data found in response");
    return 0;
  }

  /**
   * Add a linguistic system to the model language list.
   *
   * @param language - language label, e.g. Abkhazian
   * @param notation - language notation, e.g. ab
   * @throws OEClientException - an error has occurred contacting the server
   */
  public void addLanguage(String language, String notation) throws OEClientException {
    logger.info("addLanguage entry: {} {}", language, notation);

    if (StringUtils.isBlank(language)) {
      throw new OEClientException("language must not be blank");
    }
    if (StringUtils.isBlank(notation)) {
      throw new OEClientException("notation must not be blank");
    }

    String normalizedLanguage = language.trim();
    String normalizedNotation = notation.trim();


    String url = getApiURL() + "sys/" + getModelUri() + "?language=en";

    boolean languageExists = languageExists(normalizedNotation);

    if(languageExists) {
      String payload = buildLanguagePatchPayload(normalizedLanguage, normalizedNotation, true);
      makeRequest(url, payload, RequestType.PATCH);
    } else {
    String payload = buildLanguagePatchPayload(normalizedLanguage, normalizedNotation, false);
    makeRequest(url, payload, RequestType.PATCH);

    }
  }

  private boolean languageExists(String notation) throws OEClientException {
    String url = getApiURL() + "sys/" + getModelUri() + "/lang:" + notation;

    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(PARAM_PROPERTIES, "skos:notation,meta:displayName,meta:isImported");

    try {
      String response = getResponse(url, queryParameters);
      JsonObject jsonResponse = JSON.parse(response);
      JsonArray graphArray = jsonResponse.get(JSON_LD_GRAPH).getAsArray();

      if (graphArray.isEmpty()) {
        logger.debug("languageExists: lang:{} not found (empty graph)", notation);
        return false;
      }

      // Check if skos:notation is present in the response
      JsonObject languageObject = graphArray.get(0).getAsObject();
      JsonValue skosNotation = languageObject.get("skos:notation");

      boolean exists = skosNotation != null;
      logger.debug("languageExists: lang:{} exists={} (skos:notation present={})", notation, exists, skosNotation != null);
      return exists;
    } catch (OEClientException e) {
      if (e.getMessage() != null && e.getMessage().contains(" 404")) {
        logger.debug("languageExists: lang:{} not found (404)", notation);
        return false;
      }
      throw e;
    }
  }

  private String buildLanguagePatchPayload(String language, String notation, boolean useLangFormat) {
    JsonObject valueObject = new JsonObject();
    valueObject.put("@id", useLangFormat ? "lang:" + notation : "sem:Lang-" + notation);

    JsonArray typeArray = new JsonArray();
    typeArray.add("dcterms:LinguisticSystem");
    valueObject.put("@type", typeArray);

    JsonObject titleObject = new JsonObject();
    titleObject.put("@language", "en");
    titleObject.put("@value", language);
    JsonArray titleArray = new JsonArray();
    titleArray.add(titleObject);
    valueObject.put("dc:title", titleArray);

    if (!useLangFormat) {
      JsonObject notationObject = new JsonObject();
      notationObject.put("@value", notation);
      JsonArray notationArray = new JsonArray();
      notationArray.add(notationObject);
      valueObject.put("skos:notation", notationArray);
    }

    JsonObject operationObject = new JsonObject();
    operationObject.put("op", "add");
    operationObject.put("path", "@graph/0/dcterms:language/4");
    operationObject.put("value", valueObject);

    JsonArray patchArray = new JsonArray();
    patchArray.add(operationObject);

    return patchArray.toString();
  }

}
