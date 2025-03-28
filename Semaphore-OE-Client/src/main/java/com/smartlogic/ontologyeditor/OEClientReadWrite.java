package com.smartlogic.ontologyeditor;

import java.net.URI;
import java.util.*;

import com.smartlogic.ontologyeditor.beans.*;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import static org.apache.commons.lang3.math.NumberUtils.isDigits;

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


		Date startDate = new Date();
		logger.info("addListener making call  : {} {}", payload, startDate.getTime());
		makeRequest(url, payload, RequestType.PATCH);

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

		JsonObject modelObject = new JsonObject();

		JsonArray modelTypeList = new JsonArray();
		modelTypeList.add("sys:Model");
		modelObject.put("@type", modelTypeList);

		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", model.getLabel().getValue());
		if (model.getLabel().getLanguageCode() != null) {
			labelObject.put("@language", model.getLabel().getLanguageCode());
		}
		modelObject.put("rdfs:label", labelObject);
		
		JsonArray defaultNamespaceList = new JsonArray();
		defaultNamespaceList.add(model.getDefaultNamespace());
		modelObject.put("swa:defaultNamespace", defaultNamespaceList);

		modelObject.put("rdfs:comment", model.getComment());
		String modelPayload = modelObject.toString();

		Date startDate = new Date();
		logger.info("createModel making call  : {} {}", modelPayload, startDate.getTime());
		makeRequest(url, modelPayload, RequestType.POST);

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

		logger.info("deleteModel - about to call");
		makeRequest(url, null, RequestType.DELETE);
		logger.info("deleteModel - call returned");

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

		JsonObject taskObject = new JsonObject();

		JsonArray taskTypeList = new JsonArray();
		taskTypeList.add("sys:Task");
		taskObject.put("@type", taskTypeList);

		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", task.getLabel().getValue());
		if (task.getLabel().getLanguageCode() != null) {
			labelObject.put("@language", task.getLabel().getLanguageCode());
		}
		taskObject.put("rdfs:label", labelObject);

		String taskPayload = taskObject.toString();

		Date startDate = new Date();
		logger.info("createTask making call  : {} {}", taskPayload, startDate.getTime());
		makeRequest(url, taskPayload, RequestType.POST);

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
		
		JsonObject taskObject = new JsonObject();
		JsonObject commitObject = new JsonObject();
		JsonArray typeArray = new JsonArray();
		typeArray.add("sem:Commit");
	    commitObject.put("@type", typeArray);
		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		if (label.getLanguageCode() != null) {
			labelObject.put("@language", label.getLanguageCode());
		}
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
		makeRequest(url, queryParameters, taskPayload, RequestType.POST);

	}

	/**
	 * Helper method to add multiple concepts to model in one method call including metadata.
	 * The concept and metadata lists must be the same size. The correlation between a concept
	 * and a metadata map is by list ordinal.
	 * @param conceptSchemeUri the concept scheme under which the concepts should be added.
	 * @param concepts the List of Concept objects to add.
	 * @param mds the List of optional metadata values to add to each concept. If there is no metadata for a concept,
	 *            add an empty map.
	 */
	public void createConcepts(String conceptSchemeUri, List<Concept> concepts, List<Map<String, Collection<MetadataValue>>> mds) throws OEClientException {
		logger.info("createConcepts entry: scheme uri: {}, concepts: {}, mds: {}", conceptSchemeUri,
				concepts != null ? concepts.toString() : "null",
				mds != null ? mds.toString() : "null");

		if (concepts == null) {
			throw new OEClientException("concepts cannot be null");
		}

		if (mds != null && concepts.size() != mds.size()) {
			throw new OEClientException("The concept list and the metadata list are not the same size.");
		}

		for (int n = 0; n < concepts.size(); n++) {
			try {
				createConcept(conceptSchemeUri, concepts.get(n), mds != null ? mds.get(n) : null);
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concepts.get(n), e);
			}
		}
	}

	/**
	 * Helper method to add multiple concepts to model in one method call.
	 * @param conceptSchemeUri the concept scheme under which the concepts should be added.
	 * @param concepts the set of Concept objects to add.
	 */
	public void createConcepts(String conceptSchemeUri, Set<Concept> concepts) throws OEClientException {
		logger.info("createConcepts entry: scheme uri: {}, concepts: {}", conceptSchemeUri,
				concepts != null ? concepts.toString() : "null");
		if (concepts == null) {
			throw new OEClientException("concepts cannot be null");
		}
		for (Concept concept : concepts) {
			try {
				createConcept(conceptSchemeUri, concept);
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concept, e);
			}
		}
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
		createConcept(conceptSchemeUri, concept, null);
	}

	/**
	 * createConcept - create a concept as a topConcept of a Concept Scheme.
	 * If client is in KRT mode, will also add the new concept to the Newly Created KRT concept scheme,
	 * making it eligible for review.
	 *
	 * @param conceptSchemeUri
	 *            - the URI of the concept scheme for which the new concept will
	 *            become a new concept
	 * @param concept
	 *            - the concept to create. The preferred labels and class of
	 *            this concept will be added
	 * @param metadata
	 *            - optional map of metadata key,value pairs to be added to the concept when it is created.
	 * @throws OEClientException
	 */
	public void createConcept(String conceptSchemeUri, Concept concept, Map<String, Collection<MetadataValue>> metadata) throws OEClientException {
		logger.info("createConcept entry: {} {}", conceptSchemeUri, concept.getUri());

		JsonArray conceptTypeList = new JsonArray();
		conceptTypeList.add("skos:Concept");

		JsonArray labelTypeList = new JsonArray();
		labelTypeList.add("skosxl:Label");

		JsonArray labelLiteralFormDataList = new JsonArray();
		for (Label label : concept.getPrefLabels()) {
			JsonObject labelLiteralFormData = new JsonObject();
			labelLiteralFormData.put("@value", label.getValue());
			if (label.getLanguageCode() != null) {
				labelLiteralFormData.put("@language", label.getLanguageCode());
			}
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

		if (metadata != null && !metadata.isEmpty()) {
			metadata.forEach((key, mdCollectionValue) -> {
				JsonArray jsonMetadataArray = new JsonArray();
				if (mdCollectionValue != null && !mdCollectionValue.isEmpty()) {
					mdCollectionValue.forEach(mdValue -> {
						JsonObject mdObject = new JsonObject();
						mdObject.put("@value", mdValue.getValue());
						if (mdValue.getLanguageCode() != null) {
							mdObject.put("@language", mdValue.getLanguageCode());
						}
						jsonMetadataArray.add(mdObject);
					});
					conceptDetails.put(key, jsonMetadataArray);
				}
			});
		}

		/* if in KRT mode, add new concept to NewlyCreated KRT concept scheme as a top concept */
		if (isKRTClient()) {
			String newlyAddedConceptSchemeUri = getKRTNewlyAddedSchemeUri();
			if (newlyAddedConceptSchemeUri != null) {
				JsonObject newlyCreatedConceptSchemeData = new JsonObject();
				newlyCreatedConceptSchemeData.put("@id", newlyAddedConceptSchemeUri);
				conceptDetails.put("skos:topConceptOf", newlyCreatedConceptSchemeData);
			}
		}

		String conceptSchemePayload = conceptDetails.toString();

		Date startDate = new Date();
		logger.info("createConcept making call  : {}", startDate.getTime());

		makeRequest(getModelURL(), conceptSchemePayload, RequestType.POST);
	}

	/**
	 * Helper method to add multiple concepts to model in one method call including metadata.
	 * The concept and metadata lists must be the same size. The correlation between a concept
	 * and a metadata map is by list ordinal.
	 * @param parentConceptUri the URI of the parent concept under which the concepts should be added.
	 * @param concepts the List of Concept objects to add.
	 * @param mds the List of optional metadata values to add to each concept. If there is no metadata for a concept,
	 *            add an empty map.
	 */
	public void createConceptsBelowConcept(String parentConceptUri, List<Concept> concepts, List<Map<String, Collection<MetadataValue>>> mds) throws OEClientException {
		logger.info("createConcepts entry: parent concept uri: {}, concepts: {}, mds: {}", parentConceptUri,
				concepts != null ? concepts.toString() : "null",
				mds != null ? mds.toString() : "null");

		if (concepts == null) {
			throw new OEClientException("concepts set cannot be null");
		}

		if (mds != null && (concepts.size() != mds.size())) {
			throw new OEClientException("The concept list and the metadata list are not the same size.");
		}

		for (int n = 0; n < concepts.size(); n++) {
			try {
				createConceptBelowConcept(parentConceptUri, concepts.get(n), mds != null ? mds.get(n) : null);
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concepts.get(n), e);
			}
		}
	}

	/**
	 * Create multiple concepts below the specified concept.
	 * @param parentConceptUri the pareant concept uri
	 * @param concepts the set of concepts to create below the parent
	 * @throws OEClientException excetion
	 */
	public void createConceptsBelowConcept(String parentConceptUri, Set<Concept> concepts) throws OEClientException {
		logger.info("createConceptsBelowConcept entry: parent concept uri: {}, concepts: {}", parentConceptUri,
				concepts != null ? concepts.toString() : "null");
		if (concepts == null) {
			throw new OEClientException("concepts set cannot be null");
		}
		for (Concept concept : concepts) {
			try {
				createConceptBelowConcept(parentConceptUri, concept);
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concept, e);
			}
		}
	}

	public void createConceptBelowConcept(String parentConceptUri, Concept concept) throws OEClientException {
		createConceptBelowConcept(parentConceptUri, concept, null);
	}

	public void createConceptBelowConcept(String parentConceptUri, Concept concept, Map<String, Collection<MetadataValue>> metadata) throws OEClientException {
		logger.info("createConceptBelowConcept entry: {} {} {}", parentConceptUri, concept.getUri(), metadata == null ? "" : metadata.keySet());

		JsonArray conceptTypeList = new JsonArray();
		conceptTypeList.add("skos:Concept");

		JsonArray labelTypeList = new JsonArray();
		labelTypeList.add("skosxl:Label");

		JsonArray labelLiteralFormDataList = new JsonArray();
		for (Label label : concept.getPrefLabels()) {
			JsonObject labelLiteralFormData = new JsonObject();
			labelLiteralFormData.put("@value", label.getValue());
			if (label.getLanguageCode() != null) {
				labelLiteralFormData.put("@language", label.getLanguageCode());
			}
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

		if (metadata != null && !metadata.isEmpty()) {
			metadata.forEach((key, mdCollectionValue) -> {
				JsonArray jsonMetadataArray = new JsonArray();
				if (mdCollectionValue != null && !mdCollectionValue.isEmpty()) {
					mdCollectionValue.forEach(mdValue -> {
						JsonObject mdObject = new JsonObject();
						mdObject.put("@value", mdValue.getValue());
						if (mdValue.getLanguageCode() != null) {
							mdObject.put("@language", mdValue.getLanguageCode());
						}
						jsonMetadataArray.add(mdObject);
					});
					conceptDetails.put(key, jsonMetadataArray);
				}
			});
		}

		/* if in KRT mode, add new concept to NewlyCreate KRT concept scheme as a top concept */
		if (isKRTClient()) {
			String newlyAddedConceptSchemeUri = getKRTNewlyAddedSchemeUri();
			if (newlyAddedConceptSchemeUri != null) {
				JsonObject newlyCreatedConceptSchemeData = new JsonObject();
				newlyCreatedConceptSchemeData.put("@id", newlyAddedConceptSchemeUri);
				conceptDetails.put("skos:topConceptOf", newlyCreatedConceptSchemeData);
			}
		}

		String conceptPayload = conceptDetails.toString();

		logger.info("createConceptBelowConcept making call with payload: {}", conceptPayload);
		makeRequest(getModelURL(), conceptPayload, RequestType.POST);

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

		JsonObject conceptSchemeDetails = new JsonObject();

		JsonArray conceptSchemeTypeList = new JsonArray();
		conceptSchemeTypeList.add("skos:ConceptScheme");
		conceptSchemeDetails.put("@type", conceptSchemeTypeList);

		JsonArray newconceptSchemeLabelDataList = new JsonArray();
		for (Label label : conceptScheme.getPrefLabels()) {
			JsonObject newconceptSchemeLabelData = new JsonObject();
			newconceptSchemeLabelData.put("@value", label.getValue());
			if (label.getLanguageCode() != null) {
				newconceptSchemeLabelData.put("@language", label.getLanguageCode());
			}
			newconceptSchemeLabelDataList.add(newconceptSchemeLabelData);
		}

		conceptSchemeDetails.put("rdfs:label", newconceptSchemeLabelDataList);
		conceptSchemeDetails.put("@id", conceptScheme.getUri());

		String conceptSchemePayload = conceptSchemeDetails.toString();

		Date startDate = new Date();
		logger.info("createConceptScheme making call  : {}", startDate.getTime());
		makeRequest(getModelURL(), conceptSchemePayload, RequestType.POST);
	}

	/**
	 * Update a label object of a specified label type.
	 * This version of the  method works with KRT mode enabled.
	 *
	 * @param label
	 *            - the label to be updated. The URI, language and value of this
	 *            label must be defined. The value and language code must match
	 *            the values in the Ontology.
	 * @param relationshipTypeUri the URI of the label type
	 * @param newLabelLanguage
	 *            - the new language for the label
	 * @param newLabelValue
	 *            - the new value for the label
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	@SuppressWarnings({ "unchecked" })
	public void updateLabel(Label label, String conceptUri, String relationshipTypeUri, String newLabelLanguage, String newLabelValue) throws OEClientException {
		logger.info("updateLabel (with type) entry: {}, rel type uri: {}", label.getUri(), relationshipTypeUri);


		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("path", getPathParameter(conceptUri));

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path",String.format("@graph/0/%s/1", relationshipTypeUri));
		JsonArray valueArray1 = new JsonArray();
		JsonObject value1 = new JsonObject();
		value1.put("@id", label.getUri());
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);

		String pathToRemove = String.format("@graph/0/%s/1/skosxl:literalForm/2", relationshipTypeUri);
		String pathToAdd = String.format("@graph/0/%s/1/skosxl:literalForm/3", relationshipTypeUri);

		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JsonArray valueArray2 = new JsonArray();
		JsonObject value2 = new JsonObject();
		if (label.getLanguageCode() != null) {
			value2.put("@language", label.getLanguageCode());
		}
		value2.put("@value", label.getValue());
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToRemove);
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", pathToAdd);
		JsonObject value3 = new JsonObject();
		if (label.getLanguageCode() != null) {
			value3.put("@language", newLabelLanguage);
		}
		value3.put("@value", newLabelValue);
		addOperation.put("value", value3);
		operationList.add(addOperation);

		checkKRTModified(operationList, "0", "4");

		String updateLabelPayload = operationList.toString();
		logger.info("updateLabel payload: {}", updateLabelPayload);
		makeRequest(getApiURL(), queryParameters, updateLabelPayload, RequestType.PATCH);
	}


	/**
	 * Update a label object. This method does NOT work with KRT. Use the variant of the method
	 * where the label type is specified.
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


		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JsonArray valueArray1 = new JsonArray();
		JsonObject value1 = new JsonObject();
		value1.put("@id", label.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToUpdate = "@graph/2/skosxl:literalForm/0";

		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToUpdate);
		JsonArray valueArray2 = new JsonArray();
		JsonObject value2 = new JsonObject();
		if (label.getLanguageCode() != null) {
			value2.put("@language", label.getLanguageCode());
		}
		value2.put("@value", label.getValue());
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToUpdate);
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", pathToUpdate);
		JsonArray valueArray3 = new JsonArray();
		JsonObject value3 = new JsonObject();
		if (label.getLanguageCode() != null) {
			value3.put("@language", newLabelLanguage);
		}
		value3.put("@value", newLabelValue);
		valueArray3.add(value3);
		addOperation.put("value", valueArray3);
		operationList.add(addOperation);

		String updateLabelPayload = operationList.toString();
		logger.info("updateLabel payload: {}", updateLabelPayload);
		makeRequest(getModelURL(), updateLabelPayload, RequestType.PATCH);
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


		JsonObject graphObject = new JsonObject();

		JsonArray dataArray = new JsonArray();
		for (int i = 0; i < conceptUris.length; i++) {
			String conceptUri = conceptUris[i];
			Label label = labels[i];

			JsonObject instanceObject = new JsonObject();
			instanceObject.put("@id", conceptUri);

			JsonObject labelObject = new JsonObject();
			labelObject.put("@type", "skosxl:Label");

			if ((label.getUri() != null) && (label.getUri().trim().length() > 0))
				labelObject.put("@id", label.getUri());
			else
				labelObject.put("@id", conceptUri + "_" + (new Date()).getTime());

			JsonObject literalFormObject = new JsonObject();
			literalFormObject.put("@value", label.getValue());
			if (label.getLanguageCode() != null) {
				literalFormObject.put("@language", label.getLanguageCode());
			}
			JsonArray literalFormArray = new JsonArray();
			literalFormArray.add(literalFormObject);
			labelObject.put("skosxl:literalForm", literalFormArray);

			instanceObject.put("skosxl:prefLabel", labelObject);

			/* if a KRT client, add the parent concept to the Modified scheme */
			if (isKRTClient()) {
				String modifiedSchemeUri = getKRTModifiedSchemeUri();
				if (null != modifiedSchemeUri) {
					instanceObject.put("skos:topConceptOf", modifiedSchemeUri);
				}
			}

			dataArray.add(instanceObject);
		}
		graphObject.put("@graph", dataArray);

		String createLabelsPayload = graphObject.toString();
		logger.info("createLabels payload: {}", createLabelsPayload);
		makeRequest(getModelURL(), createLabelsPayload, RequestType.POST);

	}

	/**
	 * Create/add a label to an existing concept. This call dispatches to method of same
	 * name with concept URI as first argument. It is a wrapper.
	 *
	 * @param concept the concept
	 * @param relationshipTypeUri the relationship type uri
	 * @param label the label object
	 * @throws OEClientException the exception
	 */
	public void createLabel(Concept concept, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", concept, relationshipTypeUri, label);
		createLabel(concept.getUri(), relationshipTypeUri, label);
	}

	/**
	 * Create/add a label to an existing concept with the specified URI.
	 * @param conceptUri the concept URI
	 * @param relationshipTypeUri the relationship type URI
	 * @param label the label object
	 * @throws OEClientException exception
	 */
	public void createLabel(String conceptUri, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", conceptUri, relationshipTypeUri, label);

		JsonArray operationList = new JsonArray();
		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JsonObject valueObject = new JsonObject();
		valueObject.put("@id", conceptUri);
		testOperation.put("value", valueObject);
		operationList.add(testOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(relationshipTypeUri)));

		JsonObject value2Object = new JsonObject();
		JsonArray value2Typearray = new JsonArray();
		value2Typearray.add("skosxl:Label");
		value2Object.put("@type", value2Typearray);

		JsonArray litFormArray = new JsonArray();
		JsonObject litFormObject = new JsonObject();
		litFormObject.put("@value", label.getValue());
		if (label.getLanguageCode() != null) {
			litFormObject.put("@language", label.getLanguageCode());
		}
		litFormArray.add(litFormObject);
		value2Object.put("skosxl:literalForm", litFormArray);
		addOperation.put("value", value2Object);

		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createRelationshipPayload = operationList.toString();
		logger.info("createRelationship payload: {}", createRelationshipPayload);
		makeRequest(getModelURL(), createRelationshipPayload, RequestType.PATCH);

	}

	@SuppressWarnings("unchecked")
	public void createRelationship(String relationshipTypeUri, Concept sourceConcept, Concept targetConcept)
			throws OEClientException {
		logger.info("createRelationship entry: {} {} {}", relationshipTypeUri, sourceConcept.getUri(),
				targetConcept.getUri());


		JsonArray operationList = new JsonArray();
		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JsonObject valueObject = new JsonObject();
		valueObject.put("@id", sourceConcept.getUri());
		testOperation.put("value", valueObject);
		operationList.add(testOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(relationshipTypeUri)));
		JsonArray targetArray = new JsonArray();
		JsonObject targetObject = new JsonObject();
		targetObject.put("@id", targetConcept.getUri());
		targetArray.add(targetObject);
		addOperation.put("value", targetArray);

		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createRelationshipPayload = operationList.toString();
		logger.info("createRelationship payload: {}", createRelationshipPayload);
		makeRequest(getModelURL(), createRelationshipPayload, RequestType.PATCH);

	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, String metadataValue, String metadataLanguage)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, metadataValue, metadataLanguage);

		JsonArray operationList = new JsonArray();
		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JsonObject testValue = new JsonObject();
		testValue.put("@id", concept.getUri());
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JsonArray valueArray = new JsonArray();
		JsonObject valueObject = new JsonObject();
		valueObject.put("@language", metadataLanguage);
		valueObject.put("@value", metadataValue);
		
		valueArray.add(valueObject);
		addOperation.put("value", valueArray);
		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createMetadataPayload = operationList.toString();
		logger.info("createMetadata payload: {}", createMetadataPayload);

		makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH);
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, URI uri)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, uri.toString());

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("path", getPathParameter(concept.getUri()));

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JsonObject testValue = new JsonObject();
		testValue.put("@id", concept.getUri());
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", uri.toString());
		valueObject.put("@type", "xsd:anyURI");
		addOperation.put("value", valueObject);

		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createMetadataPayload = operationList.toString();
		logger.info("createMetadata payload: {}", createMetadataPayload);
		makeRequest(getApiURL(), queryParameters, createMetadataPayload, RequestType.PATCH);
	}

	@SuppressWarnings("unchecked")
	public void createMetadata(Concept concept, String metadataTypeUri, boolean value) throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, value);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");

		JsonObject testObject = new JsonObject();
		testObject.put("@id", concept.getUri());
		testOperation.put("value", testObject);
		operationList.add(testOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/0/%s/-", getTildered(metadataTypeUri)));

		JsonArray valueArray = new JsonArray();
		valueArray.add(value);
		addOperation.put("value", valueArray);

		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createMetadataPayload = operationList.toString();
		logger.info("createMetadata payload: {}", createMetadataPayload);

		makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH );
	}

	public void updateMetadata(Concept concept, String metadataTypeUri, String oldValueLanguage, String oldValue, String newValueLanguage, String newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");

		JsonObject testObject1 = new JsonObject();
		testObject1.put("@id", concept.getUri());
		testOperation1.put("value", testObject1);
		operationList.add(testOperation1);

		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		JsonObject oldValueJsonObject = new JsonObject();
		if (oldValueLanguage != null)
			oldValueJsonObject.put("@language", oldValueLanguage);
		oldValueJsonObject.put("@value", oldValue);
		testOperation2.put("value", oldValueJsonObject);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/1/%s/2", getTildered(metadataTypeUri)));
		JsonObject newValueJsonObject = new JsonObject();
		if (newValueLanguage != null)
			newValueJsonObject.put("@language", newValueLanguage);
		newValueJsonObject.put("@value", newValue);
		addOperation.put("value", newValueJsonObject);
		operationList.add(addOperation);

		checkKRTModified(operationList, "1");

		String createMetadataPayload = operationList.toString();
		logger.info("updateMetadata payload: {}", createMetadataPayload);
		makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH );

	}

	@SuppressWarnings("unchecked")
	public void updateMetadata(Concept concept, String metadataTypeUri, boolean oldValue, boolean newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);


		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");

		JsonObject testObject1 = new JsonObject();
		testObject1.put("@id", concept.getUri());
		testOperation1.put("value", testObject1);
		operationList.add(testOperation1);

		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		testOperation2.put("value", oldValue);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/1/%s/2", getTildered(metadataTypeUri)));
		addOperation.put("value", newValue);
		operationList.add(addOperation);

		checkKRTModified(operationList, "1");

		String createMetadataPayload = operationList.toString();
		logger.info("updateMetadata payload: {}", createMetadataPayload);
		makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH );

	}
	
	@SuppressWarnings("unchecked")
	public void deleteMetadata(Concept concept, String metadataTypeUri, boolean oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		String url = getModelURL();
		
		logger.info("deleteMetadata - URL: {}", url);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path", "@graph/1");

		JsonObject testObject1 = new JsonObject();
		testObject1.put("@id", concept.getUri());
		testOperation1.put("value", testObject1);
		operationList.add(testOperation1);

		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		testOperation2.put("value", oldValue);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);
		
		String createMetadataPayload = operationList.toString();
		logger.info("deleteMetadata payload: {}", createMetadataPayload);
		makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH );
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

		makeRequest(url, queryParameters, null, RequestType.DELETE );

	}

	@SuppressWarnings("unchecked")
	public void deleteRelationship(String relationshipTypeUri, Concept concept1, Concept concept2)
			throws OEClientException {
		logger.info("deleteRelationship entry: {} {} {}", relationshipTypeUri, concept1.getUri(), concept2.getUri());

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JsonArray valueArray1 = new JsonArray();
		JsonObject value1 = new JsonObject();
		value1.put("@id", concept1.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToRemove = "@graph/2/" + getTildered(relationshipTypeUri) + "/0";
		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JsonArray valueArray2 = new JsonArray();
		JsonObject value2 = new JsonObject();
		value2.put("@id", concept2.getUri()); 
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToRemove);
		operationList.add(removeOperation);
		
		String deleteRelationshipPayload = operationList.toString();
		logger.info("deleteRelationship payload: {}", deleteRelationshipPayload);
		makeRequest(getModelURL(), deleteRelationshipPayload, RequestType.PATCH );
	}

	@SuppressWarnings("unchecked")
	public void deleteMetadata(String metadataTypeUri, Concept concept, String value, String languageCode)
			throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {} {}", metadataTypeUri, concept.getUri(), value, languageCode);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/2");
		JsonArray valueArray1 = new JsonArray();
		JsonObject value1 = new JsonObject();
		value1.put("@id", concept.getUri()); 
		valueArray1.add(value1);
		testOperation1.put("value", value1);
		operationList.add(testOperation1);
		
		String pathToRemove = "@graph/2/" + getTildered(metadataTypeUri) + "/0";
		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JsonArray valueArray2 = new JsonArray();
		JsonObject value2 = new JsonObject();
		value2.put("@language", languageCode); 
		value2.put("@value", value); 
		valueArray2.add(value2);
		testOperation2.put("value", value2);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", pathToRemove);
		operationList.add(removeOperation);
		
		String deleteRelationshipPayload = operationList.toString();
		logger.info("deleteMetadata payload: {}", deleteRelationshipPayload);
		makeRequest(getModelURL(), deleteRelationshipPayload, RequestType.PATCH );

	}

	@SuppressWarnings("unchecked")
	public void deleteLabel(String relationshipTypeUri, Concept concept, Label label) throws OEClientException {
		logger.info("deleteLabel entry: {} {} {} {}", relationshipTypeUri, concept.getUri(), label);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation1 = new JsonObject();
		testOperation1.put("op", "test");
		testOperation1.put("path","@graph/5");
		JsonObject value1 = new JsonObject();
		value1.put("@id", concept.getUri()); 
		testOperation1.put("value", value1);
		operationList.add(testOperation1);

		String pathToRemove = "@graph/5/" + getTildered(relationshipTypeUri) + "/0";
		JsonObject testOperation2 = new JsonObject();
		testOperation2.put("op", "test");
		testOperation2.put("path",pathToRemove);
		JsonArray valueArray2 = new JsonArray();
		JsonObject value2 = new JsonObject();
		value2.put("@id", label.getUri());
		JsonArray typeArray = new JsonArray();
		typeArray.add("skosxl:Label");
		value2.put("@type", typeArray);
		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", label.getValue());
		if (label.getLanguageCode() != null ) {
			labelObject.put("@language", label.getLanguageCode());
		}
		labelArray.add(labelObject);
		value2.put("skosxl:literalForm", labelArray); 
		valueArray2.add(value2);
		testOperation2.put("value", valueArray2);
		operationList.add(testOperation2);


		JsonObject removeOperation3 = new JsonObject();
		removeOperation3.put("op", "remove");
		removeOperation3.put("path",pathToRemove);
		operationList.add(removeOperation3);

		String deleteLabelPayload = operationList.toString();
		logger.info("deleteLabel payload: {}", deleteLabelPayload);
		makeRequest(getModelURL(), deleteLabelPayload, RequestType.PATCH );
	}

	@SuppressWarnings("unchecked")
	public void addClass(Concept concept, String classUri) throws OEClientException {
		logger.info("addClass entry: {} {}", classUri, concept.getUri());
		populateClasses(concept);
		
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("path", getPathParameter(concept.getUri()));

		JsonArray operationList = new JsonArray();
		if (concept.getClassUris().contains("skos:Concept")) {
			JsonObject testOperation = new JsonObject();
			testOperation.put("op", "test");
			testOperation.put("path","@graph/0/@type/0");
			testOperation.put("value", "skos:Concept");
			operationList.add(testOperation);

			JsonObject removeOperation = new JsonObject();
			removeOperation.put("op", "remove");
			removeOperation.put("path","@graph/0/@type/0");
			operationList.add(removeOperation);
		}
		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path","@graph/0/@type/1");
		addOperation.put("value", classUri);
		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String addClassPayload = operationList.toString();
		logger.info("addClass payload: {}", addClassPayload);
		makeRequest(getApiURL(), queryParameters, addClassPayload, RequestType.PATCH );

	}

	@SuppressWarnings("unchecked")
	public void removeClass(Concept concept, String classUri) throws OEClientException {
		logger.info("removeClass entry: {} {}", classUri, concept.getUri());
		populateClasses(concept);
		
		if (!concept.getClassUris().contains(classUri)) {
			throw new OEClientException(String.format("Attempting to remove class (%s) that doesn't exist on this concept (%s)", classUri, concept.getUri()));
		}

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("path", getPathParameter(concept.getUri()));


		JsonArray operationList = new JsonArray();
		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path","@graph/0/@type/0");
		testOperation.put("value", classUri);
		operationList.add(testOperation);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path","@graph/0/@type/0");
		operationList.add(removeOperation);
		
		if (concept.getClassUris().size() == 1) {
			JsonObject addOperation = new JsonObject();
			addOperation.put("op", "add");
			addOperation.put("path","@graph/0/@type/1");
			addOperation.put("value", "skos:Concept");
			operationList.add(addOperation);
		}
		
		String removeClassPayload = operationList.toString();
		logger.info("removeClass payload: {}", removeClassPayload);
		makeRequest(getApiURL(), queryParameters, removeClassPayload, RequestType.PATCH );
	}

	/**
	 * Checks if the client is in KRT mode, and if so, add the concept to the Modified KRT concept scheme.
	 * @param operationList the JSON PATCH operation list object
	 * @param conceptIndex the JSON PATCH index of the concept.
	 * @throws OEClientException exception
	 */
	protected void checkKRTModified(JsonArray operationList, String conceptIndex) throws OEClientException {

		if (conceptIndex != null && !isDigits(conceptIndex)) throw new OEClientException("Invalid concept index: " + conceptIndex);

		/* if a KRT client, add the parent concept to the Modified scheme */
		if (isKRTClient()) {
			String modifiedSchemeUri = getKRTModifiedSchemeUri();
			if (null != modifiedSchemeUri) {
				addToKRTModified(modifiedSchemeUri, operationList, conceptIndex, "-");
			}
		}

	}

	/**
	 * Checks if the client is in KRT mode, and if so, add the concept to the Modified KRT concept scheme.
	 * @param operationList the JSON PATCH operation list object
	 * @param conceptIndex the JSON PATCH index of the concept.
	 * @throws OEClientException exception
	 */
	protected void checkKRTModified(JsonArray operationList, String conceptIndex, String schemeIndex) throws OEClientException {

		if (conceptIndex != null && !isDigits(conceptIndex)) throw new OEClientException("Invalid concept index: " + conceptIndex);

		/* if a KRT client, add the parent concept to the Modified scheme */
		if (isKRTClient()) {
			String modifiedSchemeUri = getKRTModifiedSchemeUri();
			if (null != modifiedSchemeUri) {
				addToKRTModified(modifiedSchemeUri, operationList, conceptIndex, schemeIndex);
			}
		}

	}

	/**
	 * Add an operation for JSON patch to attach the concept context to the specified concept scheme URI.
	 * @param conceptSchemeUri the KRT concept scheme to attach the concept to
	 * @param operationList the operation list being used for call construction.
	 * @param conceptIndex the JSON-PATCH index of the concept. Defaults to zero.
	 * @param schemeIndex the concept scheme index in the JSON PATCH
	 */
	protected void addToKRTModified(String conceptSchemeUri, JsonArray operationList, String conceptIndex, String schemeIndex) throws OEClientException {
		if (null == conceptIndex || conceptIndex.isEmpty() || !isDigits(conceptIndex)) {
			throw new OEClientException("Invalid concept index: " + conceptIndex);
		}
		if (null == schemeIndex || schemeIndex.isEmpty() || (!schemeIndex.equals("-") && !isDigits(schemeIndex))) {
			throw new OEClientException("Invalid concept scheme index: " + schemeIndex);
		}
		JsonObject addTopConceptObject = new JsonObject();
		addTopConceptObject.put("op", "add");
		addTopConceptObject.put("path", String.format("@graph/%s/skos:topConceptOf/%s", conceptIndex, schemeIndex));
		JsonObject addTopConceptValueObject = new JsonObject();
		addTopConceptValueObject.put("@id", conceptSchemeUri);
		addTopConceptObject.put("value", addTopConceptValueObject);
		operationList.add(addTopConceptObject);
	}
}