package com.smartlogic.semaphoremodel;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.SKOSXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Generates a GUID using a seed.  This will generate the same GUID usign the same seed.
	 * @param seed The seed to use for generating the GUID
	 * @return A string representation of the GUID
	 */
	public static String generateGuid(String seed) {
		UUID uuid;
		try {
			uuid = UUID.nameUUIDFromBytes(seed.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("UnsupportedEncodingException: %f", e.getMessage()));
		}
		return uuid.toString();
	}
	
	/**
	 * Generates a GUID using a seed.  This will generate the same GUID usign the same seed.
	 * @param seed The seed to use for generating the GUID
	 * @return A string representation of the GUID
	 */
	public static UUID generateUuid(String seed) {
		try {
			return UUID.nameUUIDFromBytes(seed.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("UnsupportedEncodingException: %f", e.getMessage()));
		}
	}
	
	/**
	 * Checks if a resource already exists in the model.
	 * @param model The model
	 * @param resourceUri An existing resource URI
	 * @throws ModelException Thrown if the URI doesn't exist.
	 */
	public static void checkResourceExistsInModel(Model model, String resourceUri) throws ModelException {
		Resource resource = ResourceFactory.createResource(resourceUri);
		if (!model.containsResource(resource)) throw new ModelException(String.format("URI '%s' is doesn't exist in the model", resource.getURI()));
	}

	/**
	 * Checks if a resource doesn't exist in the model. 
	 * @param model The model
	 * @param resourceUri The resource URI which SHOULD NOT exist in the model.
	 * @throws ModelException Thrown if the resource URI exists in the model.
	 */
	public static void checkResourceDoesntExistInModel(Model model, String resourceUri) throws ModelException {
		Resource resource = ResourceFactory.createResource(resourceUri);
		if (model.containsResource(resource)) throw new ModelException(String.format("URI '%s' is already used in the model", resource.getURI()));
	}
	
	/**
	 * Takes an altLabel URI and returns a map with the Alt Label Text as the key and the Concept Resource object as the value.  
	 * @param model - the underlying model containing the Semaphore model
	 * @param altTermRelationship - The altTermRelationship property.  If Null, this will default to SKOSXL.altLabel.
	 * @param language - The label language to use.  All other language labels will be discarded.  A language of null or empty string is considered language neutral.
	 * @param throwOnDuplicate - If true and there is a duplicate label / key, we will throw an exception.  Otherwise, a warning will be logged.
	 * @return A Map of AltLabel string literals (one for each language) as the key and the value of is the Concept Resource object.
	 * @throws ModelException - if throwOnDuplicate is set and a duplicate is encountered
	 */
	public static Map<String, Resource> getAltTermMap(Model model, Property altTermRelationship, String language, boolean throwOnDuplicate) throws ModelException {
		if(altTermRelationship == null){
			altTermRelationship = SKOSXL.altLabel;
		}
		if(language == null) language = "";
		
		ParameterizedSparqlString findConceptLabelsSparql = new ParameterizedSparqlString();
		findConceptLabelsSparql.setCommandText("SELECT ?conceptUri ?label { ?conceptUri ?altLabelURI ?labelUri . ?labelUri ?skosxlLiteralForm ?label. }");
		findConceptLabelsSparql.setParam("altLabelURI", altTermRelationship);
		findConceptLabelsSparql.setParam("skosxlPrefLabel", SKOSXL.prefLabel);
		findConceptLabelsSparql.setParam("skosxlLiteralForm", SKOSXL.literalForm);

		Query query = QueryFactory.create(findConceptLabelsSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = qexec.execSelect();
		
		Map<String, Resource> labels = new HashMap<String, Resource>();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Literal label = querySolution.getLiteral("?label");
			if(labels.containsKey(label.getString())){
				String errorString = String.format("The alternate label %s is already included in this map.  Skipping.", label);
				if(throwOnDuplicate) throw new ModelException(errorString);
				logger.warn(errorString);
			} else if (label.getLanguage().compareTo(language) == 0) {

				Resource conceptResource = querySolution.getResource("?conceptUri");
				labels.put(label.getString(), conceptResource);
			}
		}
		return labels;
	}

	/**
	 * Encode a string to be put into a URI. This is a bit custom: first
	 * convert all space sequences to a single "_" character, then use the
	 * Java URLEncoder to finish the job.
	 *
	 * @param value
	 * @return
	 */
	public static String encodeStringForURI(String value) {
		if (value == null)
			return null;
		value = value.replaceAll("[ ]+", "_");
		try {
			return URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Unsupported encoding exception while encoding string for uri", uee);
		}
	}
}
