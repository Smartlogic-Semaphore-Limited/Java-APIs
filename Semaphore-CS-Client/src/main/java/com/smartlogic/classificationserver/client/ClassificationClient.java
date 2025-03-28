package com.smartlogic.classificationserver.client;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smartlogic.classificationserver.client.utils.XMLFeatureConst;

/**
 * General purpose client for the classification server
 *
 * @author Smartlogic Semaphore
 *
 */
public class ClassificationClient implements AutoCloseable {
  public static final Logger logger = LoggerFactory.getLogger(ClassificationClient.class);

  public static final int SC_INTERNAL_SERVER_ERROR = 500;
  public static final int SC_OK = 200;
  public static final String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";

  private HttpClient httpClient;

  protected synchronized void initHttpClient() {
    if (this.httpClient == null) {
      HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
      if (getClassificationConfiguration().getConnectionTimeoutMS() > 0)
        httpClientBuilder.connectTimeout(Duration.ofMillis(getClassificationConfiguration().getConnectionTimeoutMS()));

      /* first check the proxyURL and if that's not set, then check the proxyHost and proxyPort */
      if (proxyURL != null && !proxyURL.isEmpty()) {
        try {
          URL url = new URL(proxyURL);
          httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(url.getHost(), url.getPort())));
        } catch (MalformedURLException e) {
          throw new RuntimeException("Invalid proxy URL: " + proxyURL);
        }
      } else if (proxyHost != null && proxyPort != 0) {
          httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
      }
      this.httpClient = httpClientBuilder.build();
    }
  }

  /* Methods that are classification requests */

  /**
   * Determine the version information as generated by Classification Server
   *
   * @return The version string
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public String getVersion() throws ClassificationException {
    logger.debug("getVersion - entry");

    String commandString = getCommandXML("version", null);
    CSVersion version = new CSVersion(sendPostRequest(commandString, null));
    return version.getVersion();
  }

  /**
   * Return the rulebase classes that are currently configured on the classification server instance
   *
   * @return List of rulebases classes
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public Collection<RulebaseClass> getRulebaseClasses() throws ClassificationException {
    String commandString = getCommandXML("listrulenetclasses", null);
    RulebaseClassSet rulebaseClassSet = new RulebaseClassSet(sendPostRequest(commandString, null));
    return rulebaseClassSet.getRulebaseClasses();
  }

  /**
   * Clear out a publish set so that new pack files can be uploaded. Until the publish set is
   * committed, this will have no effect on what is currently live
   *
   * @param publishSetName
   *          - the name of the set to be created
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void clearPublishSet(String publishSetName) throws ClassificationException {
    String commandString = getCommandXML("publish_set_init", publishSetName);
    sendPostRequest(commandString, null);
  }

  /**
   * Upload the collection of pakfiles to the named publish set Until the publish set is committed,
   * this will have no effect on what is currently live
   *
   * @param publishSetName
   *          - the name of the publish set to which the pak files should be added
   * @param pakFiles
   *          - the pak files to upload
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void sendPakfiles(String publishSetName, Collection<File> pakFiles)
      throws ClassificationException {
    for (File pakFile : pakFiles) {
      sendPakFile(publishSetName, pakFile);
    }
  }

  /**
   * Upload the pakfile to the named publish set Until the publish set is committed, this will have
   * no effect on what is currently live
   *
   * @param publishSetName
   *          - the name of the publish set to which the pak file should be added
   * @param pakFile
   *          - the pak file to upload
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void sendPakFile(String publishSetName, File pakFile) throws ClassificationException {
    String commandString = getCommandXML("publish_set_add", publishSetName);
    sendPostRequest(commandString, pakFile);
  }

  /**
   * Instruct that a particular publish set should become live This command will affect the
   * classification result
   *
   * @param publishSetName
   *          - the name of the publish set to commit
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void commitPublishSet(String publishSetName) throws ClassificationException {
    String commandString = getCommandXML("publish_set", publishSetName);
    sendPostRequest(commandString, null);
  }

  /**
   * Deactivate (remove) a particular publish set from the classification servers rulebase set.
   *
   * @param publishSetName
   *          - the name of the publish set to deactivate
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void deactivatePublishSet(String publishSetName) throws ClassificationException {
    String commandString = getCommandXML("publish_set_deactivate", publishSetName);
    sendPostRequest(commandString, null);
  }

  /**
   * Deletes a deactivated publish set from classification server.
   *
   * @param publishSetName
   *          - the name of the deactivated publish set to delete
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public void deletePublishSet(String publishSetName) throws ClassificationException {
    String commandString = getCommandXML("publish_set_delete", publishSetName);
    sendPostRequest(commandString, null);
  }

  /**
   * Return the information that CS makes available.
   *
   * @return Classification Server information
   * @throws ClassificationException
   *           - There has been a connectivity issue
   */
  public CSInfo getInfo() throws ClassificationException {
    logger.debug("getInfo");

    CSInfo csInfo = new CSInfo(sendPostRequest(getCommandXML("info", null), null));
    return csInfo;
  }

  /**
   * Return the list of languages available on the cs instance
   *
   * @return List of languages
   * @throws ClassificationException
   *           Classification exception
   */
  public Collection<Language> getLanguages() throws ClassificationException {
    logger.debug("getLanguages - entry");

    LanguageSet langSet =
        new LanguageSet(sendPostRequest(getCommandXML("listlanguages", null), null));
    return langSet.getLanguages();
  }

  /**
   * Return the map of default parameter values
   *
   * @return Map of default parameter values
   * @throws ClassificationException
   *           Classification exception
   */
  public Map<String, Parameter> getDefaults() throws ClassificationException {
    logger.debug("getDefaults - entry");
    Defaults defaults =
        new Defaults(sendPostRequest(getCommandXML("getparameterdefaults", null), null));
    return defaults.getDefaults();
  }

  /**
   * Return the status of the classification server instance
   *
   * @return A classification status object
   * @throws ClassificationException
   *           Classification exception
   */
  @Deprecated // This response appears pretty useless
  public ClassificationServerStatus status() throws ClassificationException {
    if (logger.isDebugEnabled()) {
      logger.debug("status - entry");
    }

    ClassificationServerStatus status =
        new ClassificationServerStatus(sendPostRequest(getCommandXML("stats", null), null));
    return status;

  }

  /* Plain getters and setters for this object */

  private ClassificationConfiguration classificationConfiguration;

  /**
   * Get the configuration of the classification server
   *
   * @return The configuration
   */
  public ClassificationConfiguration getClassificationConfiguration() {
    return classificationConfiguration;
  }

  /**
   * Set the configuration of the classification server
   *
   * @param classificationConfiguration
   *          The configuration to use
   */
  public void setClassificationConfiguration(
      ClassificationConfiguration classificationConfiguration) {
    this.classificationConfiguration = classificationConfiguration;
  }

  private UUID auditUUID = null;

  /**
   * Return the UUID object used to tag the request
   *
   * @return The UUID object
   */
  public UUID getAuditUUID() {
    return auditUUID;
  }

  /**
   * Set a UUID object that will be used to tag the request. If configured, this will be stored in
   * the classification server log and so can be used for auditing purposes.
   *
   * @param auditGUID
   *          The audit GUID to use
   */
  public void setAuditUUID(UUID auditGUID) {
    this.auditUUID = auditGUID;
  }

  private String proxyHost = null;

  /**
   * The name of the proxy host in use.
   *
   * @return The proxy host. Null if no proxy is in use (the default)
   */
  @Deprecated
  public String getProxyHost() {
    return proxyHost;
  }

  /**
   * Set the proxy host to be used for all requests
   *
   * @param proxyHost
   *          The proxy host to use
   */
  @Deprecated
  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  private int proxyPort;

  /**
   * The port of the proxy being used
   *
   * @return The port number
   */
  @Deprecated
  public int getProxyPort() {
    return proxyPort;
  }

  /**
   * The port of the proxy being used
   *
   * @param proxyPort
   *          The port number to use
   */
  @Deprecated
  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  private String proxyURL;

  private String getProxyURL() {
    if (proxyURL == null) {
      if ((proxyHost != null) && (proxyPort != 0)) {
        proxyURL = "http://" + proxyHost + ":" + proxyPort;
      }
    }
    return proxyURL;
  }

  public void setProxyURL(String proxyURL) {
    this.proxyURL = proxyURL;
  }

  /* Classification requests */

  /**
   * Classify the supplied file
   *
   * @param inputFile
   *          The input file to classify
   * @param fileType
   *          File type of "inputFile". If the file type is not supplied (i.e. is null) then it will
   *          be guessed by classification server.
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(File inputFile, String fileType)
      throws ClassificationException {
    return new Result(getStructuredDocument(inputFile, fileType));
  }

  public Document getStructuredDocument(File inputFile, String fileType)
      throws ClassificationException {
    return getStructuredDocument(inputFile, fileType, null, null);
  }

  /**
   * Classify the supplied title and body as if they were a document
   *
   * @param fileName
   *          The file name of the document to classify
   * @param title
   *          The document title
   * @param body
   *          The document body
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(FileName fileName, Body body, Title title)
      throws ClassificationException {
    return new Result(getStructuredDocument(fileName, body, title));
  }

  public Document getStructuredDocument(FileName fileName, Body body, Title title)
      throws ClassificationException {
    return getStructuredDocument(fileName, body, title, null);
  }

  /**
   * Classify the supplied title and body as if they were a document
   *
   * @param title
   *          The document title
   * @param body
   *          The document body
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(Body body, Title title) throws ClassificationException {
    return new Result(getStructuredDocument(body, title));
  }

  public Document getStructuredDocument(Body body, Title title) throws ClassificationException {
    return getStructuredDocument(null, body, title, null);
  }

  /**
   * Classify the supplied title and body as if they were a document
   *
   * @param fileName
   *          The file name of the document to classify
   * @param body
   *          The document body
   * @param title
   *          The document title
   * @param metadata
   *          Map containing metadata
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(FileName fileName, Body body, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return new Result(getStructuredDocument(fileName, body, title, metadata));
  }

  public Document  getStructuredDocument(FileName fileName, Body body, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    logger.debug("Treating document: '{}'", title.getValue());

    // If there is no body, then don't bother attempting to classify the document
    if ((body == null) || (body.getValue() == null) || (body.getValue().trim().length() == 0)) {
      return getBlankStructuredDocument();
    }

    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();

    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    addByteArray(bodyPublisher, body, fileName);
    return XMLReader.getDocument(getClassifications(bodyPublisher));
  }

  public byte[] getClassificationServerResponse(FileName filename, Body body, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    logger.debug("Treating document: '" + title.getValue() + "'");

    // If there is no body, then don't bother attempting to classify the
    // document
    if ((body == null) || (body.getValue() == null) || (body.getValue().trim().length() == 0)) {
      return new byte[0];
    }

    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();

    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    addByteArray(bodyPublisher, body, filename);
    return getClassificationServerResponse(bodyPublisher);
  }

  /**
   * Classify the supplied title and body as if they were a document
   *
   * @param body
   *          The document body
   * @param title
   *          The document title
   * @param metadata
   *          Map containing metadata
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(Body body, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return new Result(getStructuredDocument(body, title, metadata));
  }

  public Document getStructuredDocument(Body body, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return getStructuredDocument(null, body, title, metadata);
  }

  /**
   * Classify the supplied url
   *
   * @param url
   *          The URL to classify
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(URL url) throws ClassificationException {
    return new Result(getStructuredDocument(url));
  }

  public Document getStructuredDocument(URL url) throws ClassificationException {
    return getStructuredDocument(url, null, null);
  }

  /**
   * Classify the supplied url with the extra metadata
   *
   * @param url
   *          The URL to classify
   * @param title
   *          The document title
   * @param metadata
   *          Map containing metadata
   * @return the classifications as returned by classification server.
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(URL url, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return new Result(getStructuredDocument(url, title, metadata));
  }

  public Document getStructuredDocument(URL url, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();
    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    bodyPublisher.addPart("path", url.toExternalForm());
    return XMLReader.getDocument(getClassifications(bodyPublisher));
  }

  private final static SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

  /**
   * Return the classification records for all requests between the two supplied dates
   *
   * @param startTime
   *          The earliest possible date for returned results
   * @param endTime
   *          The latest possible date for returned results
   * @return One record for each document classified in that date range
   * @throws ClassificationException
   *           Classification exception
   */
  public Collection<ClassificationRecord> getClassificationHistory(Date startTime, Date endTime)
      throws ClassificationException {
    logger.info("getClassificationHistory - entry");

    MultipartFormDataBodyPublisher bodyPublisher = new MultipartFormDataBodyPublisher();
    bodyPublisher.addPart("start_time", simpleDateFormat.format(startTime));
    bodyPublisher.addPart("finish_time", simpleDateFormat.format(endTime));
    bodyPublisher.addPart("operation", "getclassificationhistory");

    ClassificationHistory classificationHistory =
        new ClassificationHistory(getClassificationServerResponse(bodyPublisher));
    return classificationHistory.getClassificationRecords();
  }

  public byte[] getClassifiedBytes(Body body, Title title, Map<String, Collection<String>> metadata)
      throws ClassificationException {
    logger.debug("Treating document: '" + title.getValue() + "'");

    // If there is no body, then don't bother attempting to classify the document
    if ((body == null) || (body.getValue() == null) || (body.getValue().trim().length() == 0)) {
      return new byte[0];
    }

    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();

    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    addByteArray(bodyPublisher, body, null);
    return getClassifications(bodyPublisher);
  }

  public byte[] getClassifiedBytes(URL url, Title title, Map<String, Collection<String>> metadata)
      throws ClassificationException {
    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();
    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    bodyPublisher.addPart("path", url.toExternalForm());
    return getClassifications(bodyPublisher);
  }

  public byte[] getClassificationServerResponse(Body body, Title title)
      throws ClassificationException {
    return getClassificationServerResponse(null, body, title, null);
  }

  /**
   * Return directly the output from classification server with no analysis
   *
   * @param inputFile
   *          The input file to classify
   * @param fileType
   *          File type of "inputFile". If the file type is not supplied (i.e. is null) then it will
   *          be guessed by classification server.
   * @return The classification server response
   * @throws ClassificationException
   *           Classification exception
   */
  public byte[] getClassificationServerResponse(File inputFile, String fileType)
      throws ClassificationException {
    return getClassificationServerResponse(inputFile, fileType, null, null);
  }

  /**
   * Return in a structured form the output of the classification process
   *
   * @param data
   *          Data to classify
   * @param fileName
   *          A string containing the name of the file to classify
   * @return The structured result of the classification
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(byte[] data, String fileName) throws ClassificationException {
    return new Result(getStructuredDocument(data, fileName));
  }

  public Document getStructuredDocument(byte[] data, String fileName)
      throws ClassificationException {
    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();

    if ((data == null) || (data.length == 0)) {
      return getBlankStructuredDocument();
    }

    addByteArray(bodyPublisher, data, fileName);

    return XMLReader.getDocument(getClassificationServerResponse(bodyPublisher));
  }

  /**
   * Return in a structured form the output of the classification process
   *
   * @param data
   *          Data to classify
   * @param fileName
   *          A string containing the name of the file to classify
   * @param title
   *          The document title
   * @param metadata
   *          Map containing metadata
   * @return The structured result of the classification
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(byte[] data, String fileName, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return new Result(getStructuredDocument(data, fileName, title, metadata));
  }

  public Document getStructuredDocument(byte[] data, String fileName, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    logger.debug("Treating file: '" + fileName + "'");

    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();

    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    addByteArray(bodyPublisher, data, fileName);

    return XMLReader.getDocument(getClassificationServerResponse(bodyPublisher));
  }

  /**
   * Return in a structured form the output of the classification process
   *
   * @param inputFile
   *          The input file to classify
   * @param fileType
   *          File type of "inputFile". If the file type is not supplied (i.e. is null) then it will
   *          be guessed by classification server.
   * @param title
   *          The document title
   * @param metadata
   *          Map containing metadata
   * @return The structured result of the classification
   * @throws ClassificationException
   *           Classification exception
   */
  public Result getClassifiedDocument(File inputFile, String fileType, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    return new Result(getStructuredDocument(inputFile, fileType, title, metadata));
  }

  public Document getStructuredDocument(File inputFile, String fileType, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {

    MultipartFormDataBodyPublisher bodyPublisher = getDefaultParts();
    addTitle(bodyPublisher, title);
    addMetadata(bodyPublisher, metadata);
    addFile(bodyPublisher, inputFile, fileType);

    return XMLReader.getDocument(getClassificationServerResponse(bodyPublisher));
  }

  private void addTitle(MultipartFormDataBodyPublisher publisher, Title title) {
    if ((title != null) && (title.getValue() != null) && (!title.getValue().isEmpty())) {
      publisher.addPart(title.getParameterName(), title.getValue());
    }
  }

  private void addByteArray(MultipartFormDataBodyPublisher publisher, Body body, FileName filename) {
    if (filename == null) {
      publisher.addPart(body.getParameterName(), body.getValue());
    } else {
      Supplier<InputStream> supplier = () -> new ByteArrayInputStream(body.getValue().getBytes(StandardCharsets.UTF_8));
      publisher.addPart(body.getParameterName(), supplier, filename.getValue(), MIME_TYPE_APPLICATION_OCTET_STREAM);
    }
  }

  private void addByteArray(MultipartFormDataBodyPublisher publisher, byte[] data, String fileName) {
      Supplier<InputStream> supplier = () -> new ByteArrayInputStream(data);
      publisher.addPart("UploadFile", supplier, fileName, MIME_TYPE_APPLICATION_OCTET_STREAM);
  }

  private void addFile(MultipartFormDataBodyPublisher publisher, File inputFile, String fileType)
      throws ClassificationException {
    if (inputFile == null) {
      throw new ClassificationException("Null input file provided");
    }
    if (!inputFile.exists()) {
      throw new ClassificationException("Input file not found: " + inputFile.getAbsolutePath());
    }

    try {
      byte[] fileBytes = Files.readAllBytes(inputFile.toPath());
      addFileContent(publisher, fileBytes, inputFile.getName());
    } catch (IOException ioe) {
      throw new ClassificationException("Error reading file: " + inputFile.getAbsolutePath(), ioe);
    }
  }

  private void addFileContent(MultipartFormDataBodyPublisher publisher, byte[] fileContent, String fileName)
      throws ClassificationException {
    if (fileContent == null) {
      throw new ClassificationException("Null input file provided");
    }

    Supplier<InputStream> supplier = () -> new ByteArrayInputStream(fileContent);
    publisher.addPart("UploadFile", supplier, fileName, MIME_TYPE_APPLICATION_OCTET_STREAM);
  }

  private void addMetadata(MultipartFormDataBodyPublisher publisher,
                           Map<String, Collection<String>> metadata) {
    if (metadata != null) {
      for (String name : metadata.keySet()) {
        Collection<String> values = metadata.get(name);
        if (values != null) {
          int m = 0;
          for (String value : values) {
            if (m == 0) {
              publisher.addPart("meta_" + name, value);
            } else {
              publisher.addPart("meta_" + name + "__" + m, value);
            }
            m++;
          }
        }
      }
    }
  }

  private MultipartFormDataBodyPublisher getDefaultParts() {
    MultipartFormDataBodyPublisher multiPartBodyPublisher = new MultipartFormDataBodyPublisher();
    for (String parameterName : classificationConfiguration.getAdditionalParameters().keySet()) {
      String value = classificationConfiguration.getAdditionalParameters().get(parameterName);
      if ((value != null) && (!value.isEmpty())) {
        multiPartBodyPublisher.addPart(parameterName, value);
      }
    }
    if (classificationConfiguration.isSingleArticle()) {
      multiPartBodyPublisher.addPart("singlearticle", "true");
    }
    if (classificationConfiguration.isMultiArticle()) {
      multiPartBodyPublisher.addPart("multiarticle", "true");
    }
    if (classificationConfiguration.isFeedback()) {
      multiPartBodyPublisher.addPart("feedback", "true");
    }
    if (classificationConfiguration.isStylesheet()) {
      multiPartBodyPublisher.addPart("stylesheet", "true");
    }
    if (classificationConfiguration.isUseGeneratedKeys()) {
      multiPartBodyPublisher.addPart("use_generated_keys", "on");
    }
    if (classificationConfiguration.isReturnHashCode()) {
      multiPartBodyPublisher.addPart("return_hash", "true");
    }
    return multiPartBodyPublisher;
  }

  private byte[] getClassificationServerResponse(MultipartFormDataBodyPublisher publisher)
      throws ClassificationException {


    if (this.getAuditUUID() != null) {
      publisher.addPart("audit_tag", this.getAuditUUID().toString());
    }

    byte[] returnedData = sendPostRequest(publisher);

    logger.debug("getClassificationServerResponse - exit: " + returnedData.length);
    return returnedData;
  }

  private byte[] getClassifications(MultipartFormDataBodyPublisher publisher)
      throws ClassificationException {
    return getClassifications(publisher, null);
  }

  private byte[] getClassifications(MultipartFormDataBodyPublisher publisher, Map<String, String> outMeta)
      throws ClassificationException {
    byte[] returnedData = getClassificationServerResponse(publisher);
    if ((returnedData != null) && (outMeta != null)) {
      Result result = new Result(XMLReader.getDocument(returnedData));
      if (result.getMetadata() != null) {
        for (String meta : result.getMetadata().keySet()) {
          outMeta.put(meta, result.getMetadata().get(meta));
        }
      }
    }
    return returnedData;
  }

  public byte[] getClassificationServerResponse(File inputFile, String fileType, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    logger.debug("Treating file: '" + inputFile + "'");

    MultipartFormDataBodyPublisher multiPartBodyPublisher = getDefaultParts();
    addFile(multiPartBodyPublisher, inputFile, fileType);

    addTitle(multiPartBodyPublisher, title);
    addMetadata(multiPartBodyPublisher, metadata);
    return getClassificationServerResponse(multiPartBodyPublisher);
  }

  public byte[] getClassificationServerResponse(byte[] fileContent, String fileName, Title title,
      Map<String, Collection<String>> metadata) throws ClassificationException {
    logger.debug("Treating raw bytes: '" + title + "'");

    MultipartFormDataBodyPublisher multiPartBodyPublisher = getDefaultParts();
    addFileContent(multiPartBodyPublisher, fileContent, fileName);

    addTitle(multiPartBodyPublisher, title);
    addMetadata(multiPartBodyPublisher, metadata);
    return getClassificationServerResponse(multiPartBodyPublisher);
  }

  private DocumentBuilder documentBuilder = null;

  private String getCommandXML(String command, String publishSetName)
      throws ClassificationException {
    createDocumentBuilder();
    Document document = documentBuilder.newDocument();
    Element requestElement = document.createElement("request");
    requestElement.setAttribute("op", command);
    document.appendChild(requestElement);

    if (publishSetName != null) {
      Element publishSetElement = document.createElement("publish_set");
      publishSetElement.appendChild(document.createTextNode(publishSetName));
      requestElement.appendChild(publishSetElement);
    }

    StringWriter stringWriter = new StringWriter();
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      DOMSource source = new DOMSource(document);
      StreamResult destination = new StreamResult(stringWriter);
      transformer.transform(source, destination);
    } catch (TransformerException e) {
      throw new ClassificationException(
          String.format("TransformerException building CS command: %s %s - %s", command,
              publishSetName, e.getMessage()));
    }
    return stringWriter.toString();
  }

  private void createDocumentBuilder() {
    if (documentBuilder == null) {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      try {
        documentBuilderFactory.setFeature(XMLFeatureConst.LOAD_DTD_GRAMMAR, false);
        documentBuilderFactory.setFeature(XMLFeatureConst.LOAD_EXTERNAL_DTD, false);
        documentBuilderFactory.setFeature(XMLFeatureConst.EXTERNAL_GENERAL_ENTITIES, false);
        documentBuilderFactory.setFeature(XMLFeatureConst.EXTERNAL_PARAMETER_ENTITIES, false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        throw new RuntimeException("Failed to create XML document builder", e);
      }
    }
  }

  private byte[] sendPostRequest(String commandString, File pakFile)
      throws ClassificationException {

    initHttpClient();

    MultipartFormDataBodyPublisher multiPartBodyPublisher = new MultipartFormDataBodyPublisher();

    if (pakFile != null) {
      try {
        byte[] fileBytes = Files.readAllBytes(pakFile.toPath());
        addFileContent(multiPartBodyPublisher, fileBytes, pakFile.getName());
      } catch (IOException e) {
        throw new ClassificationException("Failed to read file: " + pakFile.getName(), e);
      }
    }

    multiPartBodyPublisher.addPart("XML_INPUT", commandString);

    return sendPostRequest(multiPartBodyPublisher);
  }

  private int clientPoolSize = 2;

  public int getClientPoolSize() {
    return clientPoolSize;
  }

  public void setClientPoolSize(int clientPoolSize) {
    this.clientPoolSize = clientPoolSize;
  }

  private synchronized void initialize() {
    initHttpClient();
  }

  @Override
  public void close() {
    // no cleanup now
  }

  private byte[] sendPostRequest(MultipartFormDataBodyPublisher publisher) throws ClassificationException {
    initialize();

    byte[] responseData;
    try {
      HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofByteArray(publisher.build());
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .setHeader("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
          .uri(new URI(classificationConfiguration.getUrl()))
          .POST(bp)
          ;
      if (classificationConfiguration.getSocketTimeoutMS() > 0) {
        requestBuilder.timeout(Duration.ofMillis(classificationConfiguration.getSocketTimeoutMS()));
      }
      addHeaders(requestBuilder);

      HttpRequest request = requestBuilder.build();

      HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response == null) {
        throw new ClassificationException(
            "Null response from http client: " + classificationConfiguration.getUrl());
      }

      int statusCode = response.statusCode();
      logger.debug("HTTP response code: {}", statusCode);

      try(InputStream responseInputStream = response.body()) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(responseInputStream, byteArrayOutputStream);
        responseData = byteArrayOutputStream.toByteArray();

        if (statusCode == SC_INTERNAL_SERVER_ERROR) {
          throw new ClassificationException(
              "Internal classification server error: " + new String(responseData, StandardCharsets.UTF_8));
        } else if (statusCode != SC_OK) {
          throw new ClassificationException("HttpStatus: " +
              statusCode +
              " received from classification server (" +
              classificationConfiguration.getUrl() +
              ") " +
              new String(responseData, StandardCharsets.UTF_8));
        }
      }

    } catch (IOException e) {
      throw new ClassificationException(
          "IOException talking to classification server: " + e.getMessage());
    } catch (InterruptedException e) {
      throw new ClassificationException(
          "InterruptedException talking to classification server: " + e.getMessage());
    } catch (URISyntaxException e) {
      throw new ClassificationException(
          "URISyntaxException talking to classification server: " + e.getMessage()
      );
    }

    return responseData;
  }

  private static Document blankDocument = null;

  private static Document getBlankStructuredDocument() throws ClassificationException {
    if (blankDocument == null) {
      blankDocument = XMLReader.getDocument(
          "<response><STRUCTUREDDOCUMENT/></response>".getBytes(StandardCharsets.UTF_8));
    }
    return blankDocument;
  }

  private void addHeaders(HttpRequest.Builder httpRequestBuilder) {
    if (classificationConfiguration.getApiToken() != null) {
      logger.trace("Adding authorization header: {}", classificationConfiguration.getApiToken());
      httpRequestBuilder.setHeader("Authorization", classificationConfiguration.getApiToken());
    }
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.getClass().getCanonicalName() + "\n");
    stringBuilder
        .append("  Host Name: '" + this.getClassificationConfiguration().getHostName() + "'\n");
    stringBuilder
        .append("  Host Path: '" + this.getClassificationConfiguration().getHostPath() + "'\n");
    stringBuilder
        .append("  Host Port: '" + this.getClassificationConfiguration().getHostPort() + "'\n");
    stringBuilder.append("  Connection Timeout MS: '" +
        this.getClassificationConfiguration().getConnectionTimeoutMS() +
        "'\n");
    stringBuilder.append("  Socket Timeout MS: '" +
        this.getClassificationConfiguration().getSocketTimeoutMS() +
        "'\n");
    stringBuilder
        .append("  Protocol: '" + this.getClassificationConfiguration().getProtocol() + "'\n");
    stringBuilder.append("  Proxy Host: '" + this.getProxyHost() + "'\n");
    stringBuilder.append("  Proxy Port: '" + this.getProxyPort() + "'\n");
    return stringBuilder.toString();
  }
}
