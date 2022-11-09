// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.smartlogic.ses.client.exceptions.NoSuchTermException;
import com.smartlogic.ses.client.exceptions.SESException;

public class SESClient implements AutoCloseable {
  Logger logger = LoggerFactory.getLogger(this.getClass());

  public enum DetailLevel {
    MINIMAL, FULL
  }

  private String protocol = "http";
  private String host;
  private int port = 80;
  private String path;

  private String ontology;
  private String template = "service.xml";

  private int connectionTimeoutMS;
  private int socketTimeoutMS;

  private int maxConnections = 10;

  private String language;

  private String url;

  private CloseableHttpClient httpClient;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setPort(String port) {
    this.port = Integer.parseInt(port);
  }

  public String getPath() throws SESException {
    if (path == null) {
      if (getUrl() != null) {
        try {
          path = (new URL(getUrl())).getPath();
        } catch (MalformedURLException e) {
          throw new SESException(String.format("Malformd URL: '%s'", this.getUrl()));
        }
      }
    }
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getOntology() {
    return ontology;
  }

  public void setOntology(String ontology) {
    this.ontology = ontology;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public int getConnectionTimeoutMS() {
    return connectionTimeoutMS;
  }

  public void setConnectionTimeoutMS(int connectionTimeoutMS) {
    this.connectionTimeoutMS = connectionTimeoutMS;
  }

  public int getSocketTimeoutMS() {
    return socketTimeoutMS;
  }

  public void setSocketTimeoutMS(int socketTimeoutMS) {
    this.socketTimeoutMS = socketTimeoutMS;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getMaxConnections() {
    return this.maxConnections;
  }

  private File saveFile = null;

  public File getSaveFile() {
    return saveFile;
  }

  public void setSaveFile(File saveFile) {
    this.saveFile = saveFile;
  }

  @Override
  public void close() {
    if (httpClient != null) {
      try {
        httpClient.close();
      } catch (IOException ioe) {
        logger.warn("Failed to cleanly close HttpClient.", ioe);
      }
      httpClient = null;
    }
  }

  /**
   * Return all terms from the ontology
   *
   * @return List of all descendants
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAllTerms() throws SESException {
    return getAllDescendents(null, null);
  }

  public Map<String, Term> getAllTerms(SESFilter sesFilter) throws SESException {
    logger.info("getTerms - entry");
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=allterms");

      query.append(getFilterString(sesFilter));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    Map<String, Term> returnData =
        (semaphore.getTerms() != null) ? semaphore.getTerms().getTerms() : new HashMap<>();
    logger.debug("getTermDetails - exit");
    return returnData;

  }

  /**
   * Return all terms from the ontology underneath the supplied term (deprecated?)
   *
   * @param parentId
   *          ID of the parent term
   * @param hierarchyType
   *          The hierarchical relationship type to use
   * @return List of all descendants
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAllDescendents(String parentId, String hierarchyType)
      throws SESException {
    return getAllDescendants(parentId, hierarchyType, null);
  }

  /**
   * Return all terms from the ontology underneath the supplied term
   *
   * @param parentId
   *          ID of the parent term
   * @param hierarchyType
   *          The hierarchical relationship type to use
   * @return List of all descendants
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAllDescendants(String parentId, String hierarchyType)
      throws SESException {
    return getAllDescendants(parentId, hierarchyType, null);
  }

  /**
   * Return all terms from the ontology underneath the supplied term
   *
   * @param parentId
   *          ID of the parent term
   * @param hierarchyType
   *          The hierarchical relationship type to use
   * @param filter
   *          Any SES filter to apply
   * @return List of all descendants
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAllDescendants(String parentId, String hierarchyType,
      SESFilter filter) throws SESException {
    logger.info("getAllDescendents - entry");

    Map<String, Term> returnData;
    if (parentId == null) {
      returnData = this.browse(null, filter);
    } else {
      returnData = new HashMap<>();
      Term parentTerm = this.getTermDetails(parentId, DetailLevel.FULL, filter);
      returnData.put(parentTerm.getId().getValue(), parentTerm);
    }

    Collection<String> termsToGet = new HashSet<>();
    Collection<String> missingTerms = new HashSet<>();
    boolean termsFetched = false;
    do {
      termsFetched = false;
      for (Term term : returnData.values()) {
        if (term == null) {
          continue;
        }
        for (Hierarchy hierarchy : term.getHierarchies()) {
          if ((hierarchyType != null) && (!hierarchyType.equalsIgnoreCase(hierarchy.getType()))) {
            continue;
          }

          // We don't want to go up the hierarchy
          if ("Broader Term".equalsIgnoreCase(hierarchy.getType())) {
            continue;
          }

          for (Field field : hierarchy.getFields().values()) {
            if (!returnData.containsKey(field.getId()) && !missingTerms.contains(field.getId())) {
              termsToGet.add(field.getId());
            }
          }
        }
        if (termsToGet.size() > 500) {
          break;
        }
      }
      if (termsToGet.size() > 0) {
        Map<String, Term> fetchedTerms =
            this.getTermDetails(termsToGet.toArray(new String[0]), DetailLevel.FULL, filter);
        for (String fetchedId : fetchedTerms.keySet()) {
          Term term = fetchedTerms.get(fetchedId);
          if (term != null) {
            returnData.put(fetchedId, term);
          }
        }
        for (String termRequested : termsToGet) {
          if (!returnData.containsKey(termRequested)) {
            logger.trace("Referenced term \"" + termRequested + "\" does not exist in SES index");
            missingTerms.add(termRequested); // I asked for this term, but didn't get it
          }
        }
        termsFetched = true;
        termsToGet.clear();
      }
    } while (termsFetched);

    logger.debug("getAllDescendents - exit: " + returnData.size());
    return returnData;
  }

  /**
   * Return the mapped concepts from the Semantic Enhancement Server
   *
   * @param query
   *          The query text
   * @return map from term id to term
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getMappedConcepts(String query) throws SESException {
    return getMappedConcepts(query, 0, null);
  }

  /**
   * Return the mapped concepts from the Semantic Enhancement Server
   *
   * @param query
   *          The query text
   * @param minDocs
   *          - the mininum frequency for terms to be returned
   * @param stopStage
   *          - the stage at which the mapping process should stop (1=exacts only, 2=exacts and
   *          inexact)
   * @return map from term id to term
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getMappedConcepts(String query, int minDocs, int stopStage)
      throws SESException {
    SESFilter sesFilter = new SESFilter();
    sesFilter.setMinDocs(minDocs);
    return getMappedConcepts(query, stopStage, sesFilter);
  }

  public Map<String, Term> getMappedConcepts(String query, SESFilter sesFilter)
      throws SESException {
    return getMappedConcepts(query, 0, sesFilter);
  }

  public Map<String, Term> getMappedConcepts(String query, int stopStage, SESFilter sesFilter)
      throws SESException {

    logger.info("mapConcepts - entry: '" + query + "'");
    URL url = null;

    try {
      StringBuffer cgiQuery = new StringBuffer();
      cgiQuery.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      cgiQuery.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      cgiQuery.append(getFilterString(sesFilter));
      if (stopStage != 0) {
        cgiQuery.append("&stop_cm_after_stage=" + stopStage);
      }
      cgiQuery.append("&service=conceptmap");
      cgiQuery.append("&query=" + URLEncoder.encode(query, "UTF8"));
      cgiQuery.append(getLanguageChoice());

      url = getURLImpl(cgiQuery.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.debug("mapConcepts - exit: '" + query + "'");
    if (semaphore.getTerms() != null) {
      return semaphore.getTerms().getTerms();
    }
    return new HashMap<>();
  }

  /**
   * Return the terms from the Semantic Enhancement Server matching the search term
   *
   * @param query
   *          The search text
   * @return map from term id to term
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> search(String query) throws SESException {
    return search(query, new SESFilter());
  }

  /**
   * Return the terms from the Semantic Enhancement Server matching the search term
   *
   * @param query
   *          The search text
   * @param sesFilter
   *          - container for any filters that need to be applied
   * @return map from term id to term
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> search(String query, SESFilter sesFilter) throws SESException {
    logger.info("getTerms - entry");
    URL url = null;

    try {
      StringBuffer cgiQuery = new StringBuffer();
      cgiQuery.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      cgiQuery.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      cgiQuery.append("&service=search");
      cgiQuery.append("&query=" + URLEncoder.encode(query, "UTF8"));
      cgiQuery.append(getFilterString(sesFilter));
      cgiQuery.append(getLanguageChoice());

      url = getURLImpl(cgiQuery.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    Map<String, Term> returnData =
        (semaphore.getTerms() != null) ? semaphore.getTerms().getTerms() : new HashMap<>();
    logger.debug("getTermDetails - exit");
    return returnData;
  }

  private String dateFormat = null;

  private synchronized void setDateFormat() throws SESException {
    VersionInfo versionInfo = this.getVersion();
    String version = versionInfo.getVersion();
    logger.debug("setDateFormat - version: {}", version);
    if (version != null) {
      // This is SES > 4.0.36
      dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    } else {
      dateFormat = "yyyyMMddHHmmss";
    }
  }

  private SimpleDateFormat getDateFormatter() throws SESException {
    if (dateFormat == null) {
      setDateFormat();
    }
    return new SimpleDateFormat(dateFormat);
  }

  private String getFilterString(SESFilter sesFilter) throws SESException {
    if (sesFilter == null) {
      return "";
    }
    StringBuilder path = new StringBuilder();
    try {
      if (sesFilter.getMinDocs() != SESFilter.UNDEFINED_INT) {
        path.append("&mindocs=" + sesFilter.getMinDocs());
      }

      if (sesFilter.getMaxResultCount() != SESFilter.UNDEFINED_INT) {
        path.append("&maxresultcount=" + sesFilter.getMaxResultCount());
      }

      if (sesFilter.getClasses() != null) {
        for (String className : sesFilter.getClasses()) {
          path.append("&class=" + URLEncoder.encode(className, "UTF8"));
        }
      }

      if (sesFilter.getFacets() != null) {
        for (String facetName : sesFilter.getFacets()) {
          path.append("&facet=" + URLEncoder.encode(facetName, "UTF8"));
        }
      }
      if (sesFilter.getModifiedBeforeDate() != null) {
        path.append("&filter=MODIFIED_BEFORE=" +
            getDateFormatter().format(sesFilter.getModifiedBeforeDate()));
      }
      if (sesFilter.getModifiedAfterDate() != null) {
        path.append("&filter=MODIFIED_AFTER=" +
            getDateFormatter().format(sesFilter.getModifiedAfterDate()));
      }
      if (sesFilter.getExcludeAttributes() != null) {
        for (String attribute : sesFilter.getExcludeAttributes()) {
          path.append("&filter=ATN=" + URLEncoder.encode(attribute, "UTF8"));
        }
      }
      if (sesFilter.getIncludeAttributes() != null) {
        for (String attribute : sesFilter.getIncludeAttributes()) {
          path.append("&filter=AT=" + URLEncoder.encode(attribute, "UTF8"));
        }
      }
      if (sesFilter.getStartTermZthesIds() != null) {
        for (String startTermZthesId : sesFilter.getStartTermZthesIds()) {
          path.append("&filter=DE=" + URLEncoder.encode(startTermZthesId, "UTF8"));
        }
      }
      if (sesFilter.getUris() != null) {
        for (String uri : sesFilter.getUris()) {
          path.append("&filter=URI=" + URLEncoder.encode(uri, "UTF8"));
        }
      }
      if (sesFilter.getLabelTypes() != null) {
        for (String uri : sesFilter.getLabelTypes()) {
          path.append("&filter=EQ_REL=" + URLEncoder.encode(uri, "UTF8"));
        }
      }
      if (sesFilter.getMetadata() != null) {
        for (Entry<String, String> entry : sesFilter.getMetadata().entrySet()) {
          path.append("&filter=MDC_" +
              URLEncoder.encode(entry.getKey(), "UTF8") +
              "=" +
              URLEncoder.encode(entry.getValue(), "UTF8"));
        }
      }

    } catch (UnsupportedEncodingException e) {
      logger.error("UnsupportedEncodingException: " + e.getMessage());
    }
    return path.toString();
  }

  /**
   * Return the language part of the SES request. For backwards compliance, we need to supply this
   * as "lang" and "language".
   *
   * @return The language part of the SES request
   * @throws UnsupportedEncodingException
   */
  private String getLanguageChoice() throws UnsupportedEncodingException {
    if (language == null) {
      return "";
    }
    String encodedLanguage = URLEncoder.encode(language, "UTF8");
    return "&lang=" + encodedLanguage + "&language=" + encodedLanguage;
  }

  /**
   * Return the terms from the Semantic Enhancement Server matching the search term
   *
   * @param query
   *          The search term
   * @return collection of matching termssorted alphabetically
   * @throws SESException
   *           SES exception
   */
  public Collection<Term> sortedSearch(String query) throws SESException {
    return sortTerms(search(query).values());
  }

  /**
   * Return the minimal amount of data for the terms - just the name and id will be populated
   *
   * @param ids
   *          Array of IDs
   * @return minimally populated terms in a map keyed by term id
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getTermDetails(String[] ids) throws SESException {
    return getTermDetails(ids, DetailLevel.MINIMAL, null);
  }

  /**
   * Return the minimal amount of data for one term - just the name and id will be populated
   *
   * @param id
   *          The ID for which you wish details to be returned
   * @return minimally populated term
   * @throws SESException
   *           SES exception
   * @throws NoSuchTermException
   *           Exception if no such term
   */
  public Term getTermDetails(String id) throws SESException, NoSuchTermException {
    return getTermDetails(id, DetailLevel.MINIMAL);
  }

  /**
   * Details of one term identified by its URI. *
   *
   * @param uri
   *          The URI of the term you wish to retrieve
   * @return minimally populated term
   * @throws SESException
   *           SES exception
   * @throws NoSuchTermException
   *           Exception if no such term
   */
  public Term getTermDetailsByURI(String uri) throws SESException, NoSuchTermException {
    SESFilter sesFilter = new SESFilter();
    sesFilter.setUris(new String[] { uri });
    Map<String, Term> terms = getAllTerms(sesFilter);
    if ((terms == null) || (terms.size() == 0)) {
      return null;
    }

    if (terms.size() > 1) {
      throw new SESException("More than one term has the URI " + uri);
    }
    return terms.values().toArray(new Term[0])[0];
  }

  public Map<String, Term> getTermDetailsByName(String name)
      throws SESException, NoSuchTermException {
    return getTermDetailsByName(name, null);
  }

  public Map<String, Term> getTermDetailsByName(String name, SESFilter sesFilter)
      throws SESException, NoSuchTermException {
    logger.info("getTermDetails - entry");
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=term");
      query.append("&term=" + URLEncoder.encode(name, "UTF8"));
      query.append(getLanguageChoice());

      query.append(getFilterString(sesFilter));

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.debug("getTermDetails - exit");
    if (semaphore.getTerms() != null) {
      return semaphore.getTerms().getTerms();
    }
    return new HashMap<>();
  }

  /**
   * Return the full details of the selected terms
   *
   * @param ids
   *          Array of IDs
   * @param detailLevel
   *          - the extent to which the term details should be populated
   * @return the terms matching the supplied ids
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getTermDetails(String[] ids, DetailLevel detailLevel)
      throws SESException {
    return getTermDetails(ids, detailLevel, null);
  }

  /**
   * Return the full details of the selected terms
   *
   * @param ids
   *          Array of IDs
   * @param detailLevel
   *          - the extent to which the term details should be populated
   * @param sesFilter
   *          Any SES filter to apply
   * @return the terms matching the supplied ids
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getTermDetails(String[] ids, DetailLevel detailLevel,
      SESFilter sesFilter) throws SESException {

    logger.info("getTermDetails - entry");

    List<URL> urls = new ArrayList<>();
    URL url = null;

    try {
      StringBuffer basepath = new StringBuffer();
      basepath.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      basepath.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      if (detailLevel == DetailLevel.FULL) {
        basepath.append("&service=term");
      } else if (detailLevel == DetailLevel.MINIMAL) {
        basepath.append("&service=termlite");
      } else {
        logger.warn("Invalid detail level requested");
      }
      basepath.append(getLanguageChoice());
      basepath.append(getFilterString(sesFilter));
      basepath.append("&id=");
      String sep = "";
      StringBuffer path = new StringBuffer(basepath);
      for (String id : ids) {
        String thisPath = this.getPath();
        Objects.requireNonNull(thisPath);
        if (thisPath.length() + path.length() + id.length() > 2048) {
          url = getURLImpl(path.toString());
          if (logger.isDebugEnabled()) {
            logger.debug("URL: " + url.toExternalForm());
          }
          urls.add(url);
          path = new StringBuffer(basepath);
        }
        path.append(sep + URLEncoder.encode(id, "UTF8"));
        sep = ",";
      }
      url = getURLImpl(path.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
      urls.add(url);

    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Map<String, Term> termdetails = new HashMap<>();

    for (URL u : urls) {
      Semaphore semaphore = getSemaphore(u);

      logger.debug("getTermDetails - exit");
      if (semaphore.getTerms() != null) {
        termdetails.putAll(semaphore.getTerms().getTerms());
      }
    }

    // Go through and fill in the gaps that might have been left by missing terms
    // Unless of course we are filtering the results, in which case the missing
    // terms
    // are the ones we don't want to see...
    if (sesFilter == null) {
      for (String id : ids) {
        if (!termdetails.containsKey(id)) {
          termdetails.put(id, null);
        }
      }
    }
    return termdetails;
  }

  /**
   * Sends requests which updates documents counter for term
   *
   * @param id
   *          ID of term to update
   * @param newValue
   *          The new count value for the term
   * @throws SESException
   *           SES exception
   */
  public void updateFrequency(String id, Integer newValue) throws SESException {
    logger.info("updateFrequency - id: '" + id + "'");

    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=updatefreq");
      query.append("&id=" + URLEncoder.encode(id, "UTF8"));
      query.append(getLanguageChoice());

      query.append("&new_value=" + URLEncoder.encode(newValue.toString(), "UTF8"));

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    getSemaphore(url);
    return;
  }

  /**
   * Sends request which increases popularity counter for term
   *
   * @param id
   *          ID of term to update
   * @throws SESException
   *           SES exception
   */
  public void increasePopularity(String id) throws SESException {
    logger.info("increasePopularity - id: '" + id + "'");

    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=increase_popularity");
      query.append("&id=" + URLEncoder.encode(id, "UTF8"));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    getSemaphore(url);
    return;
  }

  /**
   * Return details of the term with the selected id. If there is no such term, null will be
   * returned
   *
   * @param id
   *          ID of term to return details for
   * @param detailLevel
   *          Level of detail for the results
   * @return the term matching the supplied id, or null if no such term exists
   * @throws SESException
   *           SES exception
   * @throws NoSuchTermException
   *           "no such term" exception can be thrown
   */
  public Term getTermDetails(String id, DetailLevel detailLevel)
      throws SESException, NoSuchTermException {
    return getTermDetails(id, detailLevel, null);
  }

  /**
   * Return details of the term with the selected id. If there is no such term, null will be
   * returned
   *
   * @param id
   *          ID of the term to return details for
   * @param detailLevel
   *          Level of detail for the results
   * @param sesFilter
   *          Any SES filter to apply
   * @return the term matching the supplied id, or null if no such term exists
   * @throws SESException
   *           SES exception
   * @throws NoSuchTermException
   *           "no such term" exception can be thrown
   */
  public Term getTermDetails(String id, DetailLevel detailLevel, SESFilter sesFilter)
      throws SESException, NoSuchTermException {

    logger.info("getTermDetails - entry: '" + id + "'");

    Map<String, Term> termMap = getTermDetails(new String[] { id }, detailLevel, sesFilter);

    logger.info("getTermDetails - exit: '" + termMap.get(id) + "'");
    return termMap.get(id);
  }

  /**
   * Return the terms (in brief) that match the supplied prefix
   *
   * @param prefix
   *          Prefix text for terms
   * @return all matching term hints
   * @throws SESException
   *           SES exception
   */
  public Map<String, TermHint> getTermHints(String prefix) throws SESException {
    SESFilter sesFilter = new SESFilter();
    return getTermHints(prefix, sesFilter);
  }

  /**
   * Return the terms (in brief) that match the supplied prefix
   *
   * @param prefix
   *          Prefix text for terms
   * @param minDocs
   *          the minimum frequency for returned terms
   * @return all matching term hints
   * @throws SESException
   *           SES exception
   */
  @Deprecated
  public Map<String, TermHint> getTermHints(String prefix, int minDocs) throws SESException {
    logger.info("getTermHints - entry: '" + prefix + "'");

    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&mindocs=" + minDocs);
      query.append("&service=PREFIX");
      query.append("&term_prefix=" + URLEncoder.encode(prefix, "UTF8"));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.info("getTermHints - exit");
    if (semaphore.getTermHints() != null) {
      return semaphore.getTermHints().getTermHints();
    }
    return new HashMap<>();
  }

  /**
   * Return the terms (in brief) that match the supplied prefix
   *
   * @param prefix
   *          Prefix text for terms
   * @param sesFilter
   *          the filter that should be applied to the results
   * @return all matching term hints
   * @throws SESException
   *           SES exception
   */
  public Map<String, TermHint> getTermHints(String prefix, SESFilter sesFilter)
      throws SESException {
    logger.info("getTermHints - entry: '" + prefix + "'");

    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append(getFilterString(sesFilter));
      query.append("&service=PREFIX");
      query.append("&term_prefix=" + URLEncoder.encode(prefix, "UTF8"));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.info("getTermHints - exit");
    if (semaphore.getTermHints() != null) {
      return semaphore.getTermHints().getTermHints();
    }
    return new HashMap<>();
  }

  /**
   * Return the top level terms
   *
   * @return the set of terms
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> browse() throws SESException {
    return browse(null);
  }

  /**
   * Return the terms related to the presented term id
   *
   * @param id
   *          The term ID
   * @return the terms returned by the browse command
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> browse(String id) throws SESException {
    return browse(id, null);
  }

  /**
   * Return the terms related to the presented term id
   *
   * @param id
   *          The term ID
   * @param sesFilter
   *          Any SES filter
   * @return the terms returned by the browse command
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> browse(String id, SESFilter sesFilter) throws SESException {
    logger.info("browse - entry: " + id);
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=browse");
      if (id != null) {
        query.append("&id=" + URLEncoder.encode(id, "UTF8"));
      } else {
        query.append("&filter_hierarchy=false");
      }
      query.append(getFilterString(sesFilter));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    Map<String, Term> terms = semaphore.getTerms().getTerms();
    if (terms == null) {
      terms = new HashMap<>();
    }

    if (semaphore.getBrowseTerm() != null) {
      terms.put(semaphore.getBrowseTerm().getId().getValue(), semaphore.getBrowseTerm());
    }
    logger.debug("browse - exit");
    return terms;
  }

  private URL getURLImpl(String query) throws SESException {

    if (this.getUrl() != null) {
      try {
        return new URL(this.getUrl() + query);
      } catch (MalformedURLException e) {
        throw new SESException(String.format("Malformd URL: '%s'", this.getUrl()));
      }
    } else {
      try {
        return new URL(getProtocol(), getHost(), getPort(), getPath() + query);
      } catch (MalformedURLException e) {
        throw new SESException(String.format("Malformd URL: '%s' '%s' %d '%s' '%s'", getProtocol(),
            getHost(), getPort(), getPath(), query));
      }
    }
  }

  /**
   * Return the A to Z terms for the supplied prefix
   *
   * @param prefix
   *          The letter/number (prefix) or "all"
   * @return the terms marked as use for A to Z starting with the supplied prefix
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAtoZTerms(String prefix) throws SESException {
    return getAtoZTerms(prefix, null);
  }

  /**
   * Return the A to Z terms for the supplied prefix
   *
   * @param prefix
   *          The letter/number (prefix) or "all"
   * @param sesFilter
   *          The SES filter
   * @return the terms marked as use for A to Z starting with the supplied prefix
   * @throws SESException
   *           SES exception
   */
  public Map<String, Term> getAtoZTerms(String prefix, SESFilter sesFilter) throws SESException {

    logger.info("getAtoZTerms - entry: " + prefix);
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?TBDB=" + URLEncoder.encode(getOntology(), "UTF8"));
      query.append("&template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=az");
      query.append("&az=" + URLEncoder.encode(prefix, "UTF8"));
      query.append(getFilterString(sesFilter));
      query.append(getLanguageChoice());

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.debug("getAtoZTerms - exit");
    if (semaphore.getTerms() != null) {
      return semaphore.getTerms().getTerms();
    }
    return new HashMap<>();
  }

  public Collection<Model> listModels() throws SESException {
    logger.info("listModels - entry");
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=modelslist");

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.info("listModels - exit: " + semaphore.getModels().size());
    return semaphore.getModels();
  }

  /**
   * Return the A to Z terms for the supplied prefix sorted alphabetically (case insensitive)
   *
   * @param prefix
   *          The letter/number (prefix) or "all"
   * @return the terms marked as use for A to Z starting with the supplied prefix, sorted
   *         alphabetically
   * @throws SESException
   *           SES exception
   */
  public Collection<Term> getSortedAtoZTerms(String prefix) throws SESException {
    Map<String, Term> termMap = getAtoZTerms(prefix);
    return sortTerms(termMap.values());
  }

  public VersionInfo getVersion() throws SESException {
    logger.info("getVersion - entry");
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=versions");

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.info("getVersion - exit: " + semaphore.getModels().size());
    return semaphore.getVersionInfo();

  }

  public OMStructure getStructure() throws SESException {
    logger.info("getOMStructure - entry");

    StringBuffer query = new StringBuffer();
    query.append("/" + getOntology());
    if (getLanguage() != null) {
      query.append("/" + getLanguage());
    }

    Semaphore semaphore = getSemaphore(getURLImpl(query.toString()));

    logger.info("getOMStructure - exit");
    return semaphore.getOmStructure();
  }

  public StatisticsInfo getStatistics() throws SESException {
    logger.info("getStatistics - entry");
    URL url = null;

    try {
      StringBuffer query = new StringBuffer();
      query.append("?template=" + URLEncoder.encode(getTemplate(), "UTF8"));
      query.append("&service=stats");

      url = getURLImpl(query.toString());
      if (logger.isDebugEnabled()) {
        logger.debug("URL: " + url.toExternalForm());
      }
    } catch (UnsupportedEncodingException e) {
      throw new SESException("UnsupportedEncodingException: " + e.getMessage());
    }

    Semaphore semaphore = getSemaphore(url);

    logger.info("getStatistics - exit");
    return semaphore.getStatisticsInfo();
  }

  private Collection<Term> sortTerms(Collection<Term> termsToSort) {
    Collection<Term> treeSet = new TreeSet<>(new AlphabeticalTermComparator());
    treeSet.addAll(termsToSort);
    return treeSet;
  }

  protected void initHttpClient() throws NoSuchAlgorithmException, KeyManagementException {

    if (this.httpClient == null) {
      Builder requestConfigBuilder = RequestConfig.copy(RequestConfig.DEFAULT)
          .setSocketTimeout(getSocketTimeoutMS()).setConnectTimeout(getConnectionTimeoutMS())
          .setConnectionRequestTimeout(getConnectionTimeoutMS());
      if ((getProxyHost() != null) && (getProxyHost().length() > 0) && (getProxyPort() > 0)) {
        HttpHost proxy = new HttpHost(getProxyHost(), getProxyPort(), "http");
        requestConfigBuilder.setProxy(proxy);
      }
      RequestConfig requestConfig = requestConfigBuilder.build();

      SSLContextBuilder builder = new SSLContextBuilder();
      SSLConnectionSocketFactory sslsf =
          new SSLConnectionSocketFactory(builder.build(), new DefaultHostnameVerifier());

      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
      cm.setMaxTotal(this.maxConnections);
      cm.setDefaultMaxPerRoute(this.maxConnections);

      this.httpClient = HttpClients.custom().setConnectionManager(cm)
          .setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf).build();
    }
  }

  protected Semaphore getSemaphore(URL url) throws SESException {
    if (logger.isInfoEnabled()) {
      logger.info("getSemaphore - entry: '" + url.toExternalForm() + "'");
    }
    Semaphore semaphore = null;
    HttpGet httpGet = null;

    try {

      initHttpClient();

      if (logger.isDebugEnabled()) {
        logger.debug("About to make HTTP request: " + url.toExternalForm());
      }

      httpGet = new HttpGet(url.toExternalForm());
      if (getApiToken() != null) {
        httpGet.addHeader("Authorization", getApiToken());
      }

      HttpResponse response = httpClient.execute(httpGet);

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request complete: " + url.toExternalForm());
      }

      if (response == null) {
        throw new SESException("Null response from http client: " + url.toExternalForm());
      }
      if (response.getStatusLine() == null) {
        throw new SESException("Null status line from http client: " + url.toExternalForm());
      }

      int statusCode = response.getStatusLine().getStatusCode();

      if (logger.isDebugEnabled()) {
        logger.debug("HTTP request complete: " + statusCode + " " + url.toExternalForm());
      }

      if (statusCode != HttpStatus.SC_OK) {
        throw new SESException(
            "Status code " + statusCode + " received from URL: " + url.toExternalForm());
      }

      HttpEntity entity = response.getEntity();

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      entity.writeTo(byteArrayOutputStream);
      if (saveFile != null) {
        saveRequestAndResponse(saveFile, url, byteArrayOutputStream.toByteArray());
      }

      InputSource inputSource =
          new InputSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

      // Read the semaphore object from the returned XML
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setValidating(false);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document xmlDocument = documentBuilder.parse(inputSource);

      semaphore = new Semaphore(xmlDocument.getDocumentElement());
    } catch (ParserConfigurationException e) {
      throw new SESException("ParserConfigurationException: " + e.getMessage());
    } catch (IOException e) {
      throw new SESException("IOException: " + e.getMessage());
    } catch (SAXException e) {
      throw new SESException("SAXException: " + e.getMessage());
    } catch (KeyManagementException e) {
      throw new SESException("KeyManagementException: " + e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      throw new SESException("NoSuchAlgorithmException: " + e.getMessage());
    } finally {
      if (logger.isDebugEnabled()) {
        logger.debug("getSemaphore - about to abort the connection " + url.toExternalForm());
      }
      if (httpGet != null) {
        httpGet.abort();
      }
      if (logger.isDebugEnabled()) {
        logger.debug("getSemaphore - about to release connection " + url.toExternalForm());
      }
      if (logger.isDebugEnabled()) {
        logger.debug("getSemaphore - connection released " + url.toExternalForm());
      }
    }

    if (logger.isInfoEnabled()) {
      logger.info("getSemaphore - exit: '" + url.toExternalForm() + "'");
    }
    return semaphore;
  }

  private void saveRequestAndResponse(File saveFile, URL url, byte[] response) {

    try (FileOutputStream fileOutputStream = new FileOutputStream(saveFile)) {
      fileOutputStream.write(url.toExternalForm().getBytes("UTF-8"));
      fileOutputStream.write("\n\n".getBytes());
      fileOutputStream.write(response);
    } catch (Exception e) {
      logger.warn(String.format("{} thrown saving request: {}", e.getClass().getSimpleName(),
          e.getMessage()));
    }
  }

  private class AlphabeticalTermComparator implements Comparator<Term> {

    @Override
    public int compare(Term term1, Term term2) {
      if (term1 == null) {
        return 1;
      }
      if (term2 == null) {
        return -1;
      }

      if (term1.getName() == null) {
        return 1;
      }
      if (term2.getName() == null) {
        return -1;
      }

      if (term1.getName().getValue() == null) {
        return 1;
      }
      if (term2.getName().getValue() == null) {
        return -1;
      }

      return term1.getName().getValue().toLowerCase()
          .compareTo(term2.getName().getValue().toLowerCase());
    }

  }

}
