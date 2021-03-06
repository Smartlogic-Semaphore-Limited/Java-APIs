package com.smartlogic.ontologyeditor;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import com.smartlogic.ontologyeditor.beans.Task;

public class OEClientReadWrite extends OEClientReadOnly {

	/**
	 * addListener - create a task within the current model
	 * @param listenerUri - the identifier (within the model) for the listener being added
	 * @param listenerUrl - where the listener is to be found 
	 * @throws OEClientException 
	 */
	public void addListener(String listenerUri, String listenerUrl) throws OEClientException {
		logger.info("addListener entry: {} {}", listenerUri, listenerUrl);

		String url = getModelSysURL();
		logger.info("addListener URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);
		
		JsonArray requestArray = new JsonArray();
		
		JsonObject requestObject = new JsonObject();
		requestObject.put("op", "add");
		requestObject.put("path", "@graph/0/semlisteners:hasListener/-");

		JsonObject valueObject = new JsonObject();
		valueObject.put("@id", listenerUri);
		valueObject.put("@type", "semlisteners:Listener");
		
		JsonArray listenerURLArray = new JsonArray();
		listenerURLArray.add(listenerUrl);
		valueObject.put("semlisteners:listenerUrl", listenerURLArray);
		
		requestObject.put("value",  valueObject);
		requestArray.add(requestObject);
		
		String payload = requestArray.toString();

		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(payload, "application/json-patch+json"));
		
		Date startDate = new Date();
		logger.info("addListener making call  : {} {}", payload, startDate.getTime());
		Response response = invocation.invoke();

		checkResponseStatus("addListener", response);
		
	}
		
