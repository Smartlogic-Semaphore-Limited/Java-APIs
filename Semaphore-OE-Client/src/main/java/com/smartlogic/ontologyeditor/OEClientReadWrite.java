package com.smartlogic.ontologyeditor;

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

import com.hp.hpl.jena.sparql.util.FmtUtils;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

public class OEClientReadWrite extends OEClientReadOnly {

	/**
	 * createConcept - create a concept as a topConcept of a Concept Scheme
	 *
	 * @param conceptSchemeUri
	 *            - the URI of the concept scheme for which the new concept will
	 *            become a new concept
	 * @param concept
	 *            - the concept to create. The preferred labels and class of
	 *            this concept will be added
	 */
	public void createConcept(String conceptSchemeUri, Concept concept) {
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
		logger.info("createConcept call complete: {}", startDate.getTime());

		/*
		 * Possible response codes are: - 201 in case of success - 409 in case
		 * of constraint violation (if e. g. concept scheme already exists)
		 */
		logger.info("createConcept status: {}", response.getStatus());
		if (logger.isDebugEnabled()) {
			logger.debug("createConcept response: {}", response.readEntity(String.class));
		}

	}

	/**
	 * createConceptScheme - create a concept as a topConcept of a Concept
	 * Scheme
	 *
	 * @param conceptScheme
	 *            - the concept scheme to create, the labels of this concept
	 *            will be created
	 */
	public void createConceptScheme(ConceptScheme conceptScheme) {
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
		logger.info("createConceptScheme call complete: {}", startDate.getTime());

		/*
		 * Possible response codes are: - 201 in case of success - 409 in case
		 * of constraint violation (if e. g. concept scheme already exists)
		 */
		logger.info("createConceptScheme status: {}", response.getStatus());
		if (logger.isDebugEnabled()) {
			logger.debug("createConceptScheme response: {}", response.readEntity(String.class));
		}
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
	 * @throws OEClientException
	 */
	@SuppressWarnings({ "unchecked" })
	public void updateLabel(Label label, String newLabelLanguage, String newLabelValue) throws OEClientException {
		logger.info("updateLabel entry: {}", label.getUri());

		if (label.getUri() == null) {
			throw new OEClientException("Attemping to update label with null URI");
		}
		String processedLabelUri = FmtUtils.stringForURI(label.getUri());
		String escapedLabelUri = getEscapedUri(processedLabelUri);

		String url = String.format("%s/%s", getModelURL(), escapedLabelUri);
		logger.info("updateLabel - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);
		JSONArray operationList = new JSONArray();
		JSONObject testOperation = new JSONObject();
		JSONObject replaceOperation = new JSONObject();

		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0/skosxl:literalForm/0");
		JSONObject oldLabelObject = new JSONObject();
		oldLabelObject.put("@value", label.getValue());
		oldLabelObject.put("@language", label.getLanguageCode());
		testOperation.put("value", oldLabelObject);

		replaceOperation.put("op", "replace");
		replaceOperation.put("path", "@graph/0/skosxl:literalForm/0");
		JSONObject newLabelObject = new JSONObject();
		newLabelObject.put("@value", newLabelValue);
		newLabelObject.put("@language", newLabelLanguage);
		replaceOperation.put("value", newLabelObject);

		operationList.add(testOperation);
		operationList.add(replaceOperation);

		String conceptSchemePayload = operationList.toJSONString();

		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(conceptSchemePayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		if (response.getStatus() == 200) {
			label.setValue(newLabelValue);
			label.setLanguage(newLabelLanguage);
			return;
		}

		throw new OEClientException(
				String.format("%s Response recieved\n%s", response.getStatus(), response.getEntity().toString()));
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
		logger.info("createLabelsPayload payload: {}", createLabelsPayload);
		Response response = invocationBuilder.post(Entity.entity(createLabelsPayload, "application/ld+json"));

		if (response.getStatus() == 201) {
			return;
		}

		String responseString = response.readEntity(String.class);
		logger.warn(responseString);
		throw new OEClientException(String.format("%s Response received\n%s", response.getStatus(), responseString));

	}

	@SuppressWarnings("unchecked")
	public void createLabel(Concept concept, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", concept.getUri(), relationshipTypeUri, label);

		String url = getResourceURL(concept.getUri());
		logger.info("createRelationship - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(relationshipTypeUri)));

		JSONObject labelObject = new JSONObject();
		if ((label.getUri() != null) && (label.getUri().trim().length() > 0))
			labelObject.put("@id", label.getUri());
		else
			labelObject.put("@id", concept.getUri() + "_" + (new Date()).getTime());

		JSONArray typeArray = new JSONArray();
		typeArray.add("skosxl:Label");
		labelObject.put("@type", typeArray);

		JSONObject literalFormObject = new JSONObject();
		literalFormObject.put("@value", label.getValue());
		literalFormObject.put("@language", label.getLanguageCode());
		JSONArray literalFormArray = new JSONArray();
		literalFormArray.add(literalFormObject);
		labelObject.put("skosxl:literalForm", literalFormArray);

		JSONArray valueArray = new JSONArray();
		valueArray.add(labelObject);
		addOperation.put("value", valueArray);

		operationList.add(addOperation);

		String createLabelPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createLabel payload: {}", createLabelPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createLabelPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		if (response.getStatus() == 200) {
			return;
		}

		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));

	}

	@SuppressWarnings("unchecked")
	public void createRelationship(String relationshipTypeUri, Concept sourceConcept, Concept targetConcept)
			throws OEClientException {
		logger.info("createRelationship entry: {} {} {}", relationshipTypeUri, sourceConcept.getUri(),
				targetConcept.getUri());

		String url = getResourceURL(sourceConcept.getUri());
		logger.info("createRelationship - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
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

		if (response.getStatus() == 200) {
			return;
		}

		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataType, String metadataValue, String metadataLanguage)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {} {}", concept.getUri(), metadataType, metadataValue,
				metadataLanguage);

		String url = getResourceURL(concept.getUri());
		logger.info("createMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();
		JSONObject addOperation = new JSONObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataType)));

		if ((metadataLanguage == null) || (metadataLanguage.trim().length() == 0)) {
			addOperation.put("value", metadataValue);
		} else {
			JSONObject valueObject = new JSONObject();
			valueObject.put("@value", metadataValue);
			valueObject.put("@language", metadataLanguage);
			addOperation.put("value", valueObject);
		}
		operationList.add(addOperation);

		String createMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("createMetadata payload: {}", createMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(createMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		if (response.getStatus() == 200) {
			return;
		}

		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));
	}

	public void deleteConcept(Concept concept) throws OEClientException {
		logger.info("deleteConcept entry: {} {} {}", concept.getUri());

		String url = getResourceURL(concept.getUri());
		logger.info("deleteConcept - URL: {}", url);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("mode", "empty");

		Invocation.Builder invocationBuilder = getInvocationBuilder(url, queryParameters);

		Response response = invocationBuilder.delete();

		if (response.getStatus() == 204) {
			return;
		}

		logger.warn(response.readEntity(String.class));
		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));
	}

	@SuppressWarnings("unchecked")
	public void deleteRelationship(String relationshipTypeUri, Concept concept1, Concept concept2)
			throws OEClientException {
		logger.info("deleteRelationship entry: {} {} {}", relationshipTypeUri, concept1.getUri(), concept2.getUri());

		String url = getResourceURL(concept1.getUri());
		logger.info("deleteRelationship - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation = new JSONObject();
		testOperation.put("op", "test");
		testOperation.put("path", String.format("@graph/0/%s/0", getTildered(relationshipTypeUri)));
		JSONObject valueObject1 = new JSONObject();
		valueObject1.put("@id", concept2.getUri());
		testOperation.put("value", valueObject1);
		operationList.add(testOperation);

		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/0/%s/0", getTildered(relationshipTypeUri)));
		operationList.add(removeOperation);

		String deleteRelationshipPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteRelationship payload: {}", deleteRelationshipPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(deleteRelationshipPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		if (response.getStatus() == 200) {
			return;
		}

		logger.warn(response.readEntity(String.class));
		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));
	}

	@SuppressWarnings("unchecked")
	public void deleteMetadata(String metadataTypeUri, Concept concept, String value, String languageCode)
			throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {} {}", metadataTypeUri, concept.getUri(), value, languageCode);

		String url = getResourceURL(concept.getUri());
		logger.info("deleteMetadata - URL: {}", url);
		Invocation.Builder invocationBuilder = getInvocationBuilder(url);

		JSONArray operationList = new JSONArray();

		JSONObject testOperation1 = new JSONObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");
		JSONObject valueObject1 = new JSONObject();
		valueObject1.put("@id", concept.getUri());
		testOperation1.put("value", valueObject1);
		operationList.add(testOperation1);
		
		JSONObject testOperation2 = new JSONObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		JSONObject valueObject2 = new JSONObject();
		valueObject2.put("@value", value);
		valueObject2.put("@language", languageCode);
		testOperation2.put("value", valueObject2);
		operationList.add(testOperation2);
				
		JSONObject removeOperation = new JSONObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/0/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);

		String deleteMetadataPayload = operationList.toJSONString().replaceAll("\\/", "/");
		logger.info("deleteMetadata payload: {}", deleteMetadataPayload);
		Invocation invocation = invocationBuilder.build("PATCH",
				Entity.entity(deleteMetadataPayload, "application/json-patch+json"));
		Response response = invocation.invoke();

		if (response.getStatus() == 200) {
			return;
		}

		logger.warn(response.readEntity(String.class));
		throw new OEClientException(
				String.format("%s Response received\n%s", response.getStatus(), response.getEntity().toString()));
	}

}
