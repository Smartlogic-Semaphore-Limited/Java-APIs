package com.smartlogic.classificationserver.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General purpose client for the classification server
 *
 * @author Smartlogic Semaphore
 *
 */
public class ClassificationClient {
	public static Logger logger = LoggerFactory.getLogger(ClassificationClient.class);

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
	 * @param classificationConfiguration The configuration to use
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
	 * Set a UUID object that will be used to tag the request. If configured,
	 * this will be stored in the classification server log and so can be used
	 * for auditing purposes.
	 *
	 * @param auditGUID The audit GUID to use
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
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * Set the proxy host to be used for all requests
	 *
	 * @param proxyHost The proxy host to use
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	private int proxyPort;

	/**
	 * The port of the proxy being used
	 *
	 * @return The port number
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * The port of the proxy being used
	 *
	 * @param proxyPort The port number to use
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * Classify the supply binary array as if it were the contents of a file
	 *
	 * @param data The data to classify
	 * @param fileName String containing the name of the file
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyBinary(
			byte[] data, String fileName) throws ClassificationException {
		return classifyBinary(data, fileName, null, null);
	}

	/**
	 * Classify the supply binary array as if it were the contents of a file
	 *
	 * @param data The data to classify
	 * @param fileName String containing the name of the file
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyBinary(
			byte[] data, String fileName, Title title,
			Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating file: '" + fileName + "'");
		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
		addTitle(parts, title);
		addMetadata(parts, metadata);
		addByteArray(parts, data, fileName);

		return getClassifications(parts).getAllClassifications();
	}

	/**
	 * Classify the supplied file

	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @return The classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 * @deprecated Use getClassifiedDocument(File, fileType) instead
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyFile(
			File inputFile, String fileType) throws ClassificationException {
		return classifyFile(inputFile, fileType, null, null);
	}

	/**
	 * Classify the supplied file
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(File inputFile, String fileType)
			throws ClassificationException {
		return getClassifiedDocument(inputFile, fileType, null, null);
	}

	/**
	 * Classify the supplied file
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @param outMetadata Container for extracted metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyFile(
			File inputFile, String fileType, Map<String, String> outMetadata)
			throws ClassificationException {
		// add the filename as a original_uri meta
		Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
		List<String> l = new LinkedList<String>();
		l.add(inputFile.getName());
		metadata.put("ORIGINALURI", l);
		return classifyFile(inputFile, fileType, null, metadata, outMetadata);
	}

	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyFileWithMetadata(
			File inputFile, String fileType,
			Map<String, Collection<String>> inMetadata,
			Map<String, String> outMetadata) throws ClassificationException {
		return classifyFile(inputFile, fileType, null, inMetadata, outMetadata);
	}

	private String version = null;

	/**
	 * Return the version of Classification Server (as displayed in the Test
	 * Interface)
	 *
	 * @return The version
	 * @throws ClassificationException Classification exception
	 */
	public String getVersion() throws ClassificationException {
		logger.debug("getVersion");
		if (version == null) {
			Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
			parts.add(FormBodyPartBuilder.create("body",  getStringBody("squirmish")).build());
			getClassificationServerResponse(parts);
		}
		return version;
	}

	/**
	 * Return the information that CS makes available.
	 * @return Classification Server information
	 * @throws ClassificationException Classification exception
	 */
	public CSInfo getInfo() throws ClassificationException {
		logger.debug("getInfo");

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
		parts.add(FormBodyPartBuilder.create("operation", getStringBody("info")).build());
		CSInfo csInfo = new CSInfo(getClassificationServerResponse(parts));

		return csInfo;
	}