	/**
	 * createModel - create a task within the current model
	 * @param model - the model to be created
	 * @throws OEClientException  - an error has occurred contacting the server
	 */
	public void createModel(Model model) throws OEClientException {
		logger.info("createModel entry: {}", model.getLabel());

		String url = getApiURL() + "sys/sys:Model/rdf:instance";
		logger.info("createModel URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JsonObject modelObject = new JsonObject();

		JsonArray modelTypeList = new JsonArray();
		modelTypeList.add("sys:Model");
		modelObject.put("@type", modelTypeList);

		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", model.getLabel().getValue());
		labelObject.put("@language", model.getLabel().getLanguageCode());
		modelObject.put("rdfs:label", labelObject);
		
		JsonArray defaultNamespaceList = new JsonArray();
		defaultNamespaceList.add(model.getDefaultNamespace());
		modelObject.put("swa:defaultNamespace", defaultNamespaceList);

		modelObject.put("rdfs:comment", model.getComment());
		String modelPayload = modelObject.toString();

		Date startDate = new Date();
		logger.info("createModel making call  : {} {}", modelPayload, startDate.getTime());
		Response response = invocationBuilder.post(Entity.entity(modelPayload, "application/ld+json"));

		checkResponseStatus("createModel", response);
		
	}

	/**
	 * Delete model
	 * @param model - the model to be deleted
	 * @throws OEClientException  - an error has occurred contacting the server
	 */
	public void deleteModel(Model model) throws OEClientException {
		logger.info("deleteModel entry: {}", model.getLabel());

		String url = getApiURL() + "sys/" + model.getUri();
		logger.info("deleteModel URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);
		
		logger.info("deleteModel - about to call");
		Response response = invocationBuilder.delete();
		logger.info("deleteModel - call returned");

		checkResponseStatus("deleteModel", response);
	}

	/**
	 * createTask - create a task within the current model
	 * @param task 
	 *          - the task to be created
	 * @throws OEClientException 
	 */
	public void createTask(Task task) throws OEClientException {
		logger.info("createTask entry: {}", task.getLabel());

		String url = getModelSysURL() + "/meta:hasTask";
		logger.info("createTask URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JsonObject taskObject = new JsonObject();

		JsonArray taskTypeList = new JsonArray();
		taskTypeList.add("sys:Task");
		taskObject.put("@type", taskTypeList);

		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", task.getLabel().getValue());
		labelObject.put("@language", task.getLabel().getLanguageCode());
		taskObject.put("rdfs:label", labelObject);

		String taskPayload = taskObject.toString();

		Date startDate = new Date();
		logger.info("createTask making call  : {} {}", taskPayload, startDate.getTime());
		Response response = invocationBuilder.post(Entity.entity(taskPayload, "application/ld+json"));
		checkResponseStatus("createTask", response);
		
	}

	public void commitTask(Task task) throws OEClientException {
		Label label = new Label("en", "Commit added via API");
		String comment = "No comment supplied";
		commitTask(task, label, comment);
	}
	
	public void commitTask(Task task, Label label, String comment) throws OEClientException {
		logger.info("commitTask entry: {}", task);

		String url = getTaskSysURL(task) + "/teamwork:Change/rdf:instance";
		logger.info("commitTask URL: {}", url);
		
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("action", "commit");
		queryParameters.put("filter", "subject(teamwork:status = teamwork:Uncommitted)");
		
		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		JsonObject taskObject = new JsonObject();
		JsonObject commitObject = new JsonObject();
		JsonArray typeArray = new JsonArray();
		typeArray.add("sem:Commit");
	    commitObject.put("@type", typeArray);
		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@language", label.getLanguageCode());
		labelObject.put("@value", label.getValue());
		labelArray.add(labelObject);
		commitObject.put("rdfs:label", labelArray);
		JsonArray commentArray = new JsonArray();
		JsonObject commentObject = new JsonObject();
		commentObject.put("@language", "");
		commentObject.put("@value", comment);
		commentArray.add(commentObject);
		commitObject.put("rdfs:comment", commentArray);
		
		taskObject.put("@graph", commitObject);
		String taskPayload = taskObject.toString();

		Date startDate = new Date();
		logger.info("commitTask making call  : {} {}", taskPayload, startDate.getTime());
		Response response = invocationBuilder.post(Entity.entity(taskPayload, "application/ld+json"));

		checkResponseStatus("commitTask", response);
		
	}


	/**
	 * createConcept - create a concept as a topConcept of a Concept Scheme
	 *
	 * @param conceptSchemeUri
	 *            - the URI of the concept scheme for which the new concept will
	 *            become a new concept
	 * @param concept
	 *            - the concept to create. The preferred labels and class of
	 *            this concept will be added
	 * @throws OEClientException 
	 */
	public void createConcept(String conceptSchemeUri, Concept concept) throws OEClientException {
		logger.info("createConcept entry: {} {}", conceptSchemeUri, concept.getUri());

		Invocation.Builder invocationBuilder = getInvocationBuilder(getModelURL());

		JsonArray conceptTypeList = new JsonArray();
		conceptTypeList.add("skos:Concept");

		JsonArray labelTypeList = new JsonArray();
		labelTypeList.add("skosxl:Label");

		JsonArray labelLiteralFormDataList = new JsonArray();
		for (Label label : concept.getPrefLabels()) {
			JsonObject labelLiteralFormData = new JsonObject();
			labelLiteralFormData.put("@value", label.getValue());
			labelLiteralFormData.put("@language", label.getLanguageCode());
			labelLiteralFormDataList.add(labelLiteralFormData);
		}

		JsonObject newConceptLabelData = new JsonObject();
		newConceptLabelData.put("@type", labelTypeList);
		newConceptLabelData.put("skosxl:literalForm", labelLiteralFormDataList);

		JsonArray newConceptLabelDataList = new JsonArray();
		newConceptLabelDataList.add(newConceptLabelData);

		JsonObject relatedConceptSchemeData = new JsonObject();
		relatedConceptSchemeData.put("@id", conceptSchemeUri);

		JsonObject conceptDetails = new JsonObject();
		conceptDetails.put("@type", conceptTypeList);
		conceptDetails.put("skosxl:prefLabel", newConceptLabelDataList);
		conceptDetails.put("skos:topConceptOf", relatedConceptSchemeData);
		conceptDetails.put("@id", concept.getUri());
		for (Identifier identifier : concept.getIdentifiers())
			conceptDetails.put(identifier.getUri(), identifier.getValue());

		String conceptSchemePayload = conceptDetails.toString();

		Date startDate = new Date();
		logger.info("createConcept making call  : {}", startDate.getTime());
		Response response = invocationBuilder.post(Entity.entity(conceptSchemePayload, "application/ld+json"));

		checkResponseStatus("createConcept", response);

	}

	public void createConceptBelowConcept(String parentConceptUri, Concept concept) throws OEClientException {
		logger.info("createConceptBelowConcept entry: {} {}", parentConceptUri, concept.getUri());

		Invocation.Builder invocationBuilder = getInvocationBuilder(getModelURL());

		JsonArray conceptTypeList = new JsonArray();
		conceptTypeList.add("skos:Concept");

		JsonArray labelTypeList = new JsonArray();
		labelTypeList.add("skosxl:Label");

		JsonArray labelLiteralFormDataList = new JsonArray();
		for (Label label : concept.getPrefLabels()) {
			JsonObject labelLiteralFormData = new JsonObject();
			labelLiteralFormData.put("@value", label.getValue());
			labelLiteralFormData.put("@language", label.getLanguageCode());
			labelLiteralFormDataList.add(labelLiteralFormData);
		}

		JsonObject newConceptLabelData = new JsonObject();
		newConceptLabelData.put("@type", labelTypeList);
		newConceptLabelData.put("skosxl:literalForm", labelLiteralFormDataList);

		JsonArray newConceptLabelDataList = new JsonArray();
		newConceptLabelDataList.add(newConceptLabelData);

		JsonObject relatedConceptSchemeData = new JsonObject();
		relatedConceptSchemeData.put("@id", parentConceptUri);
		JsonArray relatedConceptSchemeArray = new JsonArray();
		relatedConceptSchemeArray.add(relatedConceptSchemeData);
		
		JsonObject conceptDetails = new JsonObject();
		conceptDetails.put("@type", conceptTypeList);
		conceptDetails.put("skosxl:prefLabel", newConceptLabelDataList);
		conceptDetails.put("skos:broader", relatedConceptSchemeArray);
		conceptDetails.put("@id", concept.getUri());
		for (Identifier identifier : concept.getIdentifiers())
			conceptDetails.put(identifier.getUri(), identifier.getValue());

		String conceptSchemePayload = conceptDetails.toString();

		logger.info("createConceptBelowConcept making call with payload: {}", conceptSchemePayload);
		Response response = invocationBuilder.post(Entity.entity(conceptSchemePayload, "application/ld+json"));

		checkResponseStatus("createConceptBelowConcept", response);

	}
	private void checkResponseStatus(String callingMethod, Response response) throws OEClientException {
		if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
			logger.info("{} call completed successfully: {}", callingMethod);
		} else {
			String message = String.format("%s call returned error %s: %s", 
					callingMethod, response.getStatus(), response.readEntity(String.class));
			logger.warn(message);
			throw new OEClientException(message);
		}
	}

	/**
	 * createConceptScheme - create a concept as a topConcept of a Concept
	 * Scheme
	 *
	 * @param conceptScheme
	 *            - the concept scheme to create, the labels of this concept
	 *            will be created
	 * @throws OEClientException 
	 */
	public void createConceptScheme(ConceptScheme conceptScheme) throws OEClientException {
		logger.info("createConceptScheme entry: {}", conceptScheme.getUri());

		Invocation.Builder invocationBuilder = getInvocationBuilder(getModelURL());

		JsonObject conceptSchemeDetails = new JsonObject();

		JsonArray conceptSchemeTypeList = new JsonArray();
		conceptSchemeTypeList.add("skos:ConceptScheme");
		conceptSchemeDetails.put("@type", conceptSchemeTypeList);

		JsonArray newconceptSchemeLabelDataList = new JsonArray();
		for (Label label : conceptScheme.getPrefLabels()) {
			JsonObject newconceptSchemeLabelData = new JsonObject();
			newconceptSchemeLabelData.put("@value", label.getValue());
			newconceptSchemeLabelData.put("@language", label.getLanguageCode());
			newconceptSchemeLabelDataList.add(newconceptSchemeLabelData);
		}

		conceptSchemeDetails.put("rdfs:label", newconceptSchemeLabelDataList);
		conceptSchemeDetails.put("@id", conceptScheme.getUri());

		String conceptSchemePayload = conceptSchemeDetails.toString();

		Date startDate = new Date();
		logger.info("createConceptScheme making call  : {}", startDate.getTime());
		Response response = invocationBuilder.post(Entity.entity(conceptSchemePayload, "application/ld+json"));

		checkResponseStatus("createConceptScheme", response);
	}



	/**
	 * Update a label object
	 *
	 * @param label
	 *            - the label to be updated. The URI, language and value of this
	 *            label must be defined. The value and language code must match
	 *            the values in the Ontology.
	 * @param newLabelLanguage
	 *            - the new language for the label
	 * @param newLabelValue
	 *            - the new value for the label
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	@SuppressWarnings({ "unchecked" })
	public void updateLabel(Label label, String newLabelLanguage, String newLabelValue) throws OEClientException {
		logger.info("updateLabel entry: {}", label.getUri());

		String url = getModelURL();
		logger.info("updateLabel - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JSONArray valueArray1 = new JSONArray();
		JSONObject value1 = new JSONObject();
		value1.put("@id", label.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToUpdate = "@graph/2/skosxl:literalForm/0";
		
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToUpdate);
		JSONArray valueArray2 = new JSONArray();
		JSONObject value2 = new JSONObject();
		value2.put("@language", label.getLanguageCode()); 
		value2.put("@value", label.getValue()); 
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);
		
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToUpdate);
		operationList.add(removeOperation);
		
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", pathToUpdate);
		JSONArray valueArray3 = new JSONArray();
		JSONObject value3 = new JSONObject();
		value3.put("@language", newLabelLanguage); 
		value3.put("@value", newLabelValue); 
		valueArray3.add(value3);
		addOperation.put("value", valueArray3);
		operationList.add(addOperation);
		
		
		String updateLabelPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("updateLabel payload: {}", updateLabelPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(updateLabelPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("updateLabel", response);
	}

	@SuppressWarnings("unchecked")
	public void createLabels(String[] conceptUris, Label[] labels) throws OEClientException {
		if (conceptUris == null)
			throw new IllegalArgumentException("createLabels cannot take null concept URIs array");
		if (labels == null)
			throw new IllegalArgumentException("createLabels cannot take null labels array");
		logger.info("createLabels: {} conceptUris, {} labels provided", conceptUris.length, labels.length);

		if ((conceptUris.length != labels.length))
			throw new IllegalArgumentException(String.format("conceptUris size (%d) must match labels size (%d)",
					conceptUris.length, labels.length));
		if ((conceptUris.length == 0))
			return;

		String url = getModelURL();
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONObject graphObject = new JSONObject();

		JSONArray dataArray = new JSONArray();
		for (int i = 0; i < conceptUris.length; i++) {
			String conceptUri = conceptUris[i];
			Label label = labels[i];

			JSONObject instanceObject = new JSONObject();
			instanceObject.put("@id", conceptUri);

			JSONObject labelObject = new JSONObject();
			labelObject.put("@type", "skosxl:Label");

			if ((label.getUri() != null) && (label.getUri().trim().length() > 0))
				labelObject.put("@id", label.getUri());
			else
				labelObject.put("@id", conceptUri + "_" + (new Date()).getTime());

			JSONObject literalFormObject = new JSONObject();
			literalFormObject.put("@value", label.getValue());
			literalFormObject.put("@language", label.getLanguageCode());
			JSONArray literalFormArray = new JSONArray();
			literalFormArray.add(literalFormObject);
			labelObject.put("skosxl:literalForm", literalFormArray);

			instanceObject.put("skosxl:prefLabel", labelObject);

			dataArray.add(instanceObject);
		}
		graphObject.put("@graph", dataArray);

		String createLabelsPayload = graphObject.toJSONString().replaceAll("\\/", "/");
		logger.info("createLabels payload: {}", createLabelsPayload);
		Response response = invocationBuilder.post(Entity.entity(createLabelsPayload, "application/ld+json"));

		checkResponseStatus("createLabels", response);

	}

	@SuppressWarnings("unchecked")
	public void createLabel(Concept concept, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", concept.getUri(), relationshipTypeUri, label);

		String url = getModelURL();
		logger.info("createLabel - URL: {}", url);
		
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JSONObject testValue = new JSONObject();
		testValue.put("@id", concept.getUri());
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(relationshipTypeUri)));

		JSONArray valueArray = new JSONArray();
		JSONObject valueObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		typeArray.add("skosxl:Label");
		valueObject.put("@type", typeArray);
		
		JSONArray labelArray = new JSONArray();
		JSONObject labelObject = new JSONObject();
		if ((label.getUri() != null) && (label.getUri().trim().length() > 0))
			labelObject.put("@id", label.getUri());
		labelObject.put("@language", label.getLanguageCode());
		labelObject.put("@value", label.getValue());
		labelArray.add(labelObject);
		valueObject.put("skosxl:literalForm", labelArray);

		valueArray.add(valueObject);
		addOperation.put("value", valueArray);
		operationList.add(addOperation);

		String createLabelPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createLabel payload: {}", createLabelPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createLabelPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("createLabel", response);
	}

	@SuppressWarnings("unchecked")
	public void createRelationship(String relationshipTypeUri, Concept sourceConcept, Concept targetConcept)
			throws OEClientException {
		logger.info("createRelationship entry: {} {} {}", relationshipTypeUri, sourceConcept.getUri(),
				targetConcept.getUri());

		String url = getModelURL();
		logger.info("createRelationship - URL: {}", url);
		
		
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JSONObject valueObject = new JSONObject();
		valueObject.put("@id", sourceConcept.getUri());
		testOperation.put("value", valueObject);
		operationList.add(testOperation);
		
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(relationshipTypeUri)));
		JSONArray targetArray = new JSONArray();
		JSONObject targetObject = new JSONObject();
		targetObject.put("@id", targetConcept.getUri());
		targetArray.add(targetObject);
		addOperation.put("value", targetArray);

		operationList.add(addOperation);

		String createRelationshipPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createRelationship payload: {}", createRelationshipPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createRelationshipPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("createRelationship", response);
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, String metadataValue, String metadataLanguage)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, metadataValue, metadataLanguage);

		String url = getModelURL();
		logger.info("createMetadata - URL: {}", url);
		

		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JSONObject testValue = new JSONObject();
		testValue.put("@id", concept.getUri());
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JSONArray valueArray = new JSONArray();
		JSONObject valueObject = new JSONObject();
		valueObject.put("@language", metadataLanguage);
		valueObject.put("@value", metadataValue);
		
		valueArray.add(valueObject);
		addOperation.put("value", valueArray);
		operationList.add(addOperation);

		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("createMetadata (String)", response);
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, URI uri)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, uri.toString());

		String url = getModelURL();
		logger.info("createMetadata - URL: {}", url);

		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JSONObject valueObject = new JSONObject();
		valueObject.put("@value", uri.toString());
		valueObject.put("@type", "http://www.w3.org/2001/XMLSchema#anyURI");
		addOperation.put("value", valueObject);

		operationList.add(addOperation);

		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("createMetadata (URI)", response);
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, boolean value) throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, value);

		String url = getModelURL();
		
		
		logger.info("createMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		
		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");

		JSONObject testObject = new JSONObject();
		testObject.put("@id", concept.getUri());
		testOperation.put("value", testObject);
		operationList.add(testOperation);
	
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JSONArray valueArray = new JSONArray();
		valueArray.add(value);
		addOperation.put("value", valueArray);

		operationList.add(addOperation);

		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("createMetadata (boolean)", response);
	}
	
	@SuppressWarnings("unchecked")
	public void updateMetadata(Concept concept, String metadataTypeUri, boolean oldValue, boolean newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		String url = getModelURL();
		
		
		logger.info("updateMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		
		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");

		JSONObject testObject1 = new JSONObject();
		testObject1.put("@id", concept.getUri());
		testOperation1.put("value", testObject1);
		operationList.add(testOperation1);
	
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		testOperation2.put("value", oldValue);
		operationList.add(testOperation2);
	
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);
		
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/1/%s/2", getTildered(metadataTypeUri)));
		addOperation.put("value", newValue);
		operationList.add(addOperation);

		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("updateMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("updateMetadata", response);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteMetadata(Concept concept, String metadataTypeUri, boolean oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		String url = getModelURL();
		
		logger.info("deleteMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		
		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");

		JSONObject testObject1 = new JSONObject();
		testObject1.put("@id", concept.getUri());
		testOperation1.put("value", testObject1);
		operationList.add(testOperation1);
	
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		testOperation2.put("value", oldValue);
		operationList.add(testOperation2);
	
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);
		
		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("deleteMetadata (Boolean)", response);
	}
	
	public void deleteConcept(Concept concept) throws OEClientException {
		logger.info("deleteConcept entry: {} {} {}", concept.getUri());

		String url = getApiURL();
		url = url.substring(0, url.length() - 1);
		logger.info("deleteConcept - URL: {}", url);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("mode", "empty");
		
		StringBuilder pathBuilder = new StringBuilder(getModelUri());
		pathBuilder.append("/");
		pathBuilder.append(getEscapedUri(getEscapedUri("<" + concept.getUri() + ">")));
		String path = pathBuilder.toString();
		logger.info("deleteConcept - path: {}", path);
		queryParameters.put("path", path);

		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Response response = invocationBuilder.delete();

		checkResponseStatus("deleteConcept", response);
	}

	@SuppressWarnings("unchecked")
	public void deleteRelationship(String relationshipTypeUri, Concept concept1, Concept concept2)
			throws OEClientException {
		logger.info("deleteRelationship entry: {} {} {}", relationshipTypeUri, concept1.getUri(), concept2.getUri());

		String url = getModelURL();
		logger.info("deleteRelationship - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JSONArray valueArray1 = new JSONArray();
		JSONObject value1 = new JSONObject();
		value1.put("@id", concept1.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToRemove = "@graph/2/" + getTildered(relationshipTypeUri) + "/0";
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JSONArray valueArray2 = new JSONArray();
		JSONObject value2 = new JSONObject();
		value2.put("@id", concept2.getUri()); 
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);
		
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToRemove);
		operationList.add(removeOperation);
		
		String deleteRelationshipPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteRelationship payload: {}", deleteRelationshipPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(deleteRelationshipPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("deleteRelationship", response);
	}

	@SuppressWarnings("unchecked")
	public void deleteMetadata(String metadataTypeUri, Concept concept, String value, String languageCode)
			throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {} {}", metadataTypeUri, concept.getUri(), value, languageCode);

		String url = getModelURL();
		logger.info("deleteMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JSONArray valueArray1 = new JSONArray();
		JSONObject value1 = new JSONObject();
		value1.put("@id", concept.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToRemove = "@graph/2/" + getTildered(metadataTypeUri) + "/0";
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JSONArray valueArray2 = new JSONArray();
		JSONObject value2 = new JSONObject();
		value2.put("@language", languageCode); 
		value2.put("@value", value); 
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);
		
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToRemove);
		operationList.add(removeOperation);
		
		String deleteRelationshipPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteMetadata payload: {}", deleteRelationshipPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(deleteRelationshipPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("deleteMetadata (String)", response);
	}

	@SuppressWarnings("unchecked")
	public void deleteLabel(String relationshipTypeUri, Concept concept, Label label) throws OEClientException {
		logger.info("deleteLabel entry: {} {} {} {}", relationshipTypeUri, concept.getUri(), label);
		
		String url = getModelURL();
		logger.info("deleteLabel - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/5");
		JSONObject value1 = new JSONObject();
		value1.put("@id", concept.getUri()); 
		testOperation1.put("value", value1);
		operationList.add(testOperation1);

		String pathToRemove = "@graph/5/" + getTildered(relationshipTypeUri) + "/0";
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JSONArray valueArray2 = new JSONArray();
		JSONObject value2 = new JSONObject();
		value2.put("@id", label.getUri()); 
		JSONArray typeArray = new JSONArray();
		typeArray.add("skosxl:Label");
		value2.put("@type", typeArray); 
		JSONArray labelArray = new JSONArray();
		JSONObject labelObject = new JSONObject();
		labelObject.put("@value", label.getValue());
		labelObject.put("@language", label.getLanguageCode()); 
		labelArray.add(labelObject);
		value2.put("skosxl:literalForm", labelArray); 
		valueArray2.add(value2);
		testOperation2.put("value", valueArray2);
		operationList.add(testOperation2);
		
		
		JSONObject removeOperation3 = new JSONObject();
		removeOperation3.put("op", "remove");
		removeOperation3.put("path",pathToRemove);
		operationList.add(removeOperation3);

		String deleteLabelPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteLabel payload: {}", deleteLabelPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(deleteLabelPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("deleteLabel", response);
	}

	@SuppressWarnings("unchecked")
	public void addClass(Concept concept, String classUri) throws OEClientException {
		logger.info("addClass entry: {} {}", classUri, concept.getUri());
		populateClasses(concept);
		
		String url = getApiURL();
		
		Map<String, String> queryParameters = new HashMap<String, String>();
		String path = getModelUri() + "/" + getEscapedUri(getEscapedUri("<" + concept.getUri() + ">"));
		queryParameters.put("path", path);
		
		logger.info("addClass - URL: {}", url);
		logger.info("addClass - parameters: {}", queryParameters);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);
		
		JSONArray operationList = new JSONArray();
		if (concept.getClassUris().contains("skos:Concept")) {
			JSONObject testOperation = new JSONObject();
			testOperation.put("op", "test");
			testOperation.put("path","@graph/0/@type/0");
			testOperation.put("value", "skos:Concept");
			operationList.add(testOperation);

			JSONObject removeOperation = new JSONObject();
			removeOperation.put("op", "remove");
			removeOperation.put("path","@graph/0/@type/0");
			operationList.add(removeOperation);
		}
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path","@graph/0/@type/1");
		addOperation.put("value", classUri);
		operationList.add(addOperation);
		
		String addClassPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("addClass payload: {}", addClassPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(addClassPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("addClass", response);
	}

	@SuppressWarnings("unchecked")
	public void removeClass(Concept concept, String classUri) throws OEClientException {
		logger.info("removeClass entry: {} {}", classUri, concept.getUri());
		populateClasses(concept);
		
		if (!concept.getClassUris().contains(classUri)) {
			throw new OEClientException(String.format("Attempting to remove class (%s) that doesn't exist on this concept (%s)", classUri, concept.getUri()));
		}
		
		String url = getApiURL();
		
		Map<String, String> queryParameters = new HashMap<String, String>();
		String path = getModelUri() + "/" + getEscapedUri(getEscapedUri("<" + concept.getUri() + ">"));
		queryParameters.put("path", path);
		
		logger.info("addClass - URL: {}", url);
		logger.info("addClass - parameters: {}", queryParameters);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);
		
		JSONArray operationList = new JSONArray();
		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path","@graph/0/@type/0");
		testOperation.put("value", classUri);
		operationList.add(testOperation);
		
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path","@graph/0/@type/0");
		operationList.add(removeOperation);
		
		if (concept.getClassUris().size() == 1) {
			JSONObject addOperation = new JSONObject();
			addOperation.put("op", "add");
			addOperation.put("path","@graph/0/@type/1");
			addOperation.put("value", "skos:Concept");
			operationList.add(addOperation);
		}
		
		String removeClassPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("removeClass payload: {}", removeClassPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(removeClassPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		checkResponseStatus("deleteRelationship", response);
	}

}
