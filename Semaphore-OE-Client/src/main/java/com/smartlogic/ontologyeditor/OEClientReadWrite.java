// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smartlogic.ontologyeditor.beans.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

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
	 * createModel - create a model
	 * @param model - the model to be created
	 * @return the URI of the newly created model from the x-location-uri header, or null
	 * @throws OEClientException  - an error has occurred contacting the server
	 */
	public String createModel(Model model) throws OEClientException {
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

		if (model.getComment() != null ) {
			modelObject.put("rdfs:comment", model.getComment());
		}
		String modelPayload = modelObject.toString();

		Date startDate = new Date();
		logger.info("createModel making call  : {} {}", modelPayload, startDate.getTime());
		return makeRequest(url, modelPayload, RequestType.POST);

	}

	/**
	 * Link another model into the current model as an {@code owl:imports} entry.
	 *
	 * @param importedModelUri the URI of the model to import
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void linkModel(String importedModelUri) throws OEClientException {

		logger.info("linkModel importing URL: {}", importedModelUri);

		String url = getModelURL() + "/" + getModelUri();

		JsonArray jsonArray = new JsonArray();

		JsonObject addObject = new JsonObject();
		addObject.put("op", "add");
		addObject.put("path", "@graph/0/owl:imports/1");

		JsonObject linkedModel = new JsonObject();
		linkedModel.put("@id", importedModelUri);

		addObject.put("value", linkedModel);

		jsonArray.add(addObject);
		String modelPayload = jsonArray.toString();

		Date startDate = new Date();
		logger.info("linkModel making call  : {} {} {}", modelPayload, url, startDate.getTime());
		makeRequest(url, modelPayload, RequestType.PATCH);

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
	 * deleteTask - delete a task (and its associated graph) from the current model
	 * @param task - the task to be deleted
	 * @throws OEClientException  - an error has occurred contacting the server
	 */
	public void deleteTask(Task task) throws OEClientException {
		logger.info("deleteTask entry: {}", task);

		String url = getTaskSysURL(task);
		logger.info("deleteTask URL: {}", url);

		logger.info("deleteTask - about to call");
		makeRequest(url, null, RequestType.DELETE);
		logger.info("deleteTask - call returned");

	}

	/**
	 * createTask - create a task within the current model
	 * @param task 
	 *          - the task to be created
	 * @return the URI of the newly created task from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createTask(Task task) throws OEClientException {
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
		return makeRequest(url, taskPayload, RequestType.POST);

	}

	/**
	 * createTaskAndReturn - create a task and return a new task instance with server-assigned identifiers.
	 * This method does not mutate the input task.
	 *
	 * @param task
	 *          - the task to be created
	 * @return a new Task containing the created task id/graphUri when available
	 * @throws OEClientException
	 */
	public Task createTaskAndReturn(Task task) throws OEClientException {
		String createdTaskUri = createTask(task);
		if (createdTaskUri == null) {
			throw new OEClientException("Task creation did not return a URI — cannot resolve the created task");
		}

		for (Task existingTask : getAllTasks()) {
			if (createdTaskUri.equals(existingTask.getId()) || createdTaskUri.equals(existingTask.getGraphUri())) {
				return existingTask;
			}
		}

		logger.warn("createTaskAndReturn could not resolve graphUri for task URI: {}", createdTaskUri);
		return new Task(task.getLabel(), createdTaskUri, createdTaskUri);
	}

	public void commitTask(Task task) throws OEClientException {
		Label label = new Label("en", "Commit added via API");
		String comment = "No comment supplied";
		commitTask(task, label, comment);
	}

	/**
	 * commitTask - commit all of the task's uncommitted changes to master.
	 * @param task - the task whose changes are to be committed
	 * @param label - the label (title) for the resulting commit
	 * @param comment - the comment (description) for the resulting commit
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void commitTask(Task task, Label label, String comment) throws OEClientException {
		commitTask(task, label, comment, null);
	}

	/**
	 * commitTask - commit the task's uncommitted changes to master, optionally limited to only
	 * those changes made on or before a cut-off date. Changes made after the cut-off date are left
	 * uncommitted on the task.
	 * @param task - the task whose changes are to be committed
	 * @param label - the label (title) for the resulting commit
	 * @param comment - the comment (description) for the resulting commit
	 * @param upToDate - if non-null, only commit changes created on or before this date; if null,
	 *          all uncommitted changes are committed
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void commitTask(Task task, Label label, String comment, Date upToDate) throws OEClientException {
		logger.info("commitTask entry: {} upToDate: {}", task, upToDate);

		String url = getTaskSysURL(task) + "/teamwork:Change/rdf:instance";
		logger.info("commitTask URL: {}", url);
		
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("action", "commit");
		queryParameters.put("checkConstraints", "true");
		queryParameters.put(PARAM_FILTERS, buildCommitFilters(upToDate));
		queryParameters.put("sparqlFilter", "not exists { ?subject sem:accepted false }");
		if (label.getLanguageCode() != null) {
			queryParameters.put("language", label.getLanguageCode());
		}
		
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
		if (label.getLanguageCode() != null) {
			commentObject.put("@language", label.getLanguageCode());
		} else {
			commentObject.put("@language", "");
		}
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
	 * Builds the "filters" query parameter value used to select which of a task's uncommitted
	 * changes should be committed. Always restricts to changes with status
	 * {@code teamwork:Uncommitted}; when {@code upToDate} is supplied an additional filter is
	 * added so that only changes created on or before that date are included, leaving later
	 * changes uncommitted on the task.
	 *
	 * @param upToDate - the (inclusive) cut-off date, or null to commit all uncommitted changes
	 * @return the comma-separated filters expression to send to the KMM API
	 */
	private String buildCommitFilters(Date upToDate) {
		String statusFilter = "subject(teamwork:status = teamwork:Uncommitted)";
		if (upToDate == null) {
			return statusFilter;
		}
		String cutoffFilter = String.format("subject(dcterms:created <= \"%s\"^^xsd:dateTime)",
				upToDate.toInstant().toString());
		return cutoffFilter + "," + statusFilter;
	}

	/**
	 * createClass - create a class
	 * @param label the label for the class
	 * @param classUri the URI of the class
	 * @param superClasses the super classes
	 * @return the URI of the newly created class from the x-location-uri header, or null
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createClass(Label label, String classUri, ConceptClass[] superClasses) throws OEClientException {
		logger.info("createClass entry: {} {}", label, classUri);

		JsonObject classObject = new JsonObject();
		classObject.put("@id", classUri);
		JsonArray typeArray = new JsonArray();
		typeArray.add("owl:Class");
		classObject.put("@type", typeArray);

		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		if (label.getLanguageCode() != null) {
			labelObject.put("@language", label.getLanguageCode());
		}
		labelObject.put("@value", label.getValue());
		labelArray.add(labelObject);
		classObject.put("rdfs:label", labelArray);

		JsonArray superClassArray = new JsonArray();
		if ((superClasses == null) || (superClasses.length == 0)) {
			JsonObject superClassObject = new JsonObject();
			superClassObject.put("@id", "skos:Concept");
			superClassArray.add(superClassObject);
		} else {
			for (ConceptClass superClass : superClasses) {
				JsonObject superClassObject = new JsonObject();
				superClassObject.put("@id", superClass.getUri());
				superClassArray.add(superClassObject);
			}
		}
		classObject.put("rdfs:subClassOf", superClassArray);
		String classPayload = classObject.toString();

		Date startDate = new Date();
		logger.info("commitTask making call  : {} {}", classPayload, startDate.getTime());
		return makeRequest(getModelURL(), classPayload, RequestType.POST);



	}
	/**
	 * Helper method to add multiple concepts to model in one method call including metadata.
	 * The concept and metadata lists must be the same size. The correlation between a concept
	 * and a metadata map is by list ordinal.
	 * @param conceptSchemeUri the concept scheme under which the concepts should be added.
	 * @param concepts the List of Concept objects to add.
	 * @param mds the List of optional metadata values to add to each concept. If there is no metadata for a concept,
	 *            add an empty map.
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 */
	public String createConcepts(String conceptSchemeUri, List<Concept> concepts, List<Map<String, Collection<MetadataValue>>> mds) throws OEClientException {
		logger.info("createConcepts entry: scheme uri: {}, concepts: {}, mds: {}", conceptSchemeUri,
				concepts != null ? concepts.toString() : "null",
				mds != null ? mds.toString() : "null");

		if (concepts == null) {
			throw new OEClientException("concepts cannot be null");
		}

		if (mds != null && concepts.size() != mds.size()) {
			throw new OEClientException("The concept list and the metadata list are not the same size.");
		}

		String firstUri = null;
		for (int n = 0; n < concepts.size(); n++) {
			try {
				String uri = createConcept(conceptSchemeUri, concepts.get(n), mds != null ? mds.get(n) : null);
				if (n == 0) {
					firstUri = uri;
				}
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concepts.get(n), e);
			}
		}
		return firstUri;
	}

	/**
	 * Helper method to add multiple concepts to model in one method call.
	 * @param conceptSchemeUri the concept scheme under which the concepts should be added.
	 * @param concepts the set of Concept objects to add.
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 */
	public String createConcepts(String conceptSchemeUri, Set<Concept> concepts) throws OEClientException {
		logger.info("createConcepts entry: scheme uri: {}, concepts: {}", conceptSchemeUri,
				concepts != null ? concepts.toString() : "null");
		if (concepts == null) {
			throw new OEClientException("concepts cannot be null");
		}
		String firstUri = null;
		int count = 0;
		for (Concept concept : concepts) {
			try {
				String uri = createConcept(conceptSchemeUri, concept);
				if (count == 0) {
					firstUri = uri;
				}
				count++;
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concept, e);
			}
		}
		return firstUri;
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
	 * @return the URI of the newly created concept from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createConcept(String conceptSchemeUri, Concept concept) throws OEClientException {
		logger.info("createConcept entry: {} {}", conceptSchemeUri, concept.getUri());
		return createConcept(conceptSchemeUri, concept, null);
	}

	/**
	 * createConcept - create a concept as a topConcept of a Concept Scheme.
	 * If client is in KRT mode, will also add the new concept to the Newly Created KRT concept scheme,
	 * making it eligible for review.
	 *
	 * All properties set on the concept will be included in the creation request:
	 * preferred labels, alternative labels (via {@link Concept#addAltLabel}),
	 * custom classes (via {@link Concept#addClass}), and relationships
	 * (via {@link Concept#addRelationship}).
	 *
	 * @param conceptSchemeUri
	 *            - the URI of the concept scheme for which the new concept will
	 *            become a new concept
	 * @param concept
	 *            - the concept to create. The preferred labels and class of
	 *            this concept will be added
	 * @param metadata
	 *            - optional map of metadata key,value pairs to be added to the concept when it is created.
	 * @return the URI of the newly created concept from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createConcept(String conceptSchemeUri, Concept concept, Map<String, Collection<MetadataValue>> metadata) throws OEClientException {
		logger.info("createConcept entry: {} {}", conceptSchemeUri, concept.getUri());

		JsonObject conceptDetails = buildConceptJsonLd(concept, conceptSchemeUri, true, metadata);

		String conceptSchemePayload = conceptDetails.toString();

		Date startDate = new Date();
		logger.info("createConcept making call  : {}", startDate.getTime());

		return makeRequest(getModelURL(), conceptSchemePayload, RequestType.POST);
	}

	/**
	 * Create multiple concepts in a single request using a @graph payload.
	 * Each concept is paired with a parent URI and a flag indicating whether
	 * the concept should be a top concept of a concept scheme (true) or a
	 * narrower concept of a parent concept (false).
	 *
	 * @param concepts the list of concepts to create
	 * @param parentUris the list of parent URIs - either a concept scheme URI (when asTopConcept is true)
	 *                   or a parent concept URI (when asTopConcept is false)
	 * @param asTopConcept the list of flags indicating whether each concept is a top concept of a scheme
	 *                     (true) or a narrower concept below a parent concept (false)
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	public String createConcepts(List<Concept> concepts, List<String> parentUris, List<Boolean> asTopConcept) throws OEClientException {
		return createConcepts(concepts, parentUris, asTopConcept, null);
	}

	/**
	 * Create multiple concepts in a single request using a @graph payload,
	 * optionally with metadata. Each concept is paired with a parent URI and
	 * a flag indicating whether the concept should be a top concept of a
	 * concept scheme (true) or a narrower concept of a parent concept (false).
	 * If metadata is provided, its size must match the concepts list size.
	 *
	 * @param concepts the list of concepts to create
	 * @param parentUris the list of parent URIs - either a concept scheme URI (when asTopConcept is true)
	 *                   or a parent concept URI (when asTopConcept is false)
	 * @param asTopConcept the list of flags indicating whether each concept is a top concept of a scheme
	 *                     (true) or a narrower concept below a parent concept (false)
	 * @param metadataList optional list of metadata maps, one per concept (may be null)
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	@SuppressWarnings("unchecked")
	public String createConcepts(List<Concept> concepts, List<String> parentUris, List<Boolean> asTopConcept,
						   List<Map<String, Collection<MetadataValue>>> metadataList) throws OEClientException {
		if (concepts == null)
			throw new IllegalArgumentException("createConcepts cannot take null concepts list");
		if (parentUris == null)
			throw new IllegalArgumentException("createConcepts cannot take null parentUris list");
		if (asTopConcept == null)
			throw new IllegalArgumentException("createConcepts cannot take null asTopConcept list");
		if (concepts.size() != parentUris.size())
			throw new IllegalArgumentException(String.format("concepts size (%d) must match parentUris size (%d)",
					concepts.size(), parentUris.size()));
		if (concepts.size() != asTopConcept.size())
			throw new IllegalArgumentException(String.format("concepts size (%d) must match asTopConcept size (%d)",
					concepts.size(), asTopConcept.size()));
		if (metadataList != null && metadataList.size() != concepts.size())
			throw new IllegalArgumentException(String.format("concepts size (%d) must match metadataList size (%d)",
					concepts.size(), metadataList.size()));
		if (concepts.isEmpty())
			return null;

		logger.info("createConcepts entry: concepts count={}", concepts.size());

		JsonObject graphObject = new JsonObject();
		JsonArray dataArray = new JsonArray();

		for (int i = 0; i < concepts.size(); i++) {
			Concept concept = concepts.get(i);
			if (concept == null)
				throw new IllegalArgumentException("createConcepts: null concept at index " + i);
			String parentUri = parentUris.get(i);
			boolean isTopConcept = asTopConcept.get(i);
			Map<String, Collection<MetadataValue>> metadata = metadataList != null ? metadataList.get(i) : null;

			JsonObject conceptDetails = buildConceptJsonLd(concept, parentUri, isTopConcept, metadata);
			dataArray.add(conceptDetails);
		}

		graphObject.put("@graph", dataArray);

		String createConceptsPayload = graphObject.toString();
		logger.info("createConcepts payload: {}", createConceptsPayload);
		return makeRequest(getModelURL(), createConceptsPayload, RequestType.POST);
	}

	/**
	 * Helper method to add multiple concepts to model in one method call including metadata.
	 * The concept and metadata lists must be the same size. The correlation between a concept
	 * and a metadata map is by list ordinal.
	 * @param parentConceptUri the URI of the parent concept under which the concepts should be added.
	 * @param concepts the List of Concept objects to add.
	 * @param mds the List of optional metadata values to add to each concept. If there is no metadata for a concept,
	 *            add an empty map.
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 */
	public String createConceptsBelowConcept(String parentConceptUri, List<Concept> concepts, List<Map<String, Collection<MetadataValue>>> mds) throws OEClientException {
		logger.info("createConcepts entry: parent concept uri: {}, concepts: {}, mds: {}", parentConceptUri,
				concepts != null ? concepts.toString() : "null",
				mds != null ? mds.toString() : "null");

		if (concepts == null) {
			throw new OEClientException("concepts set cannot be null");
		}

		if (mds != null && (concepts.size() != mds.size())) {
			throw new OEClientException("The concept list and the metadata list are not the same size.");
		}

		String firstUri = null;
		for (int n = 0; n < concepts.size(); n++) {
			try {
				String uri = createConceptBelowConcept(parentConceptUri, concepts.get(n), mds != null ? mds.get(n) : null);
				if (n == 0) {
					firstUri = uri;
				}
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concepts.get(n), e);
			}
		}
		return firstUri;
	}

	/**
	 * Create multiple concepts below the specified concept.
	 * @param parentConceptUri the pareant concept uri
	 * @param concepts the set of concepts to create below the parent
	 * @return the URI of the first newly created concept from the x-location-uri header, or null
	 * @throws OEClientException excetion
	 */
	public String createConceptsBelowConcept(String parentConceptUri, Set<Concept> concepts) throws OEClientException {
		logger.info("createConceptsBelowConcept entry: parent concept uri: {}, concepts: {}", parentConceptUri,
				concepts != null ? concepts.toString() : "null");
		if (concepts == null) {
			throw new OEClientException("concepts set cannot be null");
		}
		String firstUri = null;
		int count = 0;
		for (Concept concept : concepts) {
			try {
				String uri = createConceptBelowConcept(parentConceptUri, concept);
				if (count == 0) {
					firstUri = uri;
				}
				count++;
			} catch (OEClientException e) {
				logger.warn("Failed to create concept: {}", concept, e);
			}
		}
		return firstUri;
	}

	/**
	 * createConceptBelowConcept - create a concept as a narrower concept under another concept
	 * @param parentConceptUri the parent concept uri
	 * @param concept the concept to create
	 * @return the URI of the newly created concept from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createConceptBelowConcept(String parentConceptUri, Concept concept) throws OEClientException {
		return createConceptBelowConcept(parentConceptUri, concept, null);
	}

	/**
	 * createConceptBelowConcept - create a concept as a narrower concept under another concept with metadata
	 * @param parentConceptUri the parent concept uri
	 * @param concept the concept to create
	 * @param metadata optional metadata to add to the concept
	 * @return the URI of the newly created concept from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createConceptBelowConcept(String parentConceptUri, Concept concept, Map<String, Collection<MetadataValue>> metadata) throws OEClientException {
		logger.info("createConceptBelowConcept entry: {} {} {}", parentConceptUri, concept.getUri(), metadata == null ? "" : metadata.keySet());

		JsonObject conceptDetails = buildConceptJsonLd(concept, parentConceptUri, false, metadata);

		String conceptPayload = conceptDetails.toString();

		logger.info("createConceptBelowConcept making call with payload: {}", conceptPayload);
		return makeRequest(getModelURL(), conceptPayload, RequestType.POST);

	}

	/**
	 * Build a JSON-LD object for a concept with all its properties: preferred labels,
	 * alternative labels, custom classes, metadata, relationships, and identifiers.
	 *
	 * @param concept the concept to serialize
	 * @param parentUri the parent URI - either a concept scheme URI (if isTopConcept) or a parent concept URI
	 * @param isTopConcept true if the concept is a top concept of a scheme, false if narrower of a parent concept
	 * @param metadata optional metadata map
	 * @return the JSON-LD object representing the concept
	 */
	private JsonObject buildConceptJsonLd(Concept concept, String parentUri, boolean isTopConcept,
										  Map<String, Collection<MetadataValue>> metadata) throws OEClientException {

		// @type: always include skos:Concept, then add any custom classes on top
		JsonArray conceptTypeList = new JsonArray();
		conceptTypeList.add("skos:Concept");
		if (concept.getClassUris() != null && !concept.getClassUris().isEmpty()) {
			for (String classUri : concept.getClassUris()) {
				conceptTypeList.add(classUri);
			}
		}

		// Preferred labels
		JsonArray newConceptLabelDataList = new JsonArray();
		for (Label label : concept.getPrefLabels()) {
			JsonObject newConceptLabelData = new JsonObject();
			JsonArray labelTypeList = new JsonArray();
			labelTypeList.add("skosxl:Label");
			newConceptLabelData.put("@type", labelTypeList);

			JsonArray labelLiteralFormDataList = new JsonArray();
			JsonObject labelLiteralFormData = new JsonObject();
			labelLiteralFormData.put("@value", label.getValue());
			if (label.getLanguageCode() != null) {
				labelLiteralFormData.put("@language", label.getLanguageCode());
			}
			labelLiteralFormDataList.add(labelLiteralFormData);
			newConceptLabelData.put("skosxl:literalForm", labelLiteralFormDataList);

			newConceptLabelDataList.add(newConceptLabelData);
		}

		// Build the concept object
		JsonObject conceptDetails = new JsonObject();
		conceptDetails.put("@type", conceptTypeList);
		conceptDetails.put("skosxl:prefLabel", newConceptLabelDataList);
		conceptDetails.put("@id", concept.getUri());

		// Parent relationship (top concept of scheme or narrower of parent concept)
		if (isTopConcept) {
			JsonObject relatedConceptSchemeData = new JsonObject();
			relatedConceptSchemeData.put("@id", parentUri);
			conceptDetails.put("skos:topConceptOf", relatedConceptSchemeData);
		} else {
			JsonObject broaderConceptData = new JsonObject();
			broaderConceptData.put("@id", parentUri);
			JsonArray broaderArray = new JsonArray();
			broaderArray.add(broaderConceptData);
			conceptDetails.put("skos:broader", broaderArray);
		}

		// Identifiers (e.g. sem:guid)
		for (Identifier identifier : concept.getIdentifiers()) {
			conceptDetails.put(identifier.getUri(), identifier.getValue());
		}

		// Alternative labels (skosxl:altLabel or custom label types)
		Map<String, Collection<Label>> altLabelsByUri = concept.getAltLabelsByUri();
		if (altLabelsByUri != null && !altLabelsByUri.isEmpty()) {
			for (Map.Entry<String, Collection<Label>> entry : altLabelsByUri.entrySet()) {
				String labelTypeUri = entry.getKey();
				Collection<Label> altLabels = entry.getValue();
				if (altLabels != null && !altLabels.isEmpty()) {
					JsonArray altLabelArray = new JsonArray();
					for (Label altLabel : altLabels) {
						JsonObject altLabelData = new JsonObject();
						JsonArray altLabelTypeList = new JsonArray();
						altLabelTypeList.add("skosxl:Label");
						altLabelData.put("@type", altLabelTypeList);

						JsonArray altLiteralFormList = new JsonArray();
						JsonObject altLiteralForm = new JsonObject();
						altLiteralForm.put("@value", altLabel.getValue());
						if (altLabel.getLanguageCode() != null) {
							altLiteralForm.put("@language", altLabel.getLanguageCode());
						}
						altLiteralFormList.add(altLiteralForm);
						altLabelData.put("skosxl:literalForm", altLiteralFormList);

						altLabelArray.add(altLabelData);
					}
					conceptDetails.put(labelTypeUri, altLabelArray);
				}
			}
		}

		// Metadata
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

		// Associative relationships to existing concepts
		Map<String, Collection<String>> relationships = concept.getRelationships();
		if (relationships != null && !relationships.isEmpty()) {
			for (Map.Entry<String, Collection<String>> entry : relationships.entrySet()) {
				String relationshipTypeUri = entry.getKey();
				Collection<String> targetUris = entry.getValue();
				if (targetUris != null && !targetUris.isEmpty()) {
					// Skip skos:broader as it's already handled by the parent relationship
					if ("skos:broader".equals(relationshipTypeUri)) continue;
					JsonArray targetArray = new JsonArray();
					for (String targetUri : targetUris) {
						JsonObject targetObject = new JsonObject();
						targetObject.put("@id", targetUri);
						targetArray.add(targetObject);
					}
					conceptDetails.put(relationshipTypeUri, targetArray);
				}
			}
		}

		// KRT mode: add new concept to NewlyCreated KRT concept scheme
		if (isKRTClient()) {
			String newlyAddedConceptSchemeUri = getKRTNewlyAddedSchemeUri();
			if (newlyAddedConceptSchemeUri != null) {
				JsonObject newlyCreatedConceptSchemeData = new JsonObject();
				newlyCreatedConceptSchemeData.put("@id", newlyAddedConceptSchemeUri);
				conceptDetails.put("skos:topConceptOf", newlyCreatedConceptSchemeData);
			}
		}

		return conceptDetails;
	}

	/**
	 * createConceptScheme - create a concept as a topConcept of a Concept
	 * Scheme
	 *
	 * @param conceptScheme
	 *            - the concept scheme to create, the labels of this concept
	 *            will be created
	 * @return the URI of the newly created concept scheme from the x-location-uri header, or null
	 * @throws OEClientException
	 */
	public String createConceptScheme(ConceptScheme conceptScheme) throws OEClientException {
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
		return makeRequest(getModelURL(), conceptSchemePayload, RequestType.POST);
	}

	/**
	 * createConceptSchemeAndReturn - create a concept scheme and return a new concept scheme instance
	 * with the server-assigned URI when available. This method does not mutate the input concept scheme.
	 *
	 * @param conceptScheme the concept scheme to create
	 * @return a new ConceptScheme instance with labels copied from the input and URI from server response when present
	 * @throws OEClientException if the request fails
	 */
	public ConceptScheme createConceptSchemeAndReturn(ConceptScheme conceptScheme) throws OEClientException {
		String createdUri = createConceptScheme(conceptScheme);
		String conceptSchemeUri = createdUri != null ? createdUri : conceptScheme.getUri();
		return new ConceptScheme(this, conceptSchemeUri, new ArrayList<>(conceptScheme.getPrefLabels()));
	}

	/**
	 * Create multiple concept schemes in a single request using a @graph payload.
	 *
	 * @param conceptSchemes the list of concept schemes to create
	 * @return the URI of the first newly created concept scheme from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	public String createConceptSchemes(List<ConceptScheme> conceptSchemes) throws OEClientException {
		if (conceptSchemes == null)
			throw new IllegalArgumentException("createConceptSchemes cannot take null conceptSchemes list");
		if (conceptSchemes.isEmpty())
			return null;

		logger.info("createConceptSchemes entry: count={}", conceptSchemes.size());

		JsonObject graphObject = new JsonObject();
		JsonArray dataArray = new JsonArray();

		for (ConceptScheme conceptScheme : conceptSchemes) {
			JsonObject conceptSchemeDetails = new JsonObject();

			JsonArray conceptSchemeTypeList = new JsonArray();
			conceptSchemeTypeList.add("skos:ConceptScheme");
			conceptSchemeDetails.put("@type", conceptSchemeTypeList);

			JsonArray labelDataList = new JsonArray();
			for (Label label : conceptScheme.getPrefLabels()) {
				JsonObject labelData = new JsonObject();
				labelData.put("@value", label.getValue());
				if (label.getLanguageCode() != null) {
					labelData.put("@language", label.getLanguageCode());
				}
				labelDataList.add(labelData);
			}

			conceptSchemeDetails.put("rdfs:label", labelDataList);
			conceptSchemeDetails.put("@id", conceptScheme.getUri());

			dataArray.add(conceptSchemeDetails);
		}

		graphObject.put("@graph", dataArray);

		String createConceptSchemesPayload = graphObject.toString();
		logger.info("createConceptSchemes payload: {}", createConceptSchemesPayload);
		return makeRequest(getModelURL(), createConceptSchemesPayload, RequestType.POST);
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
		if(label.getUri() == null) {
			throw new IllegalArgumentException("Label URI is required");
		}
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

	/**
	 * Create/add preferred labels to multiple existing concepts. Each concept URI is paired with a label
	 * by array index. The relationship type is defaulted to "skosxl:prefLabel".
	 *
	 * @param conceptUris array of concept URIs
	 * @param labels array of labels (must be same length as conceptUris)
	 * @return the URI of the first newly created label from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	@SuppressWarnings("unchecked")
	public String createLabels(String[] conceptUris, Label[] labels) throws OEClientException {
		String[] relationshipTypeUris = new String[conceptUris == null ? 0 : conceptUris.length];
		Arrays.fill(relationshipTypeUris, "skosxl:prefLabel");
		return createLabels(conceptUris, relationshipTypeUris, labels);
	}

	/**
	 * Create/add labels to multiple existing concepts with specified relationship types.
	 * Each concept URI is paired with a relationship type URI and a label by array index.
	 *
	 * @param conceptUris array of concept URIs
	 * @param relationshipTypeUris array of relationship type URIs (e.g. "skosxl:prefLabel", "skosxl:altLabel")
	 * @param labels array of labels (must be same length as conceptUris and relationshipTypeUris)
	 * @return the URI of the first newly created label from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	@SuppressWarnings("unchecked")
	public String createLabels(String[] conceptUris, String[] relationshipTypeUris, Label[] labels) throws OEClientException {
		if (conceptUris == null)
			throw new IllegalArgumentException("createLabels cannot take null concept URIs array");
		if (relationshipTypeUris == null)
			throw new IllegalArgumentException("createLabels cannot take null relationship type URIs array");
		if (labels == null)
			throw new IllegalArgumentException("createLabels cannot take null labels array");
		logger.info("createLabels: {} conceptUris, {} relationshipTypeUris, {} labels provided",
				conceptUris.length, relationshipTypeUris.length, labels.length);

		if ((conceptUris.length != labels.length))
			throw new IllegalArgumentException(String.format("conceptUris size (%d) must match labels size (%d)",
					conceptUris.length, labels.length));
		if ((conceptUris.length != relationshipTypeUris.length))
			throw new IllegalArgumentException(String.format("conceptUris size (%d) must match relationshipTypeUris size (%d)",
					conceptUris.length, relationshipTypeUris.length));
		if ((conceptUris.length == 0))
			return null;


		JsonObject graphObject = new JsonObject();

		JsonArray dataArray = new JsonArray();
		for (int i = 0; i < conceptUris.length; i++) {
			String conceptUri = conceptUris[i];
			String relationshipTypeUri = relationshipTypeUris[i];
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

			instanceObject.put(relationshipTypeUri, labelObject);

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
		return makeRequest(getModelURL(), createLabelsPayload, RequestType.POST);

	}

	/**
	 * Create/add a label to an existing concept. This call dispatches to method of same
	 * name with concept URI as first argument. It is a wrapper.
	 *
	 * @param concept the concept
	 * @param relationshipTypeUri the relationship type uri
	 * @param label the label object
	 * @return the URI of the newly created label from the x-location-uri header, or null
	 * @throws OEClientException the exception
	 */
	public String createLabel(Concept concept, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", concept, relationshipTypeUri, label);
		return createLabel(concept.getUri(), relationshipTypeUri, label);
	}

	/**
	 * Create/add a label to an existing concept with the specified URI.
	 * @param conceptUri the concept URI
	 * @param relationshipTypeUri the relationship type URI
	 * @param label the label object
	 * @return the URI of the newly created label from the x-location-uri header, or null
	 * @throws OEClientException exception
	 */
	public String createLabel(String conceptUri, String relationshipTypeUri, Label label) throws OEClientException {
		logger.info("createLabel entry: {} {} {}", conceptUri, relationshipTypeUri, label);

		JsonObject instanceObject = new JsonObject();
		instanceObject.put("@id", conceptUri);
		JsonObject labelObject = new JsonObject();
		labelObject.put("@type", "skosxl:Label");
		JsonObject literalFormObject = new JsonObject();
		literalFormObject.put("@value", label.getValue());
		if (label.getLanguageCode() != null) {
			literalFormObject.put("@language", label.getLanguageCode());
		}
		JsonArray literalFormArray = new JsonArray();
		literalFormArray.add(literalFormObject);
		labelObject.put("skosxl:literalForm", literalFormArray);
		instanceObject.put(relationshipTypeUri, labelObject);

		if (isKRTClient()) {
			String modifiedSchemeUri = getKRTModifiedSchemeUri();
			if (modifiedSchemeUri != null) {
				instanceObject.put("skos:topConceptOf", modifiedSchemeUri);
			}
		}

		logger.info("createRelationship payload: {}", instanceObject);
		return makeRequest(getModelURL(), instanceObject.toString(), RequestType.POST);

	}

	public void createRelationshipType(Label forwardLabel, String forwardUri, Label inverseLabel, String inverseUri) {
		logger.info("createRelationshipType entry: {} {} {} {}", forwardLabel, forwardUri, inverseLabel, inverseUri);

		JsonObject forwardRelationshipType = getRelationshipTypeJsonObject(forwardLabel, forwardUri);
		JsonObject inverseRelationshipType = getRelationshipTypeJsonObject(inverseLabel, inverseUri);

		JsonArray inverseOfArray = new JsonArray();
		inverseOfArray.add(inverseRelationshipType);
		forwardRelationshipType.put("owl:inverseOf", inverseOfArray);

		String relationshipTypePayload = forwardRelationshipType.toString();

		logger.info("createRelationshipType making call  : {}", relationshipTypePayload);
		try {
			makeRequest(getModelURL(), relationshipTypePayload, RequestType.POST);
		} catch (OEClientException e) {
			logger.error("createRelationshipType failed: {}", e.getMessage());
		}

	}

	/**
	 * Create a new symmetric associative relationship type in the model. Unlike
	 * {@link #createRelationshipType(Label, String, Label, String)}, a symmetric relationship type
	 * has a single URI (it is its own inverse) and is typed as {@code owl:SymmetricProperty} in
	 * addition to {@code owl:ObjectProperty}, so relating concept A to concept B implies the
	 * reverse relationship B to A without needing a separate inverse relationship type.
	 *
	 * @param label
	 *            the label of the symmetric relationship type
	 * @param uri
	 *            the URI to assign to the new relationship type
	 */
	public void createSymmetricRelationshipType(Label label, String uri) {
		logger.info("createSymmetricRelationshipType entry: {} {}", label, uri);

		JsonObject relationshipType = getRelationshipTypeJsonObject(label, uri, true);

		String relationshipTypePayload = relationshipType.toString();

		logger.info("createSymmetricRelationshipType making call  : {}", relationshipTypePayload);
		try {
			makeRequest(getModelURL(), relationshipTypePayload, RequestType.POST);
		} catch (OEClientException e) {
			logger.error("createSymmetricRelationshipType failed: {}", e.getMessage());
		}

	}

	public void createLabelRelationshipType(Label forwardLabel, String forwardUri) {
		logger.info("createLabelRelationshipType entry: {} {}", forwardLabel, forwardUri);

		JsonObject relationshipTypeObject = new JsonObject();


		JsonArray typeArray = new JsonArray();
		typeArray.add("owl:ObjectProperty");
		relationshipTypeObject.put("@type", typeArray);
		relationshipTypeObject.put("@id", forwardUri);

		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", forwardLabel.getValue());
		labelObject.put("@language", forwardLabel.getLanguageCode());
		labelArray.add(labelObject);
		relationshipTypeObject.put("rdfs:label", labelArray);

		JsonArray domainArray = new JsonArray();
		JsonObject domainObject = new JsonObject();
		domainObject.put("@id", "skos:Concept");
		domainArray.add(domainObject);
		relationshipTypeObject.put("rdfs:domain", domainArray);

		JsonArray rangeArray = new JsonArray();
		JsonObject rangeObject = new JsonObject();
		rangeObject.put("@id", "skosxl:Label");
		rangeArray.add(rangeObject);
		relationshipTypeObject.put("rdfs:range", rangeArray);

		JsonArray subPropertyOfArray = new JsonArray();
		JsonObject subPropertyOfObject = new JsonObject();
		subPropertyOfObject.put("@id", "skosxl:altLabel");
		subPropertyOfArray.add(subPropertyOfObject);
		relationshipTypeObject.put("rdfs:subPropertyOf", subPropertyOfArray);


		String relationshipTypePayload = relationshipTypeObject.toString();

		logger.info("createLabelRelationshipType making call  : {}", relationshipTypePayload);
		try {
			makeRequest(getModelURL(), relationshipTypePayload, RequestType.POST);
		} catch (OEClientException e) {
			logger.error("createLabelRelationshipType failed: {}", e.getMessage());
		}

	}

	private JsonObject getRelationshipTypeJsonObject(Label forwardLabel, String forwardUri) {
		return getRelationshipTypeJsonObject(forwardLabel, forwardUri, false);
	}

	private JsonObject getRelationshipTypeJsonObject(Label forwardLabel, String forwardUri, boolean symmetric) {

		JsonObject relationshipTypeObject = new JsonObject();


		JsonArray typeArray = new JsonArray();
		typeArray.add("owl:ObjectProperty");
		if (symmetric) {
			typeArray.add("owl:SymmetricProperty");
		}
		relationshipTypeObject.put("@type", typeArray);
		relationshipTypeObject.put("@id", forwardUri);

		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", forwardLabel.getValue());
		labelObject.put("@language", forwardLabel.getLanguageCode());
		labelArray.add(labelObject);
		relationshipTypeObject.put("rdfs:label", labelArray);

		JsonArray domainArray = new JsonArray();
		JsonObject domainObject = new JsonObject();
		domainObject.put("@id", "skos:Concept");
		domainArray.add(domainObject);
		relationshipTypeObject.put("rdfs:domain", domainArray);

		JsonArray rangeArray = new JsonArray();
		JsonObject rangeObject = new JsonObject();
		rangeObject.put("@id", "skos:Concept");
		rangeArray.add(rangeObject);
		relationshipTypeObject.put("rdfs:range", rangeArray);

		JsonArray subPropertyOfArray = new JsonArray();
		JsonObject subPropertyOfObject = new JsonObject();
		subPropertyOfObject.put("@id", "skos:related");
		subPropertyOfArray.add(subPropertyOfObject);
		relationshipTypeObject.put("rdfs:subPropertyOf", subPropertyOfArray);

		return relationshipTypeObject;
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

	/**
	 * Create a string metadata value on a concept with an optional language tag.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param metadataValue the string value to add
	 * @param metadataLanguage the language tag for the value, or {@code null}
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, String metadataValue, String metadataLanguage) throws OEClientException {
		JsonObject valueObject = new JsonObject();
		if (metadataLanguage != null && !metadataLanguage.isBlank()) {
			valueObject.put("@language", metadataLanguage);
		}
		valueObject.put("@value", metadataValue);

		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	/**
	 * Create a URI-typed metadata value on a concept.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param uri the URI value to add
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, URI uri)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, uri.toString());
		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", uri.toString());
		valueObject.put("@type", "xsd:anyURI");
		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	private final static SimpleDateFormat xsdDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * Create a date metadata value on a concept.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param date the date value to add
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, Date date)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, date.toString());
		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", xsdDateFormat.format(date));
		valueObject.put("@type", "xsd:date");
		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	/**
	 * Create a decimal metadata value on a concept.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param value the decimal value to add
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, double value)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, value);
		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", BigDecimal.valueOf(value).stripTrailingZeros().toPlainString());
		valueObject.put("@type", "xsd:decimal");
		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	/**
	 * Create an integer metadata value on a concept.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param value the integer value to add
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, int value)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, value);
		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", value);
		valueObject.put("@type", "xsd:integer");
		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	/**
	 * Create a boolean metadata value on a concept.
	 *
	 * @param concept the concept that will receive the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param value the boolean value to add
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadata(Concept concept, String metadataTypeUri, boolean value)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, value);
		JsonObject valueObject = new JsonObject();
		valueObject.put("@value", value);
		valueObject.put("@type", "xsd:boolean");
		return createMetadata(concept, metadataTypeUri, valueObject);
	}

	@SuppressWarnings("unchecked")
	private String createMetadata(Concept concept, String metadataTypeUri, JsonObject valueObject)
			throws OEClientException {
		logger.info("createMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, valueObject);

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

		valueArray.add(valueObject);
		addOperation.put("value", valueArray);
		operationList.add(addOperation);

		checkKRTModified(operationList, "0");

		String createMetadataPayload = operationList.toString();
		logger.info("createMetadata payload: {}", createMetadataPayload);

		return makeRequest(getModelURL(), createMetadataPayload, RequestType.PATCH);
	}

	/**
	 * Create a string metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeString(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:string");
	}

	/**
	 * Create an integer metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeInteger(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:integer");
	}

	/**
	 * Create a date metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeDate(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:date");
	}

	/**
	 * Create a decimal metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeDecimal(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:decimal");
	}

	/**
	 * Create an anyURI metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeAnyURI(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:anyURI");
	}

	/**
	 * Create a boolean metadata type definition.
	 *
	 * @param label the display label for the metadata type
	 * @param metadataTypeUri the URI of the metadata type to create
	 * @return the URI returned by the server, or {@code null}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public String createMetadataTypeBoolean(Label label, String metadataTypeUri) throws OEClientException {
		logger.info("createMetadataTypeString entry: {} {}", label.getValue(), metadataTypeUri);
		return createMetadataType(label, metadataTypeUri, "xsd:boolean");
	}

	private String createMetadataType(Label label, String metadataTypeUri, String metadataDataRange) throws OEClientException {

		JsonArray operationList = new JsonArray();

		JsonObject metadataPayload = new JsonObject();
		metadataPayload.put("@id", metadataTypeUri);

		JsonArray typeArray = new JsonArray();
		typeArray.add("owl:DatatypeProperty");
		metadataPayload.put("@type", typeArray);

		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", label.getValue());
		if (label.getLanguageCode() != null) {
			labelObject.put("@language", label.getLanguageCode());
		}
		labelArray.add(labelObject);
		metadataPayload.put("rdfs:label", labelArray);

		JsonArray domainArray = new JsonArray();
		JsonObject domainObject = new JsonObject();
		domainObject.put("@id", "skos:Concept");
		domainArray.add(domainObject);
		metadataPayload.put("rdfs:domain", domainArray);

		JsonArray rangeArray = new JsonArray();
		JsonObject rangeObject = new JsonObject();
		rangeObject.put("@id", metadataDataRange);
		rangeArray.add(rangeObject);
		metadataPayload.put("rdfs:range", rangeArray);

		String createMetadataPayload = metadataPayload.toString();

		logger.info("createMetadata payload: {}", createMetadataPayload);

		return makeRequest(getModelURL(), createMetadataPayload, RequestType.POST );
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

	/**
	 * Update an integer metadata value on a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current integer value
	 * @param newValue the new integer value
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateMetadata(Concept concept, String metadataTypeUri, int oldValue, int newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldValue);
		oldValueObject.put("@type", "xsd:integer");

		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newValue);
		newValueObject.put("@type", "xsd:integer");

		updateTypedMetadata(concept, metadataTypeUri, oldValueObject, newValueObject);
	}

	/**
	 * Update a decimal metadata value on a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current decimal value
	 * @param newValue the new decimal value
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateMetadata(Concept concept, String metadataTypeUri, double oldValue, double newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", BigDecimal.valueOf(oldValue).stripTrailingZeros().toPlainString());
		oldValueObject.put("@type", "xsd:decimal");

		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", BigDecimal.valueOf(newValue).stripTrailingZeros().toPlainString());
		newValueObject.put("@type", "xsd:decimal");

		updateTypedMetadata(concept, metadataTypeUri, oldValueObject, newValueObject);
	}

	/**
	 * Update a date metadata value on a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current date value
	 * @param newValue the new date value
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateMetadata(Concept concept, String metadataTypeUri, Date oldValue, Date newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", xsdDateFormat.format(oldValue));
		oldValueObject.put("@type", "xsd:date");

		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", xsdDateFormat.format(newValue));
		newValueObject.put("@type", "xsd:date");

		updateTypedMetadata(concept, metadataTypeUri, oldValueObject, newValueObject);
	}

	/**
	 * Update a URI metadata value on a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current URI value
	 * @param newValue the new URI value
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateMetadata(Concept concept, String metadataTypeUri, URI oldValue, URI newValue) throws OEClientException {
		logger.info("updateMetadata entry: {} {} {} {}", concept.getUri(), metadataTypeUri, oldValue, newValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldValue.toString());
		oldValueObject.put("@type", "xsd:anyURI");

		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newValue.toString());
		newValueObject.put("@type", "xsd:anyURI");

		updateTypedMetadata(concept, metadataTypeUri, oldValueObject, newValueObject);
	}

	private void updateTypedMetadata(Concept concept, String metadataTypeUri, JsonObject oldValueObject, JsonObject newValueObject) throws OEClientException {
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
		testOperation2.put("value", oldValueObject);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", String.format("@graph/1/%s/2", getTildered(metadataTypeUri)));
		addOperation.put("value", newValueObject);
		operationList.add(addOperation);

		checkKRTModified(operationList, "1");

		String updateMetadataPayload = operationList.toString();
		logger.info("updateTypedMetadata payload: {}", updateMetadataPayload);
		makeRequest(getModelURL(), updateMetadataPayload, RequestType.PATCH);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Delete a boolean metadata value from a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current boolean value to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
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

	/**
	 * Delete an integer metadata value from a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current integer value to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteMetadata(Concept concept, String metadataTypeUri, int oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldValue);
		oldValueObject.put("@type", "xsd:integer");

		deleteTypedMetadata(concept, metadataTypeUri, oldValueObject);
	}

	/**
	 * Delete a decimal metadata value from a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current decimal value to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteMetadata(Concept concept, String metadataTypeUri, double oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", BigDecimal.valueOf(oldValue).stripTrailingZeros().toPlainString());
		oldValueObject.put("@type", "xsd:decimal");

		deleteTypedMetadata(concept, metadataTypeUri, oldValueObject);
	}

	/**
	 * Delete a date metadata value from a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current date value to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteMetadata(Concept concept, String metadataTypeUri, Date oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", xsdDateFormat.format(oldValue));
		oldValueObject.put("@type", "xsd:date");

		deleteTypedMetadata(concept, metadataTypeUri, oldValueObject);
	}

	/**
	 * Delete a URI metadata value from a concept.
	 *
	 * @param concept the concept holding the metadata
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldValue the current URI value to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteMetadata(Concept concept, String metadataTypeUri, URI oldValue) throws OEClientException {
		logger.info("deleteMetadata entry: {} {} {}", concept.getUri(), metadataTypeUri, oldValue);

		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldValue.toString());
		oldValueObject.put("@type", "xsd:anyURI");

		deleteTypedMetadata(concept, metadataTypeUri, oldValueObject);
	}

	private void deleteTypedMetadata(Concept concept, String metadataTypeUri, JsonObject oldValueObject) throws OEClientException {
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
		testOperation2.put("value", oldValueObject);
		operationList.add(testOperation2);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", String.format("@graph/1/%s/0", getTildered(metadataTypeUri)));
		operationList.add(removeOperation);

		String deleteMetadataPayload = operationList.toString();
		logger.info("deleteTypedMetadata payload: {}", deleteMetadataPayload);
		makeRequest(getModelURL(), deleteMetadataPayload, RequestType.PATCH);
	}

	public void deleteConcept(Concept concept) throws OEClientException {
		deleteConcept(concept, "empty");
	}

	/**
	 * Delete a concept using the specified delete mode.
	 *
	 * @param concept the concept to delete
	 * @param deleteMode the delete mode, such as {@code empty} or {@code withSubtree}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteConcept(Concept concept, String deleteMode) throws OEClientException {
		logger.info("deleteConcept entry: {} {}", concept.getUri(), deleteMode);

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("mode", deleteMode);
		
		StringBuilder pathBuilder = new StringBuilder(getModelURL());
		pathBuilder.append("/");
		pathBuilder.append(getEscapedUri("<" + concept.getUri() + ">"));
		String fullUrl = pathBuilder.toString();
		logger.info("deleteConcept - fullUrl: {}", fullUrl);

		makeRequest(fullUrl, queryParameters, null, RequestType.DELETE );
	}

	/**
	 * Delete a concept and all of its descendants.
	 *
	 * @param concept the root concept of the subtree to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteConceptWithSubtree(Concept concept) throws OEClientException {
		deleteConcept(concept, "withSubtree");
	}

	/**
	 * Delete a concept scheme from the current model with 'empty' mode.
	 *
	 * @param conceptScheme the concept scheme to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteConceptScheme(ConceptScheme conceptScheme) throws OEClientException {
		deleteConceptScheme(conceptScheme, "empty");
	}


	/**
	 * Delete a concept scheme from the current model.
	 *
	 * @param conceptScheme the concept scheme to delete
	 * @param deleteMode the delete mode, such as {@code empty} or {@code subtree}
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteConceptScheme(ConceptScheme conceptScheme, String deleteMode) throws OEClientException {
		logger.info("deleteConceptScheme entry: {} {}", conceptScheme.getUri(), deleteMode);

		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("mode", deleteMode);

        String fullUrl = getModelURL() + "/" +
                getEscapedUri("<" + conceptScheme.getUri() + ">");
		logger.info("deleteConceptScheme - fullUrl: {}", fullUrl);

		makeRequest(fullUrl, queryParameters, null, RequestType.DELETE );
	}

	/**
	 * Delete a concept-scheme and all of its descendants.
	 *
	 * @param conceptScheme the root concept scheme of the subtree to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteConceptSchemeWithSubtree(ConceptScheme conceptScheme) throws OEClientException {
		deleteConceptScheme(conceptScheme, "withSubtree");
	}

	/**
	 * Delete a relationship between two resources.
	 *
	 * @param relationshipTypeUri the URI of the relationship type to remove
	 * @param concept1 the source resource
	 * @param concept2 the target resource
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteRelationship(String relationshipTypeUri, AbstractBeanFromJson concept1, AbstractBeanFromJson concept2)
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
		String url = getModelURL() + "/" + getEscapedUri("<" + concept.getUri() + ">");
		logger.info("deleteLabel payload: {}", deleteLabelPayload);
		makeRequest(url, deleteLabelPayload, RequestType.PATCH );
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

	public void uploadPublisherConfiguration(byte[] zippedPublisherConfiguration) throws OEClientException {
		logger.info("uploadPublisherConfiguration entry");

		String boundary = "----geckoformboundary7cfb67d17ac7416fe63a2f1ae8c7bbe8";


		String url = getApiURL() + "publisher/workspace/" + getModelUri() + "/config";
		HashMap<String, String> queryParameters = new HashMap<>();
		queryParameters.put("mode", "replace");
		String urlToUse = getURLwithParameters(url, queryParameters);
		HttpResponse<String> response = null;

		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
				.uri(URI.create(urlToUse))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(ofMimeMultipartData(zippedPublisherConfiguration, boundary));
		addHeaders(requestBuilder);
		HttpRequest request = requestBuilder.build();

		try {
			response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException |  InterruptedException e) {
			throw new OEClientException(e.getMessage());
		}

		checkResponseStatus(response);
	}

	private HttpRequest.BodyPublisher ofMimeMultipartData(byte[] data, String boundary) throws OEClientException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			baos.write(("--" + boundary+"\r\n").getBytes());
			baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"Upload.zip\"".getBytes());
			baos.write(("\r\nContent-Type: application/x-zip-compressed\r\n\r\n").getBytes());
			baos.write(data);
			baos.write(("\r\n--" + boundary + "--\r\n").getBytes());
		} catch (IOException e) {
			throw new OEClientException(e.getMessage());
		}

		return HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray());
	}

	public void publishModel(String config, String environmentUri) throws OEClientException {
		logger.info("publishModel entry");

		String url = getApiURL() + "publisher/" + getModelUri() + "/publish";

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("config", config);
		queryParameters.put("environment", environmentUri);

		String urlToUse = getURLwithParameters(url, queryParameters);
		HttpResponse<String> response = null;

		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
				.uri(URI.create(urlToUse))
				.POST(HttpRequest.BodyPublishers.noBody());
		addHeaders(requestBuilder);
		HttpRequest request = requestBuilder.build();


		try {
			response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException |  InterruptedException e) {
			throw new OEClientException(e.getMessage());
		}

		checkResponseStatus(response);
	}

	// ======================================================================
	// Concept Scheme update
	// ======================================================================

	/**
	 * Update the label of a concept scheme.
	 *
	 * @param conceptScheme the concept scheme to update
	 * @param oldLabel the existing label to replace
	 * @param newLabel the new label value
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateConceptScheme(ConceptScheme conceptScheme, Label oldLabel, Label newLabel) throws OEClientException {
		logger.info("updateConceptScheme entry: {} {} {}", conceptScheme.getUri(), oldLabel.getValue(), newLabel.getValue());

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0/rdfs:label/0");
		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldLabel.getValue());
		if (oldLabel.getLanguageCode() != null) {
			oldValueObject.put("@language", oldLabel.getLanguageCode());
		}
		testOperation.put("value", oldValueObject);
		operationList.add(testOperation);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", "@graph/0/rdfs:label/0");
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", "@graph/0/rdfs:label/-");
		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newLabel.getValue());
		if (newLabel.getLanguageCode() != null) {
			newValueObject.put("@language", newLabel.getLanguageCode());
		}
		addOperation.put("value", newValueObject);
		operationList.add(addOperation);

		String url = getModelURL() + "/" + getEscapedUri("<" + conceptScheme.getUri() + ">");

		String payload = operationList.toString();
		logger.info("updateConceptScheme payload: {}", payload);
		makeRequest(url, payload, RequestType.PATCH);
	}

	// ======================================================================
	// Hierarchical Relationship Type
	// ======================================================================

	/**
	 * Create a hierarchical relationship type (broader/narrower).
	 *
	 * @param broaderLabel the label for the broader (forward) direction
	 * @param broaderUri the URI for the broader property
	 * @param narrowerLabel the label for the narrower (inverse) direction
	 * @param narrowerUri the URI for the narrower property
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void createHierarchicalRelationshipType(Label broaderLabel, String broaderUri,
			Label narrowerLabel, String narrowerUri) throws OEClientException {
		logger.info("createHierarchicalRelationshipType entry: {} {} {} {}",
				broaderLabel, broaderUri, narrowerLabel, narrowerUri);

		JsonObject broaderType = getHierarchicalRelationshipTypeJsonObject(broaderLabel, broaderUri, "skos:broader");
		JsonObject narrowerType = getHierarchicalRelationshipTypeJsonObject(narrowerLabel, narrowerUri, "skos:narrower");

		JsonArray inverseOfArray = new JsonArray();
		inverseOfArray.add(narrowerType);
		broaderType.put("owl:inverseOf", inverseOfArray);

		String payload = broaderType.toString();
		logger.info("createHierarchicalRelationshipType making call: {}", payload);
		makeRequest(getModelURL(), payload, RequestType.POST);
	}

	private JsonObject getHierarchicalRelationshipTypeJsonObject(Label label, String uri, String parentProperty) {
		JsonObject relationshipTypeObject = new JsonObject();

		JsonArray typeArray = new JsonArray();
		typeArray.add("owl:ObjectProperty");
		relationshipTypeObject.put("@type", typeArray);
		relationshipTypeObject.put("@id", uri);

		JsonArray labelArray = new JsonArray();
		JsonObject labelObject = new JsonObject();
		labelObject.put("@value", label.getValue());
		labelObject.put("@language", label.getLanguageCode());
		labelArray.add(labelObject);
		relationshipTypeObject.put("rdfs:label", labelArray);

		JsonArray domainArray = new JsonArray();
		JsonObject domainObject = new JsonObject();
		domainObject.put("@id", "skos:Concept");
		domainArray.add(domainObject);
		relationshipTypeObject.put("rdfs:domain", domainArray);

		JsonArray rangeArray = new JsonArray();
		JsonObject rangeObject = new JsonObject();
		rangeObject.put("@id", "skos:Concept");
		rangeArray.add(rangeObject);
		relationshipTypeObject.put("rdfs:range", rangeArray);

		JsonArray subPropertyOfArray = new JsonArray();
		JsonObject subPropertyOfObject = new JsonObject();
		subPropertyOfObject.put("@id", parentProperty);
		subPropertyOfArray.add(subPropertyOfObject);
		relationshipTypeObject.put("rdfs:subPropertyOf", subPropertyOfArray);

		return relationshipTypeObject;
	}

	// ======================================================================
	// Delete / Update Relationship Types
	// ======================================================================

	/**
	 * Delete a relationship type (hierarchical or associative) from the model.
	 *
	 * @param relationshipTypeUri the URI of the relationship type to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteRelationshipType(String relationshipTypeUri) throws OEClientException {
		logger.info("deleteRelationshipType entry: {}", relationshipTypeUri);

		String url = getModelURL() + "/" + getEscapedUri(relationshipTypeUri);
		logger.info("deleteRelationshipType URL: {}", url);
		makeRequest(url, null, RequestType.DELETE);
	}

	/**
	 * Update the label of a relationship type.
	 *
	 * @param relationshipTypeUri the URI of the relationship type
	 * @param oldLabel the current label
	 * @param newLabel the new label
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateRelationshipType(String relationshipTypeUri, Label oldLabel, Label newLabel) throws OEClientException {
		logger.info("updateRelationshipType entry: {} {} {}", relationshipTypeUri, oldLabel.getValue(), newLabel.getValue());

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0/rdfs:label/0");
		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldLabel.getValue());
		if (oldLabel.getLanguageCode() != null) {
			oldValueObject.put("@language", oldLabel.getLanguageCode());
		}
		testOperation.put("value", oldValueObject);
		operationList.add(testOperation);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", "@graph/0/rdfs:label/0");
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", "@graph/0/rdfs:label/-");
		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newLabel.getValue());
		if (newLabel.getLanguageCode() != null) {
			newValueObject.put("@language", newLabel.getLanguageCode());
		}
		addOperation.put("value", newValueObject);
		operationList.add(addOperation);

		String url = getModelURL() + "/" + getEscapedUri(relationshipTypeUri);
		String payload = operationList.toString();
		logger.info("updateRelationshipType payload: {}", payload);
		makeRequest(url, payload, RequestType.PATCH);
	}

	// ======================================================================
	// Update / Delete Metadata Types
	// ======================================================================

	/**
	 * Update the label of a metadata type.
	 *
	 * @param metadataTypeUri the URI of the metadata type
	 * @param oldLabel the current label
	 * @param newLabel the new label
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateMetadataType(String metadataTypeUri, Label oldLabel, Label newLabel) throws OEClientException {
		logger.info("updateMetadataType entry: {} {} {}", metadataTypeUri, oldLabel.getValue(), newLabel.getValue());

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0");
		JsonObject testValue = new JsonObject();
		testValue.put("@id", metadataTypeUri);
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0/rdfs:label/0");
		JsonObject oldValueObject = new JsonObject();
		oldValueObject.put("@value", oldLabel.getValue());
		if (oldLabel.getLanguageCode() != null) {
			oldValueObject.put("@language", oldLabel.getLanguageCode());
		}
		testOperation.put("value", oldValueObject);
		operationList.add(testOperation);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", "@graph/0/rdfs:label/0");
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", "@graph/0/rdfs:label/-");
		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newLabel.getValue());
		if (newLabel.getLanguageCode() != null) {
			newValueObject.put("@language", newLabel.getLanguageCode());
		}
		addOperation.put("value", newValueObject);
		operationList.add(addOperation);

		String url = getModelURL() + "/" + getEscapedUri(metadataTypeUri);
		String payload = operationList.toString();
		logger.info("updateMetadataType payload: {}", payload);
		makeRequest(url, payload, RequestType.PATCH);
	}

	/**
	 * Delete a metadata type from the model.
	 *
	 * @param metadataTypeUri the URI of the metadata type to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteMetadataType(String metadataTypeUri) throws OEClientException {
		logger.info("deleteMetadataType entry: {}", metadataTypeUri);

		String url = getModelURL() + "/" + getEscapedUri("<" + metadataTypeUri + ">");
		logger.info("deleteMetadataType URL: {}", url);
		makeRequest(url, null, RequestType.DELETE);
	}

	// ======================================================================
	// Delete Alt Label Types
	// ======================================================================

	/**
	 * Delete an alt label type from the model.
	 *
	 * @param altLabelTypeUri the URI of the alt label type to delete
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void deleteAltLabelType(String altLabelTypeUri) throws OEClientException {
		logger.info("deleteAltLabelType entry: {}", altLabelTypeUri);

		String url = getModelURL() + "/" + getEscapedUri("<" + altLabelTypeUri + ">");
		logger.info("deleteAltLabelType URL: {}", url);
		makeRequest(url, null, RequestType.DELETE);
	}

	// ======================================================================
	// Update Model
	// ======================================================================

	/**
	 * Update the display name of a model.
	 *
	 * @param model the model to update
	 * @param newDisplayName the new display name
	 * @throws OEClientException - an error has occurred contacting the server
	 */
	public void updateModel(Model model, String newDisplayName) throws OEClientException {
		logger.info("updateModel entry: {} {}", model.getUri(), newDisplayName);

		JsonArray operationList = new JsonArray();

		JsonObject testOperation = new JsonObject();
		testOperation.put("op", "test");
		testOperation.put("path", "@graph/0/rdfs:label/0");
		JsonObject testValue = new JsonObject();
		testValue.put("@value", model.getLabel().getValue());
		testOperation.put("value", testValue);
		operationList.add(testOperation);

		JsonObject removeOperation = new JsonObject();
		removeOperation.put("op", "remove");
		removeOperation.put("path", "@graph/0/rdfs:label/0");
		operationList.add(removeOperation);

		JsonObject addOperation = new JsonObject();
		addOperation.put("op", "add");
		addOperation.put("path", "@graph/0/rdfs:label/-");
		JsonObject newValueObject = new JsonObject();
		newValueObject.put("@value", newDisplayName);
		addOperation.put("value", newValueObject);
		operationList.add(addOperation);

		String url = getApiURL() + "sys/" + model.getUri();
		String payload = operationList.toString();
		logger.info("updateModel payload: {}", payload);
		makeRequest(url, payload, RequestType.PATCH);
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

		if (languageExists) {
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
		operationObject.put("path", "@graph/0/dcterms:language/-");
		operationObject.put("value", valueObject);

		JsonArray patchArray = new JsonArray();
		patchArray.add(operationObject);

		return patchArray.toString();
	}
}