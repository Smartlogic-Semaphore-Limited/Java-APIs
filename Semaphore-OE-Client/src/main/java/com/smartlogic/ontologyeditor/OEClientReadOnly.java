package com.smartlogic.ontologyeditor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.sparql.util.FmtUtils;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.uri.UriComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.cloud.Token;
import com.smartlogic.ontologyeditor.beans.ChangeRecord;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptClass;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Model;
import com.smartlogic.ontologyeditor.beans.RelationshipType;
import com.smartlogic.ontologyeditor.beans.Task;

public class OEClientReadOnly {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final static String basicProperties = "sem:guid,skosxl:prefLabel/[]";

	private String baseURL;
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
	}

	private String token;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	private Token cloudToken;
	public Token getCloudToken() {
		return cloudToken;
	}
	public void setCloudToken(Token cloudToken) {
		this.cloudToken = cloudToken;
	}
	private String getCloudTokenValue() {
		if (cloudToken == null) return "";
		
		return cloudToken.getAccess_token();
	}

	private String proxyAddress;
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	protected String getWrappedUri(String uriString) throws OEClientException {
		try {
			URI uri = new URI(uriString);
			return (uri.getAuthority() == null) ? uri.toString() : "<" + uri.toString() + ">";
		} catch (URISyntaxException e) {
			throw new OEClientException(String.format("%s is not a valid URI", uriString));
		}
	}


	protected Builder getInvocationBuilder(String url) {
		return getInvocationBuilder(url, null);
	}

	protected Builder getInvocationBuilder(String url, Map<String, String> queryParameters) {
		ClientConfig clientConfig = new ClientConfig();
		if (getProxyAddress() != null) {
			clientConfig.connectorProvider(new ApacheConnectorProvider());
			clientConfig.property(ClientProperties.PROXY_URI, getProxyAddress());
		}
		Client client = ClientBuilder.newClient(clientConfig);
		client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
		WebTarget webTarget = client.target(url);

		if (queryParameters != null) {
			for (Map.Entry<String, String> queryParameter: queryParameters.entrySet())
//				webTarget = webTarget.queryParam(queryParameter.getKey(), queryParameter.getValue().replace("_", "_1").replace("%",  "_0"));
				webTarget = webTarget.queryParam(queryParameter.getKey(), queryParameter.getValue());
		}

		
		return webTarget.request(MediaType.APPLICATION_JSON).accept("application/ld+json").header("Authorization", getCloudTokenValue());
	}

	private String modelURL = null;
	protected String getModelURL() {
		if (modelURL == null) {
			StringBuilder stringBuilder = new StringBuilder(baseURL);
			if (!baseURL.endsWith("/")) stringBuilder.append("/");
			stringBuilder.append("api/");
			stringBuilder.append("t/");
			stringBuilder.append(token);
			stringBuilder.append("/");
			stringBuilder.append(modelUri);
			modelURL = stringBuilder.toString();
		}
		return modelURL;
	}
	private String apiURL = null;
	protected String getApiURL() {
		if (apiURL == null) {
			StringBuilder stringBuilder = new StringBuilder(baseURL);
			if (!baseURL.endsWith("/")) stringBuilder.append("/");
			stringBuilder.append("api/");
			stringBuilder.append("t/");
			stringBuilder.append(token);
			stringBuilder.append("/");
			apiURL = stringBuilder.toString();
		}
		return apiURL;
	}

	private String modelSysURL = null;
	protected String getModelSysURL() {
		if (modelSysURL == null) {
			StringBuilder stringBuilder = new StringBuilder(baseURL);
			if (!baseURL.endsWith("/")) stringBuilder.append("/");
			stringBuilder.append("api/");
			stringBuilder.append("t/");
			stringBuilder.append(token);
			stringBuilder.append("/sys/");
			stringBuilder.append(modelUri);
			modelSysURL = stringBuilder.toString();
		}
		return modelSysURL;
	}

	protected String getTaskSysURL(Task task) {
		StringBuilder stringBuilder = new StringBuilder(baseURL);
		if (!baseURL.endsWith("/")) stringBuilder.append("/");
		stringBuilder.append("api/");
		stringBuilder.append("t/");
		stringBuilder.append(token);
		stringBuilder.append("/sys/");
		stringBuilder.append(task.getGraphUri());
		return stringBuilder.toString();
	}

	public Collection<Model> getAllModels() throws OEClientException {
		logger.info("getAllModels entry");
		
		String url = getApiURL() + "/sys/sys:Model/rdf:instance";
		logger.info("getAllModels URL: {}", url);
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", "meta:displayName,meta:graphUri");

		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Date startDate = new Date();
		logger.info("getAllModels making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getAllModels call complete: {}", startDate.getTime());

		logger.info("getAllModels - status: {}", response.getStatus());

		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isInfoEnabled()) logger.info("getAllModels: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
			Collection<Model> models = new ArrayList<Model>();
			Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
			while (jsonValueIterator.hasNext()) {
				JsonObject modelObject = jsonValueIterator.next().getAsObject();
				models.add(new Model(modelObject));
			}
			return models;
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	/**
	 * getAllTasks
	 * @return all the tasks present for this model
	 * @throws OEClientException 
	 */
	public Collection<Task> getAllTasks() throws OEClientException {
		logger.info("getAllTasks entry");

		String url = getModelSysURL();
		logger.info("getAllTasks URL: {}", url);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", "meta:hasTask/(meta:graphUri|meta:displayName)");
		queryParameters.put("filters", "subject_hasTask(notExists rdf:type sem:ORTTask)");

		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Date startDate = new Date();
		logger.info("getAllTasks making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getAllTasks call complete: {}", startDate.getTime());

		logger.info("getAllTasks - status: {}", response.getStatus());

		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isInfoEnabled()) logger.info("getAllTasks: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
			Collection<Task> tasks = new ArrayList<Task>();
			Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
			while (jsonValueIterator.hasNext()) {
				JsonObject modelData = jsonValueIterator.next().getAsObject();
				JsonArray taskArray = modelData.get("meta:hasTask").getAsArray();
				Iterator<JsonValue> jsonTaskIterator = taskArray.iterator();
				while (jsonTaskIterator.hasNext()) {
					tasks.add(new Task(jsonTaskIterator.next().getAsObject()));
				}
			}
			return tasks;
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
		
	}

	public Collection<ChangeRecord> getChangesSince(Date date) throws OEClientException {

			StringBuilder stringBuilder = new StringBuilder(baseURL);
			if (!baseURL.endsWith("/")) stringBuilder.append("/");
			stringBuilder.append("api/");
			stringBuilder.append("t/");
			stringBuilder.append(token);
			stringBuilder.append("/tch");
			stringBuilder.append(modelUri);
			stringBuilder.append("/teamwork:Change/rdf:instance");
			
			String url = stringBuilder.toString();
			logger.debug("getChangesSince: '{}'", url);

			Map<String, String> queryParameters = new HashMap<String, String>();
			queryParameters.put("properties", "?triple/(teamwork:subject|teamwork:predicate|teamwork:object),sem:committed");
			queryParameters.put("filters", String.format("subject(sem:committed >= \"%s\"^^xsd:dateTime)", date.toInstant().toString()));
			queryParameters.put("sort", "sem:committed");
			
			Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

			Date startDate = new Date();
			logger.info("getChangesSince making call  : {}", startDate.getTime());
			Response response = invocationBuilder.get();
			logger.info("getChangesSince call complete: {}", startDate.getTime());

			logger.info("getChangesSince - status: {}", response.getStatus());

 			if (response.getStatus() == 200) {
 				String stringResponse = response.readEntity(String.class);
 				if (logger.isInfoEnabled()) logger.info("getChangesSince: jsonResponse {}", stringResponse);
 				JsonObject jsonResponse = JSON.parse(stringResponse);
 				JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
 				Collection<ChangeRecord> changeRecords = new ArrayList<ChangeRecord>();
 				Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
 				while (jsonValueIterator.hasNext()) {
 					changeRecords.add(new ChangeRecord(this, jsonValueIterator.next().getAsObject()));
 				}
 				return changeRecords;
 			} else {
 				throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
 			}
	}
	
	public Collection<ConceptClass> getConceptClasses() throws OEClientException {
		return getConceptClasses(1000);
	}

	private Collection<RelationshipType> getRelationshipTypes(String parentType) throws OEClientException {
		logger.info("getRelationshipTypes entry: {}", parentType);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", "rdfs:label,owl:inverseOf,rdfs:subPropertyOf,owl:inverseOf/rdfs:label,owl:inverseOf/rdfs:subPropertyOf");
		Invocation.Builder invocationBuilder = getInvocationBuilder(getModelURL() + "/" + parentType +"/meta:transitiveSubProperty", queryParameters);

		Date startDate = new Date();
		logger.info("getRelationshipTypes making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getRelationshipTypes call complete: {}", startDate.getTime());

		logger.info("getRelationshipTypes - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isInfoEnabled()) logger.info("getConceptClasses: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
			Collection<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
			Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
			while (jsonValueIterator.hasNext()) {
				relationshipTypes.add(new RelationshipType(this, jsonValueIterator.next().getAsObject()));
			}
			return relationshipTypes;
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	/**
	 * Return all hierarchical relationships classes
	 * @return
	 * @throws OEClientException
	 */
	public Collection<RelationshipType> getHierarchicalRelationshipTypes() throws OEClientException {
		return getRelationshipTypes("skos:broader");
	}

	/**
	 * Return all associative relationships classes
	 * @return
	 * @throws OEClientException
	 */
	public Collection<RelationshipType> getAssociativeRelationshipTypes() throws OEClientException {
		return getRelationshipTypes("skos:related");
	}

	/**
	 * Return all concept classes
	 * @return
	 * @throws OEClientException
	 */
	public Collection<ConceptClass> getConceptClasses(int limit) throws OEClientException {
		logger.info("getConceptClasses entry");

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", "rdfs:label,rdfs:subClassOf");
		queryParameters.put("limit", Integer.toString(limit));
		Invocation.Builder invocationBuilder = getInvocationBuilder(getModelURL() + "/skos:Concept/meta:transitiveSubClass", queryParameters);

		Date startDate = new Date();
		logger.info("getConceptClasses making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getConceptClasses call complete: {}", startDate.getTime());

		logger.info("getConceptClasses - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isInfoEnabled()) logger.info("getConceptClasses: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
			Collection<ConceptClass> conceptClasses = new HashSet<ConceptClass>();
			Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
			while (jsonValueIterator.hasNext()) {
				conceptClasses.add(new ConceptClass(this, jsonValueIterator.next().getAsObject()));
			}
			return conceptClasses;
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	/**
	 * Return the concept with the supplied URI with pref label, uri and type fields populated
	 * @param conceptUri
	 * @return
	 * @throws OEClientException
	 */
	public Concept getConcept(String conceptUri) throws OEClientException {
		logger.info("getConcept entry: {}", conceptUri);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", basicProperties);
		queryParameters.put("path", getPathParameter(conceptUri));
		Invocation.Builder invocationBuilder = getInvocationBuilder(getApiURL(), queryParameters);

		Date startDate = new Date();
		logger.info("getConcept making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getConcept call complete: {}", startDate.getTime());

		logger.info("getConceptDetails - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isDebugEnabled()) logger.debug("getConceptDetails: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			return new Concept(this, jsonResponse.get("@graph").getAsArray().get(0).getAsObject());
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	/**
	 * Return the concept with the supplied GUID
	 * @param guid
	 * @return
	 * @throws OEClientException
	 */
	public Concept getConceptByGuid(String guid) throws OEClientException {
		return getConceptByIdentifier(new Identifier("sem:guid", guid));
	}

	/**
	 * Return the concept with the supplied identifier
	 * @param identifierUri
	 * @param identifier
	 * @return
	 * @throws OEClientException
	 */
	public Concept getConceptByIdentifier(Identifier identifier) throws OEClientException {
		logger.info("getConceptByIdentifier entry: {}", identifier);

		String url = getModelURL() + "/skos:Concept/meta:transitiveInstance";

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", basicProperties);
		queryParameters.put("filters", String.format("subject(exists %s \"%s\")", getWrappedUri(identifier.getUri()), identifier.getValue()));
		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Date startDate = new Date();
		logger.info("getConceptByIdentifier making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("getConceptByIdentifier call complete: {}", startDate.getTime());

		logger.info("getConceptByIdentifier - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isDebugEnabled()) logger.debug("getConceptByIdentifier: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			return new Concept(this, jsonResponse.get("@graph").getAsArray().get(0).getAsObject());
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	
	public Collection<Concept> getAllConcepts() throws OEClientException {
		return getFilteredConcepts(new OEFilter());
	}

	public Collection<Concept> getFilteredConcepts(OEFilter oeFilter) throws OEClientException {
		logger.info("getFilteredConcepts entry: {}", oeFilter);

		String url = getModelURL() + "/skos:Concept/meta:transitiveInstance";

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", basicProperties);

		if (oeFilter.getConceptClass() != null) queryParameters.put("filters", String.format("subject(rdf:type=%s)", getWrappedUri(oeFilter.getConceptClass())));

		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Date startDate = new Date();
		logger.info("getFilteredConcepts making call  : {} {}", startDate.getTime(), url);
		Response response = invocationBuilder.get();
		logger.info("getFilteredConcepts call complete: {}", startDate.getTime());

		logger.info("getFilteredConcepts - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isDebugEnabled()) logger.debug("getFilteredConcepts: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);

			JsonArray jsonArray = jsonResponse.get("@graph").getAsArray();
			Collection<Concept> concepts = new HashSet<Concept>();
			Iterator<JsonValue> jsonValueIterator = jsonArray.iterator();
			while (jsonValueIterator.hasNext()) {
				concepts.add(new Concept(this, jsonValueIterator.next().getAsObject()));
			}
			return concepts;
		} else {
			String stringResponse = response.readEntity(String.class);
			logger.info("getFilteredConcepts: jsonResponse {}", stringResponse);
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}


	public void populateRelatedConceptUris(String relationshipUri, Concept concept) throws OEClientException {
		logger.info("populateNarrowerConceptURIs entry: {}", concept.getUri());

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", getWrappedUri(relationshipUri));
		Invocation.Builder invocationBuilder = getInvocationBuilder(getResourceURL(concept.getUri()), queryParameters);

		Date startDate = new Date();
		logger.info("populateNarrowerConceptURIs making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("populateNarrowerConceptURIs call complete: {}", startDate.getTime());

		logger.info("populateNarrowerConceptURIs - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isDebugEnabled()) logger.debug("populateNarrowerConceptURIs: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			concept.populateRelatedConceptUris(relationshipUri, jsonResponse.get("@graph").getAsArray().get(0).getAsObject());
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}

	
	public void populateMetadata(String metadataUri, Concept concept) throws OEClientException {
		logger.info("populateMetadata entry: {}", concept.getUri());

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("properties", getWrappedUri(metadataUri));
		Invocation.Builder invocationBuilder = getInvocationBuilder(getResourceURL(concept.getUri()), queryParameters);

		Date startDate = new Date();
		logger.info("populateMetadata making call  : {}", startDate.getTime());
		Response response = invocationBuilder.get();
		logger.info("populateMetadata call complete: {}", startDate.getTime());

		logger.info("populateMetadata - status: {}", response.getStatus());
		if (response.getStatus() == 200) {
			String stringResponse = response.readEntity(String.class);
			if (logger.isDebugEnabled()) logger.debug("populateNarrowerConceptURIs: jsonResponse {}", stringResponse);
			JsonObject jsonResponse = JSON.parse(stringResponse);
			concept.populateMetadata(metadataUri, jsonResponse.get("@graph").getAsArray().get(0).getAsObject());
		} else {
			throw new OEClientException(String.format("Error(%d) %s from server", response.getStatus(), response.getStatusInfo().toString()));
		}
	}
	

	// Concept uri needs to be encoded to be used in path
	protected String getResourceURL(String resourceUri) {
		logger.info("getResourceURL - entry: {}", resourceUri);
		String resourceURL = getModelURL() + "/" + getPath(resourceUri);
		logger.info("getResourceURL - exit: {}", resourceURL);
		return resourceURL;
	}

	private String getPath(String resourceUri) {
		logger.info("getPath - entry: {}", resourceUri);
		String processedUri = FmtUtils.stringForURI(resourceUri);
		String escapedUri = getEscapedUri(processedUri);

		logger.info("getPath - exit: {}", escapedUri);
		return escapedUri;
	}

	protected String getPathParameter(String conceptUri) {
		logger.info("getPath - entry: {}", conceptUri);
		String pathParameter = (modelUri + "/" + getPath(conceptUri)).replaceAll("%", "%25");
		logger.info("getPath - exit: {}", pathParameter);
		return pathParameter;
	}

	/**
	 * Return the string representation of a URI when it is to be used when generating the URL for a request.
	 * @param uriToEscape
	 * @return
	 */
	protected String getEscapedUri(String uriToEscape) {
		return UriComponent.encode(uriToEscape, UriComponent.Type.PATH_SEGMENT);
	}

	/**
	 * See https://tools.ietf.org/html/rfc6901 section 3 as to why we have to do this
	 * @param wrappedUri
	 * @return
	 */
	protected String getTildered(String wrappedUri) {
		return wrappedUri.replaceAll("~", "~0").replaceAll("/", "~1");
	}

}