	/**
	 * Classify the supplied file
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyFile(
			File inputFile, String fileType, Title title,
			Map<String, Collection<String>> metadata)
			throws ClassificationException {
		return classifyFile(inputFile, fileType, title, metadata, null);
	}


	/**
	 * Classify the supplied file
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @param outMetadata Container populated with the extracted metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */

	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyFile(
			File inputFile, String fileType, Title title,
			Map<String, Collection<String>> metadata,
			Map<String, String> outMetadata) throws ClassificationException {
		logger.debug("Treating file: '" + inputFile + "'");

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
		addFile(parts, inputFile, fileType);
		addTitle(parts, title);
		addMetadata(parts, metadata);
		return getClassifications(parts, outMetadata).getAllClassifications();
	}


	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param fileName The file name of the document to classify
	 * @param title The document title
	 * @param body The document body
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyDocument(
			FileName fileName, Body body, Title title)
			throws ClassificationException {
		return classifyDocument(fileName, body, title, null);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param fileName The file name of the document to classify
	 * @param title The document title
	 * @param body The document body
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(FileName fileName, Body body, Title title)
			throws ClassificationException {
		return getClassifiedDocument(fileName, body, title, null);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param title The document title
	 * @param body The document body
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyDocument(
			Body body, Title title) throws ClassificationException {
		return classifyDocument(null, body, title, null);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param title The document title
	 * @param body The document body
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(Body body, Title title) throws ClassificationException {
		return getClassifiedDocument(null, body, title, null);
	}
	public byte[] getClassificationServerResponse(Body body, Title title) throws ClassificationException {
		return getClassificationServerResponse(null, body, title, null);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param body The document body
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyDocument(
			Body body, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		return classifyDocument(null, body, title, metadata);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param fileName The file name of the document to classify
	 * @param body The document body
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyDocument(
			FileName fileName, Body body, Title title,
			Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating document: '" + title.getValue() + "'");

		// If there is no body, then don't bother attempting to classify the
		// document
		if ((body == null) || (body.getValue() == null)
				|| (body.getValue().trim().length() == 0)) {
			Result result = new Result(null);
			return result.getAllClassifications();
		}

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		addTitle(parts, title);
		addByteArray(parts, body);
		addMetadata(parts, metadata);
		return getClassifications(parts).getAllClassifications();
	}


	/**
	 * Classify the supplied title and body as if they were a document
	 * @param fileName The file name of the document to classify
	 * @param body The document body
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(FileName fileName, Body body, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating document: '" + title.getValue() + "'");

		// If there is no body, then don't bother attempting to classify the
		// document
		if ((body == null) || (body.getValue() == null)
				|| (body.getValue().trim().length() == 0)) {
			Result result = new Result(null);
			return result;
		}

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		addTitle(parts, title);
		addMetadata(parts, metadata);
		addByteArray(parts, body, fileName);
		return getClassifications(parts);
	}
	public byte[] getClassificationServerResponse(FileName filename, Body body, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating document: '" + title.getValue() + "'");

		// If there is no body, then don't bother attempting to classify the
		// document
		if ((body == null) || (body.getValue() == null)
				|| (body.getValue().trim().length() == 0)) {
			return new byte[0];
		}

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		addTitle(parts, title);
		addMetadata(parts, metadata);
		addByteArray(parts, body, filename);
		return getClassificationServerResponse(parts);
	}

	/**
	 * Classify the supplied title and body as if they were a document
	 *
	 * @param body The document body
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(
			Body body, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating document: '" + title.getValue() + "'");
		return getClassifiedDocument(null, body, title, metadata);
	}

	/**
	 * Classify the supplied url
	 *
	 * @param url The URL to classify
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyUrl(URL url)
			throws ClassificationException {
		return getClassifiedDocument(url).getAllClassifications();
	}

	/**
	 * Classify the supplied url
	 *
	 * @param url The URL to classify
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(URL url)
			throws ClassificationException {
		return getClassifiedDocument(url, null, null);
	}

	/**
	 * Classify the supplied url with the extra metadata
	 *
	 * @param url The URL to classify
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	@Deprecated
	public Map<String, Collection<ClassificationScore>> classifyUrl(URL url,
			Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		if (logger.isDebugEnabled())
			logger.debug("Treating URL: '" + url.toExternalForm() + "'");

		return getClassifiedDocument(url, title, metadata).getAllClassifications();
	}
	/**
	 * Classify the supplied url with the extra metadata
	 *
	 * @param url The URL to classify
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return the classifications as returned by classification server.
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(URL url, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		if (logger.isDebugEnabled())
			logger.debug("Treating URL: '" + url.toExternalForm() + "'");

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
		addTitle(parts, title);
		addMetadata(parts, metadata);
		parts.add(FormBodyPartBuilder.create("path", getStringBody(url.toExternalForm())).build());
		return getClassifications(parts);
	}

	private static String statusRequestXML = "<?xml version=\"1.0\" ?>\n<request op=\"stats\">\n</request>";

	/**
	 * Return the status of the classification server instance
	 *
	 * @return A classification status object
	 * @throws ClassificationException Classification exception
	 */
	public ClassificationServerStatus status() throws ClassificationException {
		if (logger.isDebugEnabled())
			logger.debug("status - entry");

		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		partsList.add(FormBodyPartBuilder.create("XML_INPUT", getStringBody(statusRequestXML)).build());

		ClassificationServerStatus status = new ClassificationServerStatus(
				getClassificationServerResponse(partsList));
		return status;

	}

	/**
	 * Return the classification records for all requests between the two
	 * supplied dates
	 *
	 * @param startTime The earliest possible date for returned results
	 * @param endTime The latest possible date for returned results
	 * @return One record for each document classified in that date range
	 * @throws ClassificationException Classification exception
	 */
	public Collection<ClassificationRecord> getClassificationHistory(
			Date startTime, Date endTime) throws ClassificationException {
		logger.info("getClassificationHistory - entry");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ssZ");

		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		partsList.add(FormBodyPartBuilder.create("start_time", getStringBody(simpleDateFormat.format(startTime))).build());
		partsList.add(FormBodyPartBuilder.create("finish_time", getStringBody(simpleDateFormat.format(endTime))).build());
		partsList.add(FormBodyPartBuilder.create("operation", getStringBody("getclassificationhistory")).build());

		ClassificationHistory classificationHistory = new ClassificationHistory(getClassificationServerResponse(partsList));
		return classificationHistory.getClassificationRecords();
	}

	/**
	 * Return the rulebase classes that are currently configured on the
	 * classification server instance
	 *
	 * @return List of rulebases classes
	 * @throws ClassificationException Classification exception
	 */
	public Collection<RulebaseClass> getRulebaseClasses()
			throws ClassificationException {
		logger.debug("getRulebaseClasses - entry");

		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		partsList.add(FormBodyPartBuilder.create("operation", getStringBody("listrulenetclasses")).build());

		RulebaseClassSet rulebaseClassSet = new RulebaseClassSet(
				getClassificationServerResponse(partsList));
		return rulebaseClassSet.getRulebaseClasses();
	}

	/**
	 * Return the list of languages available on the cs instance
	 *
	 * @return List of languages
	 * @throws ClassificationException Classification exception
	 */
	public Collection<Language> getLanguages() throws ClassificationException {
		logger.debug("getLanguages - entry");
		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		partsList.add(FormBodyPartBuilder.create("operation", getStringBody( "listlanguages")).build());

		LanguageSet langSet = new LanguageSet(getClassificationServerResponse(partsList));
		return langSet.getLanguages();

	}

	/**
	 * Return the map of default parameter values
	 *
	 * @return Map of default parameter values
	 * @throws ClassificationException Classification exception
	 */
	public Map<String, Parameter> getDefaults() throws ClassificationException {
		logger.debug("getDefaults - entry");
		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		partsList.add(FormBodyPartBuilder.create("operation", getStringBody("getparameterdefaults")).build());
		Defaults defaults = new Defaults(
				getClassificationServerResponse(partsList));
		return defaults.getDefaults();

	}

	/**
	 * Return directly the output from classification server with no analysis
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @return The classification server response
	 * @throws ClassificationException Classification exception
	 */
	public byte[] getClassificationServerResponse(File inputFile, String fileType) throws ClassificationException {
		return getClassificationServerResponse(inputFile, fileType, null, null);
	}

	/**
	 * Return in a structured form the output of the classification process
	 *
	 * @param data Data to classify
	 * @param fileName A string containing the name of the file to classify
	 * @return The structured result of the classification
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(byte[] data, String fileName) throws ClassificationException {
		logger.debug("Treating byte array: '" + fileName + "'");
		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		if ((data == null) || (data.length == 0)) return new Result(null);

		addByteArray(parts, data, fileName);

		return new Result(getClassificationServerResponse(parts));
	}

	/**
	 * Return in a structured form the output of the classification process
	 *
	 * @param data Data to classify
	 * @param fileName A string containing the name of the file to classify
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return The structured result of the classification
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(byte[] data, String fileName,  Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating file: '" + fileName + "'");
		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		addTitle(parts, title);
		addMetadata(parts, metadata);
		addByteArray(parts, data, fileName);

		return new Result(getClassificationServerResponse(parts));
	}

	/**
	 * Return in a structured form the output of the classification process
	 *
	 * @param inputFile The input file to classify
	 * @param fileType File type of "inputFile". If the file type is not supplied (i.e. is null) then it will be guessed by classification server.
	 * @param title The document title
	 * @param metadata Map containing metadata
	 * @return The structured result of the classification
	 * @throws ClassificationException Classification exception
	 */
	public Result getClassifiedDocument(File inputFile, String fileType, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating file: '" + inputFile.getName() + "'");

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();

		addTitle(parts, title);
		addMetadata(parts, metadata);
		addFile(parts, inputFile, fileType);

		return new Result(getClassificationServerResponse(parts));
	}

	private void addTitle(Collection<FormBodyPart> parts, Title title) {
		if ((title != null) && (title.getValue() != null)
				&& (title.getValue().length() > 0)) {
			parts.add(FormBodyPartBuilder.create("title", getStringBody(title.getValue())).build());
		}
	}

	private void addByteArray(Collection<FormBodyPart> parts, Body body) {
		parts.add(FormBodyPartBuilder.create("body", getStringBody(body.getValue())).build());
	}

	private void addByteArray(Collection<FormBodyPart> parts, Body body, FileName filename) {
		if (filename == null) {
			parts.add(FormBodyPartBuilder.create("body", getStringBody(body.getValue())).build());
		} else {
			addByteArray(parts, body.getValue().getBytes(Charset.forName("UTF-8")), filename.getValue());
		}
	}

	private void addByteArray(Collection<FormBodyPart> parts, byte[] data, String fileName) {
		FormBodyPart filePart = FormBodyPartBuilder.create("UploadFile", new ByteArrayBody(data, fileName)).build();
		parts.add(filePart);
	}

	private void addFile(Collection<FormBodyPart> parts, File inputFile, String fileType) throws ClassificationException {
		if (inputFile == null) {
			throw new ClassificationException("Null input file provided");
		}
		if (!inputFile.exists()) {
			throw new ClassificationException("Input file not found: " + inputFile.getAbsolutePath());
		}

		if (fileType != null) {
			parts.add(FormBodyPartBuilder.create("UploadFile", new FileBody(inputFile)).build()); // TODO Can we access the content type?
		} else {
			parts.add(FormBodyPartBuilder.create("UploadFile", new FileBody(inputFile)).build());
		}
	}


	private void addMetadata(Collection<FormBodyPart> parts,
			Map<String, Collection<String>> metadata) {
		if (metadata != null) {
			for (String name : metadata.keySet()) {
				Collection<String> values = metadata.get(name);
				if (values != null) {
					int m = 0;
					for (String value : values) {
						if (m == 0) parts.add(FormBodyPartBuilder.create("meta_" + name,  getStringBody(value)).build());
						else parts.add(FormBodyPartBuilder.create("meta_" + name + "__" + m,  getStringBody(value)).build());
						m++;
					}
				}
			}
		}
	}

	private Collection<FormBodyPart> getParts(
			ClassificationConfiguration classificationConfiguration) {
		ArrayList<FormBodyPart> partsList = new ArrayList<FormBodyPart>();
		for (String parameterName : classificationConfiguration.getAdditionalParameters().keySet()) {
			String value = classificationConfiguration.getAdditionalParameters().get(parameterName);
			if ((value != null) && (value.length() > 0)) {
				partsList.add(FormBodyPartBuilder.create(parameterName, getStringBody(value)).build());
			}
		}
		if (classificationConfiguration.isSingleArticle())
			partsList.add(FormBodyPartBuilder.create("singlearticle", getStringBody("on")).build());
		if (classificationConfiguration.isMultiArticle())
			partsList.add(FormBodyPartBuilder.create("multiarticle", getStringBody("on")).build());
		if (classificationConfiguration.isFeedback())
			partsList.add(FormBodyPartBuilder.create("feedback", getStringBody("on")).build());
		if (classificationConfiguration.isStylesheet())
			partsList.add(FormBodyPartBuilder.create("stylesheet", getStringBody("on")).build());
		if (classificationConfiguration.isUseGeneratedKeys())
			partsList.add(FormBodyPartBuilder.create("use_generated_keys", getStringBody("on")).build());
		if (classificationConfiguration.isReturnHashCode())
			partsList.add(FormBodyPartBuilder.create("return_hash", getStringBody("on")).build());
		return partsList;
	}

	private static ContentBody getStringBody(String value) {
		ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
		return new StringBody(value, contentType);
	}

	private byte[] getClassificationServerResponse(Collection<FormBodyPart> parts)
			throws ClassificationException {
		logger.debug("getClassificationServerResponse - entry");
		URL url = null;
		HttpRequestBase baseRequest = null;
		byte[] returnedData = null;

		try {
			parts.addAll(getParts(classificationConfiguration));
			parts.add(FormBodyPartBuilder.create("return_hash", getStringBody("on")).build());
			if (this.getAuditUUID() != null) {
				parts.add(FormBodyPartBuilder.create("audit_tag", getStringBody(this.getAuditUUID().toString())).build());
			}
			if (classificationConfiguration.getUrl() != null) {
				url = new URL(classificationConfiguration.getUrl());
			} else {
				url = new URL(classificationConfiguration.getProtocol(),
					classificationConfiguration.getHostName(),
					classificationConfiguration.getHostPort(),
					classificationConfiguration.getHostPath());
			}
			if (logger.isDebugEnabled())
				logger.debug("URL: " + url.toExternalForm());

			// for (Part part: partsList) {
			// if (part instanceof StringPart) {
			// ((StringPart)part).setContentType(null);
			// ((StringPart)part).setTransferEncoding(null);
			// }
			// }
			if (parts.size() == 0) {
				baseRequest = new HttpGet(url.toExternalForm());
			} else {
				baseRequest = new HttpPost(url.toExternalForm());
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

				for (FormBodyPart part: parts) multipartEntityBuilder.addPart(part);
				((HttpPost)baseRequest).setEntity(multipartEntityBuilder.build());
			}
			if (classificationConfiguration.getApiToken() != null) {
				logger.trace("Adding authorization header: {}", classificationConfiguration.getApiToken());
				baseRequest.addHeader("Authorization", classificationConfiguration.getApiToken());
			}
			Builder requestConfigBuilder = RequestConfig.copy(RequestConfig.DEFAULT)
					.setSocketTimeout(classificationConfiguration.getSocketTimeoutMS())
					.setConnectTimeout(classificationConfiguration.getConnectionTimeoutMS())
					.setConnectionRequestTimeout(classificationConfiguration.getConnectionTimeoutMS());
			if ((getProxyHost() != null) && (getProxyHost().length() > 0) && (getProxyPort() > 0)) {
				HttpHost proxy = new HttpHost(getProxyHost(), getProxyPort(), "http");
				requestConfigBuilder.setProxy(proxy);
			}
			RequestConfig requestConfig = requestConfigBuilder.build();

			CloseableHttpClient httpClient =
				      HttpClients.custom()
				      			.setDefaultRequestConfig(requestConfig)
				                 .setSSLHostnameVerifier(new NoopHostnameVerifier())
				                 .build();

			logger.debug("Sending request");
			HttpResponse response = httpClient.execute(baseRequest);
			if (response == null) throw new ClassificationException("Null response from http client: " + url.toExternalForm());
			if (response.getStatusLine() == null) throw new ClassificationException("Null status line from http client: "+  url.toExternalForm());


			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();

			logger.debug("Status: " + statusCode);
			if (entity != null) {
				returnedData = IOUtils.toByteArray(entity.getContent());
				if (logger.isTraceEnabled()) {
					Header[] headers = response.getHeaders("Server");
					for (Header header: headers) {
						logger.trace("{}:{}", header.getName(), header.getValue());
					}
					try {
						logger.trace(new String(returnedData, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						logger.error("UnsupportedEncodingException whilst logging CS response");
					}
				}
			}

			if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				logger.warn("Internal classification server error: " + entity.toString());
				return null;
			} else if (statusCode != HttpStatus.SC_OK) {
				throw new ClassificationException("HttpStatus: " + statusCode + " received from classification server (" + url.toExternalForm() + ")" + entity.toString());
			} else {
				Header[] headers = response.getHeaders("Server");
				if (headers.length > 0)
					version = headers[0].getValue();
			}
			if (entity == null) {
				throw new ClassificationException("Null response from Classification Server");
			}


		} catch (MalformedURLException e) {
			throw new ClassificationException("MalformedURLException: " + e.getMessage());
		} catch (IOException e) {
			throw new ClassificationException("IOException: " + e.getMessage() + " (" + url.toExternalForm() + ")\n" + this.toString());
		} finally {
			if (baseRequest != null) {
				baseRequest.abort();
			}
		}
		logger.debug("getClassificationServerResponse - exit: " + returnedData.length);
		return returnedData;
	}

	private Result getClassifications(Collection<FormBodyPart> partsList) throws ClassificationException {
		return getClassifications(partsList, null);
	}

	private Result getClassifications(Collection<FormBodyPart> partsList, Map<String, String> outMeta)
			throws ClassificationException {
		Result result = new Result(getClassificationServerResponse(partsList));
		if ((result != null) && (result.getMetadata() != null) && (outMeta != null)) {
			for (String meta : result.getMetadata().keySet()){
				outMeta.put(meta, result.getMetadata().get(meta));
			}
		}
		return result;
	}

	public byte[] getClassificationServerResponse(File inputFile, String fileType, Title title, Map<String, Collection<String>> metadata)
			throws ClassificationException {
		logger.debug("Treating file: '" + inputFile + "'");

		Collection<FormBodyPart> parts = new ArrayList<FormBodyPart>();
		addFile(parts, inputFile, fileType);

		addTitle(parts, title);
		addMetadata(parts, metadata);
		return getClassificationServerResponse(parts);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.getClass() .getCanonicalName() + "\n");
		stringBuilder.append("  Host Name: '" + this.getClassificationConfiguration().getHostName() + "'\n");
		stringBuilder.append("  Host Path: '" + this.getClassificationConfiguration().getHostPath() + "'\n");
		stringBuilder.append("  Host Port: '" + this.getClassificationConfiguration().getHostPort() + "'\n");
		stringBuilder.append("  Connection Timeout MS: '" + this.getClassificationConfiguration().getConnectionTimeoutMS() + "'\n");
		stringBuilder.append("  Socket Timeout MS: '" + this.getClassificationConfiguration().getSocketTimeoutMS() + "'\n");
		stringBuilder.append("  Protocol: '" + this.getClassificationConfiguration().getProtocol() + "'\n");
		stringBuilder.append("  Proxy Host: '" + this.getProxyHost() + "'\n");
		stringBuilder.append("  Proxy Port: '" + this.getProxyPort() + "'\n");
		return stringBuilder.toString();
	}

}
