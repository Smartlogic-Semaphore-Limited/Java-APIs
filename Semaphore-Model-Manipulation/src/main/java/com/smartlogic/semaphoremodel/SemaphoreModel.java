package com.smartlogic.semaphoremodel;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

/**
 * A wrapper class around a Jena Model object that provides easy to use methods to manipulate the model.
 */
public class SemaphoreModel {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SemaphoreModel.class);

	/**
	 * Return a URI object via the specified model for a skos-xl Label object.
	 *
	 * @param model the model from which to build the URI
	 * @param conceptURIResource the concept URI for which to associate the Label object.
	 * @param relationshipProperty the property tye of the label.
	 * @param label the label literal string
	 * @return the URI object for the label.
	 * @throws ModelException ModelException
	 */
	static URI getLabelURI(Model model, Resource conceptURIResource, Property relationshipProperty, Label label) throws ModelException {
		URI labelURI = label.getURI();
		if (labelURI != null) {
			if (uriInUse(model, labelURI)) throw new ModelException("Attempting to create label with URI - '%s'. This URI is already in use.", labelURI.toString());
		} else {
			int counter = 0;
			String uriString = conceptURIResource.getURI() + "_" + relationshipProperty.getLocalName() + "_" + label.getLanguageCode();
			try {
				labelURI = new URI(uriString);
				while (uriInUse(model, labelURI)) {
					uriString = conceptURIResource.getURI() + "_" + relationshipProperty.getLocalName() + "_" + label.getLanguageCode() + counter++;
					labelURI = new URI(uriString);
				}
			} catch (URISyntaxException e) {
				throw new ModelException("Unable to create label URI: '%s'", uriString);
			}
		}
		return labelURI;
	}

	/**
	 * Returns true if the the model contains the specified URI (resource)
	 * @param model the Jena Model object.
	 * @param uri the URI of the object to check.
	 * @return true if the URI is in use, otherwise false.
	 */
	private static boolean uriInUse(Model model, URI uri) {
		return model.contains(resourceFromURI(model, uri), null);
	}

	/**
	 * Construct and return a Jena Resource object from the specified Jena Model using the URI
	 * @param model the Jena model to use to construct the Resource object.
	 * @param uri the URI for the Resource object.
	 * @return the Jena Resource object.
	 */
	protected static Resource resourceFromURI(Model model, URI uri) {
		return model.createResource(uri.toString());
	}

	/**
	 * Construct and return Jena Literal object from the spefified Jena model using the specified
	 * label.
	 * @param model the Jena model to use to construct the Resource object.
	 * @param prefLabel the label object from which to construct the Jena Literal object.
	 * @return the Jena Literal object.
	 */
	protected static Literal getAsLiteral(Model model, Label prefLabel) {
		return model.createLiteral(prefLabel.getValue(), prefLabel.getLanguageCode()).asLiteral();
	}

	/**
	 * Jena Model object for this wrapper class.
	 */
	private final Model model;

	/**
	 * Default constructor, uses a default Jena Model and sets up common namespace prefixes.
	 */
	public SemaphoreModel() {
		model = ModelFactory.createDefaultModel();
		setPrefixes();
	}

	/**
	 * Builds a new SemaphoreModel object that wraps the specified Jena model and sets up common namespace prefixes.
	 * @param model the Jena model that contains the triples.
	 */
	public SemaphoreModel(Model model) {
		this.model = model;
		setPrefixes();
	}

	/**
	 * Builds a new SemaphoreModel and populates the model with triples from the specified file.
	 * Sets up common namespace prefixes.
	 * @param inputFile the triples file from which to populate the SemaphoreModel.
	 */
	public SemaphoreModel(File inputFile) {
		Date startDate = new Date();
		
		model = ModelFactory.createDefaultModel();
		addFile(inputFile);

		Date readDate = new Date();
		logger.info(String.format("Data read in %#.3f seconds", 0.001*(readDate.getTime() - startDate.getTime())));
		
		setPrefixes();
	}

	/**
	 * Builds a new SemaphoreModel and populates the model with triples from the specified files.
	 * Sets up common namespace prefixes. All files are added to the same model (not separate named graphs).
	 * @param inputFiles the set of triples files from which to populate the SemaphoreModel.
	 */
	public SemaphoreModel(File[] inputFiles) {
		Date startDate = new Date();
		
		model = ModelFactory.createDefaultModel();
		for (File inputFile: inputFiles) {
			addFile(inputFile);
		}
		Date readDate = new Date();
		logger.info(String.format("Data read in %#.3f seconds", 0.001*(readDate.getTime() - startDate.getTime())));

		setPrefixes();
	}

	/**
	 * Returns the wrapped Jena model.
	 * @return
	 */
	public Model getModel() {
		return this.model;
	}

	/**
	 * Sets common namespace prefixes: sem, xsd, skos, skosxl, rdf, rdfs, owl and sioc
	 * in the Jena model. Serialized models will use these namespace prefixes for brevity.
	 */
	private void setPrefixes() {
		model.setNsPrefix("sem", "http://www.smartlogic.com/2014/08/semaphore-core#");
		model.setNsPrefix("sioc", "http://rdfs.org/sioc/ns#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		model.setNsPrefix("skosxl", "http://www.w3.org/2008/05/skos-xl#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
	}

	/**
	 * Load the specified file into the current SemaphoreModel object (Jena Model).
	 * All triples are loaded into the wrapped model object, not as separate named graphs.
	 * @param inputFile the input from which to load the triples. Must be valid triple file (RDF, TTL, NT)
	 */
	private void addFile(File inputFile) {
		logger.info("initialise - reading: {}", inputFile.getAbsolutePath());
		Path path = Paths.get(inputFile.getParent(), inputFile.getName());
		String fileType = "RDF/XML";
		if (inputFile.getName().endsWith("rdf") || inputFile.getName().endsWith("owl")) {
			fileType = "RDF/XML";
		} else if (inputFile.getName().endsWith("nt")) {
			fileType = "NT";
		} else if (inputFile.getName().endsWith("ttl")) {
			fileType = "TTL";
		}
		model.read(path.toUri().toString(), fileType);
		logger.info("initialise - read: {} total records: {}", inputFile, model.size());
	}

	/**
	 * Construct a new SemaphoreModel object backed by TDB database in specified folder
	 * with the specified graph name, optionally resetting the model when it loads.
	 * @param tdbDirectory The directory on disk in which to store the model database
	 * @param graphName the name of graph (uri syntax)
	 * @param resetTDB whether to reset (clear) the model.
	 * @throws IOException IOException
	 */
	public SemaphoreModel(File tdbDirectory, String graphName, boolean resetTDB) throws IOException {
		if (resetTDB && tdbDirectory.exists()) {
			FileUtils.deleteDirectory(tdbDirectory);
		}
		Dataset dataset = TDBFactory.createDataset(tdbDirectory.getAbsolutePath());

		if  ((graphName == null) || (graphName.trim().length() == 0)) {
			model = dataset.getNamedModel(graphName);
		} else {
			model = dataset.getDefaultModel();
		}
		setPrefixes();
	}

	/**
	 * Returns the size of the model as the number of triples.
	 * @return the number of triples in the model
	 */
	public long size() {
		return model.size();
	}

	/**
	 * Write the model to the specified file. If the file name has the .n3 extention,
	 * write the model as n3 format. If the file name has the extension .rdf or .xml,
	 * write the model as RDF/XML format. Otherwise, write the model in TTL (turtle) format.
	 * @param file the File object into which to write the file. Will be created if not present, cleared otherwise.
	 * @throws FileNotFoundException FileNotFoundException
	 */
	public void write(File file) throws FileNotFoundException {
		
		String fileName = file.getName().toUpperCase();
		String outputFormat = "TTL";
		if (fileName.endsWith("N3")) {
			outputFormat = "N3";
		} else if (fileName.endsWith("RDF") || (fileName.endsWith("XML"))) {
			outputFormat = "RDF/XML";
		} 
		model.write(new FileOutputStream(file), outputFormat);
	}

	/**
	 * Create and add a Concept to the model.
	 * The concept UUID will be randomly generated.
	 * @param uri the new concept's URI.
	 * @param prefLabel the new concept's preferred label.
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	public Concept createConcept(URI uri, Label prefLabel) throws ModelException {
		return createConcept(uri, prefLabel, (Resource[])null, null);
	}

	/**
	 * Deprecated - use createConcept with ConceptClass[] are Resource[] argument.
	 * @param uri do not use
	 * @param prefLabel do not use
	 * @param classURIs do not use
	 * @return do not use
	 * @throws ModelException ModelException
	 */
	@Deprecated
	public Concept createConcept(URI uri, Label prefLabel, URI[] classURIs) throws ModelException {
		Resource[] classResources = new Resource[classURIs.length];
		for (int i = 0; i < classURIs.length; i++) 
			classResources[i] = resourceFromURI(model, classURIs[i]);
		
		return createConcept(uri, prefLabel, classResources, null);
	}

	/**
	 * Creates and adds a new concept with the specified concept class and preferred label.
	 * The concept UUID will be randomly generated.
	 * @param uri the new concept's URI
	 * @param prefLabel the new concept's preferred label.
	 * @param conceptClass the new concept's concept class
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	public Concept createConcept(URI uri, Label prefLabel, ConceptClass conceptClass) throws ModelException {
		return createConcept(uri, prefLabel, new ConceptClass[] {conceptClass});
	}

	/**
	 * Create and add a concept to the model with the specified set of concept classes.
	 * The concept UUID will be randomly generated.
	 * @param uri the new concept's URI
	 * @param prefLabel the new concept's preferred label.
	 * @param conceptClasses an array of concept classes for the concept
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	public Concept createConcept(URI uri, Label prefLabel, ConceptClass[] conceptClasses) throws ModelException {
		return createConcept(uri, prefLabel, conceptClasses, null);
	}

	/**
	 * Create and add a concept to the model with the specified set of concept classes,
	 * specified preferred label and UUID.
	 * @param uri the new concept's URI
	 * @param prefLabel the new concept's preferred label.
	 * @param conceptClasses an array of concept classes for the concept
	 * @param uuid the UUID for the concept.
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	public Concept createConcept(URI uri, Label prefLabel, ConceptClass[] conceptClasses, UUID uuid) throws ModelException {
		Resource[] classResources = new Resource[conceptClasses.length];
		for (int i = 0; i < conceptClasses.length; i++) 
			classResources[i] = conceptClasses[i].getResource();
		return createConcept(uri, prefLabel, classResources, uuid);
	}

	/**
	 * Create and add a concept to the model with the specified preferred label and UUID.
	 * The concept class will be set to skos:Concept.
	 * @param uri the new concept's URI
	 * @param prefLabel the new concept's preferred label.
	 * @param uuid the new concept's UUID
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	public Concept createConcept(URI uri, Label prefLabel, UUID uuid) throws ModelException {
		return createConcept(uri, prefLabel, (Resource[])null, uuid);
	}

	/**
	 * Create and add a concept to the model with the specified set of concept classes (as Jena Resource objects),
	 * specified preferred label and UUID.
	 * @param uri the new concept's URI.
	 * @param prefLabel the new concept's preferred label
	 * @param classResources an array of Jena Resource objects representing classes
	 * @param uuid the new concept's UUID
	 * @return the new Concept object.
	 * @throws ModelException ModelException
	 */
	private Concept createConcept(URI uri, Label prefLabel, Resource[] classResources, UUID uuid) throws ModelException {
		Resource conceptURIResource = resourceFromURI(model, uri);

		if (resourceInUse(conceptURIResource)) throw new ModelException("Attempting to create concept with URI - '%s'. This URI is already in use.", uri.toString());
		
		if ((classResources == null) || (classResources.length == 0)) {
			conceptURIResource.addProperty(RDF.type, SKOS.Concept);
		} else {
			for (Resource classResource: classResources) {
				conceptURIResource.addProperty(RDF.type, classResource);
			}
		}
		
		conceptURIResource.addLiteral(SEM.guid, (uuid == null ? Utils.generateGuid(uri.toString()) : uuid.toString()));
			
		URI labelURI = getLabelURI(model, conceptURIResource, SKOS.prefLabel, prefLabel);
		Resource labelURIResource = resourceFromURI(model, labelURI);
		model.add(conceptURIResource, SKOSXL.prefLabel, labelURIResource);
		model.add(labelURIResource, SKOSXL.literalForm, getAsLiteral(model, prefLabel));
		model.add(labelURIResource, RDF.type, SKOSXL.Label);
		return new Concept(model, conceptURIResource);
	}

	/**
	 * Returns true if the specified Resource object is used in any triples.
	 * Used to identify existing resources when attempting to add new ones.
	 * @param resource the Jena Resource object (URI)
	 * @return true if the model contains the resource, otherwise false.
	 */
	private boolean resourceInUse(Resource resource) {
		return model.contains(resource, null);
	}

	/**
	 * Get the Concept object from the model by URI.
	 * @param uri the URI of the concept to get.
	 * @return the Concept object.
	 */
	public Concept getConcept(URI uri) {
		Resource conceptResource = resourceFromURI(model, uri);
		String sparql = "ASK WHERE { ?conceptURI a ?classURI . ?classURI rdfs:subClassOf* skos:Concept . }";
		ParameterizedSparqlString findConceptSparql = new ParameterizedSparqlString(model);
		findConceptSparql.setCommandText(sparql);
		findConceptSparql.setParam("conceptURI", conceptResource);
		
		Query query = QueryFactory.create(findConceptSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		return qexec.execAsk() ? new Concept(model, conceptResource) : null;
	}

	/**
	 * Create and add a new Concept Scheme with the specified URI, label and UUID.
	 * @param uri the new concept scheme's URI.
	 * @param label the new concept scheme's label.
	 * @param uuid the new concept scheme's UUID.
	 * @return the new ConceptScheme object.
	 * @throws ModelException ModelException
	 */
	public ConceptScheme createConceptScheme(URI uri, Label label, UUID uuid) throws ModelException {
		Resource conceptSchemeURIResource = resourceFromURI(model, uri);

		if (resourceInUse(conceptSchemeURIResource)) throw new ModelException(String.format("Attempting to create concept scheme with URI - '%s'. This URI is already in use.", uri.toString()));
		
		conceptSchemeURIResource.addProperty(RDF.type, SKOS.ConceptScheme);
		
		conceptSchemeURIResource.addLiteral(SEM.guid, (uuid == null ? Utils.generateGuid(uri.toString()) : uuid.toString()));
		conceptSchemeURIResource.addLiteral(RDFS.label, getAsLiteral(model, label));
			
		return new ConceptScheme(model, conceptSchemeURIResource);
	}

	/**
	 * Get a concept scheme from the model by URI.
	 * @param uri the URI of the scheme to get.
	 * @return the ConceptScheme object.
	 */
	public ConceptScheme getConceptScheme(URI uri) {

		Resource conceptSchemeResource = model.getResource(uri.toString());

		String sparql = "ASK WHERE { ?conceptSchemeURI a skos:ConceptScheme . }";
		ParameterizedSparqlString findConceptSchemeSparql = new ParameterizedSparqlString(model);
		findConceptSchemeSparql.setCommandText(sparql);
		findConceptSchemeSparql.setParam("conceptSchemeURI", conceptSchemeResource);

		Query query = QueryFactory.create(findConceptSchemeSparql.asQuery());

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			return qexec.execAsk() ? new ConceptScheme(model, conceptSchemeResource) : null;
		}
	}

	/**
	 * Create and add a new concept class to the model with the specified URI and label.
	 * This concept class with be a sub-class of skos:Concept.
	 * @param uri the new concept class' URI
	 * @param label the new concept class' label
	 * @return the new ConceptClass object.
	 * @throws ModelException ModelException
	 */
	public ConceptClass createConceptClass(URI uri, Label label) throws ModelException {
		return createConceptClass(uri, label, SKOS.Concept);
	}

	/**
	 * Create and add a new concept class to the model with the specified URI and label.
	 * This concept class with be a sub-class of specified concept class.
	 * @param uri the new concept class' URI
	 * @param label the new concept class' label
	 * @param parentURI the URI of the parent concept class.
	 * @return the new ConceptClass object.
	 * @throws ModelException ModelException
	 */
	public ConceptClass createConceptClass(URI uri, Label label, URI parentURI) throws ModelException {
		return createConceptClass(uri, label, resourceFromURI(model, parentURI));
	}

	/**
	 * Create and add a new concept class to the model with the specified URI and label.
	 * This concept class with be a sub-class of specified concept class (as Jena Resource).
	 * @param uri the new concept class' URI
	 * @param label the new concept class' label
	 * @param parentResource the URI of the parent concept class as Jena Resource.
	 * @return the new ConceptClass object.
	 * @throws ModelException ModelException
	 */
	public ConceptClass createConceptClass(URI uri, Label label, Resource parentResource) throws ModelException {
		Resource conceptClassURIResource = resourceFromURI(model, uri);

		if (resourceInUse(conceptClassURIResource)) throw new ModelException(String.format("Attempting to create concept class with URI - '%s'. This URI is already in use.", uri.toString()));
		
		conceptClassURIResource.addProperty(RDF.type, OWL.Class);
		conceptClassURIResource.addLiteral(RDFS.label, getAsLiteral(model, label));
		conceptClassURIResource.addProperty(RDFS.subClassOf, parentResource);	
		return new ConceptClass(model, conceptClassURIResource);
	}

	/**
	 * Return the concept class parent.
	 * @param cc the target concept class
	 * @return the parent of the target concept class.
	 */
	public ConceptClass getConceptClassParent(ConceptClass cc) {
		if (null == cc)
			return null;

		Statement stmt = model.getProperty(cc.getResource(), RDFS.subClassOf);
		if (null == stmt)
			return null;
		return new ConceptClass(model, stmt.getObject().asResource());
	}

	/**
	 * Gets the ConceptClass specified by label. If there is more than one class with this label,
	 * which is returned is indeterminate. Use getConceptClass with URI argument in that case.
	 * @param label the concept class' label
	 * @return the ConceptClass object.
	 * @throws ModelException ModelException
	 */
	public ConceptClass getConceptClass(Label label) throws ModelException {

		String sparql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				"SELECT * WHERE {\n" +
				"  ?cc_uri a owl:Class .\n" +
				"  ?cc_uri rdfs:subClassOf* skos:Concept .\n" +
				"  ?cc_uri rdfs:label ?cc_label_lit .\n" +
				"  ?cc_uri rdfs:label ?cc_label_arg .\n" +
				"}";
		ParameterizedSparqlString findConceptClass = new ParameterizedSparqlString(model);
		findConceptClass.setCommandText(sparql);
		findConceptClass.setLiteral("cc_label_arg", label.getValue(), label.getLanguageCode());

		Query query = QueryFactory.create(findConceptClass.asQuery());

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet rs = qexec.execSelect();
			if (!rs.hasNext()) {
				return null;
			}
			QuerySolution qs = rs.next();
			return constructConceptClassFromQuerySolution(qs);
		}
	}

	/**
	 * Construct a Jena Resource object from the specified URI.
	 * @param uri the URI from which to build the Resource object.
	 * @return the Jena Resource object.
	 */
	public Resource createResource(URI uri) {
		if (uri == null)
			return null;
		return model.createResource(uri.toString());
	}

	/**
	 * Construct a ConceptClass object from the query solution.
	 * Required parameter names: cc_uri and cc_label_lit.
	 * @param qs the query solution from which to build the ConceptClass.
	 * @return the ConceptClass object.
	 * @throws ModelException ModelException
	 */
	private ConceptClass constructConceptClassFromQuerySolution(QuerySolution qs) throws ModelException {
		if (null == qs)
			return null;
		Resource ccRes = qs.getResource("cc_uri");
		ConceptClass cc = new ConceptClass(this.model, ccRes);
		Literal ccLabelLit = qs.getLiteral("cc_label_lit");
		cc.setLabel(new Label(ccLabelLit.getString(), Language.getLanguage(ccLabelLit.getLanguage())));
		return cc;
	}

	private Resource createMetadataTypeResource(URI uri, Label label, Resource range) throws ModelException {
		
		Resource metadataTypeURIResource = resourceFromURI(model, uri);
		if (resourceInUse(metadataTypeURIResource)) throw new ModelException("Attempting to create metadata type with URI - '%s'. This URI is already in use.", uri.toString());

		metadataTypeURIResource.addProperty(RDF.type, OWL.DatatypeProperty);
		metadataTypeURIResource.addProperty(RDFS.label, getAsLiteral(model, label));
		metadataTypeURIResource.addProperty(RDFS.domain, SKOS.Concept);
		metadataTypeURIResource.addProperty(RDFS.range, range);
		return metadataTypeURIResource;
	}

	public BooleanMetadataType createBooleanMetadataType(URI uri, Label label) throws ModelException {
		Resource metadataTypeResource = createMetadataTypeResource(uri, label, XSD.xboolean);
		return new BooleanMetadataType(model, metadataTypeResource);
	}
	public IntegerMetadataType createIntegerMetadataType(URI uri, Label label) throws ModelException {
		Resource metadataTypeResource = createMetadataTypeResource(uri, label, XSD.integer);
		return new IntegerMetadataType(model, metadataTypeResource);
	}
	public CalendarMetadataType createCalendarMetadataType(URI uri, Label label) throws ModelException {
		Resource metadataTypeResource = createMetadataTypeResource(uri, label, XSD.date);
		return new CalendarMetadataType(model, metadataTypeResource);
	}
	public DateMetadataType createDateMetadataType(URI uri, Label label) throws ModelException {
		Resource metadataTypeResource = createMetadataTypeResource(uri, label, XSD.date);
		return new DateMetadataType(model, metadataTypeResource);
	}
	public StringMetadataType createStringMetadataType(URI uri, Label label) throws ModelException {
		Resource metadataTypeResource = createMetadataTypeResource(uri, label, XSD.xstring);
		return new StringMetadataType(model, metadataTypeResource);
	}

	public MetadataType getMetadataTypeIfExists(URI uri) throws ModelException {
		String sparql = "SELECT ?metadataTypeURI ?range WHERE { VALUES ?metadataTypeURI { ?suppliedURI } . ?metadataTypeURI a owl:DatatypeProperty .  ?metadataTypeURI rdfs:range ?range . }";
		ParameterizedSparqlString findConceptSchemeSparql = new ParameterizedSparqlString(model);
		findConceptSchemeSparql.setCommandText(sparql);
		findConceptSchemeSparql.setParam("suppliedURI", model.getResource(uri.toString()));
		
		Query query = QueryFactory.create(findConceptSchemeSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = qexec.execSelect();

		MetadataType metadataType = null;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource range = querySolution.getResource("?range");
			if (XSD.xstring.equals(range)) {
				metadataType = new StringMetadataType(model, querySolution.getResource("?metadataTypeURI"));
			} else if (XSD.date.equals(range)) {
				metadataType = new CalendarMetadataType(model, querySolution.getResource("?metadataTypeURI"));
			} else if (XSD.xboolean.equals(range)) {
				metadataType = new BooleanMetadataType(model, querySolution.getResource("?metadataTypeURI"));
			} else if (XSD.integer.equals(range)) {
				metadataType = new IntegerMetadataType(model, querySolution.getResource("?metadataTypeURI"));
			} else {
				throw new ModelException("Unable to determine range for metadata type with URI: '%s' - '%s'", uri, range);
			}
		}
		return metadataType;
	}
	
	public MetadataType getMetadataType(URI uri) throws ModelException {
		MetadataType metadataType = getMetadataTypeIfExists(uri);
		if (metadataType == null) 
			throw new ModelException("No metadata type exists with URI: %s", uri);
		return metadataType;
	}

	public HasBroaderRelationshipType createHasBroaderRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel) throws ModelException {
		return createHasBroaderRelationshipType(uri, label, inverseURI, inverseLabel, null, null);
	}

	public HasBroaderRelationshipType createHasBroaderRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel, ConceptClass domain, ConceptClass range) throws ModelException {
		Resource relationshipTypeResource = createConceptToConceptRelationshipTypeResource(uri, label, SKOS.broader, domain, range);
		Resource inverseRelationshipTypeResource = null;
		if (inverseURI != null) {
			inverseRelationshipTypeResource = createConceptToConceptRelationshipTypeResource(inverseURI, inverseLabel, SKOS.narrower, range, domain);
			relationshipTypeResource.addProperty(OWL.inverseOf, inverseRelationshipTypeResource);
			inverseRelationshipTypeResource.addProperty(OWL.inverseOf, relationshipTypeResource);
		}
		return new HasBroaderRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
	}
	
	
	public HasNarrowerRelationshipType createHasNarrowerRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel) throws ModelException {
		return createHasNarrowerRelationshipType(uri, label, inverseURI, inverseLabel, null, null);
	}
	
	public HasNarrowerRelationshipType createHasNarrowerRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel, ConceptClass domain, ConceptClass range) throws ModelException {
		Resource relationshipTypeResource = createConceptToConceptRelationshipTypeResource(uri, label, SKOS.narrower, domain, range);
		Resource inverseRelationshipTypeResource = null;
		if (inverseURI != null) {
			inverseRelationshipTypeResource = createConceptToConceptRelationshipTypeResource(inverseURI, inverseLabel, SKOS.broader, range, domain); // Inverse has domain and range reversed
			relationshipTypeResource.addProperty(OWL.inverseOf, inverseRelationshipTypeResource);
			inverseRelationshipTypeResource.addProperty(OWL.inverseOf, relationshipTypeResource);
		}
		return new HasNarrowerRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
	}
	
	public AssocativeRelationshipType createAssociativeRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel) throws ModelException {
		return createAssociativeRelationshipType(uri, label, inverseURI, inverseLabel, null, null);
	}
	
	public AssocativeRelationshipType createAssociativeRelationshipType(URI uri, Label label, URI inverseURI, Label inverseLabel, ConceptClass domain, ConceptClass range) throws ModelException {
		Resource relationshipTypeResource = createConceptToConceptRelationshipTypeResource(uri, label, SKOS.related, domain, range);
		Resource inverseRelationshipTypeResource = null;
		if (inverseURI == null) {
			relationshipTypeResource.addProperty(RDF.type, OWL.SymmetricProperty);
		} else {
			inverseRelationshipTypeResource = createConceptToConceptRelationshipTypeResource(inverseURI, inverseLabel, SKOS.related, range, domain); // Inverse has domain and range reversed
			relationshipTypeResource.addProperty(OWL.inverseOf, inverseRelationshipTypeResource);
			inverseRelationshipTypeResource.addProperty(OWL.inverseOf, relationshipTypeResource);
		}
		return new AssocativeRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
	}

	
	public HasEquivalentRelationshipType createHasEquivalentRelationshipType(URI uri, Label label) throws ModelException {
		Resource relationshipTypeResource = createConceptToLabelRelationshipTypeResource(uri, label, SKOSXL.altLabel, null);
		return new HasEquivalentRelationshipType(model, relationshipTypeResource);
	}
	public HasEquivalentRelationshipType createHasEquivalentRelationshipType(URI uri, Label label, Resource domain) throws ModelException {
		Resource relationshipTypeResource = createConceptToLabelRelationshipTypeResource(uri, label, SKOSXL.altLabel, null);
		return new HasEquivalentRelationshipType(model, relationshipTypeResource);
	}
	

	private Resource createRelationshipTypeResource(URI uri, Label label, Property parentProperty, ConceptClass domain) throws ModelException {
		Resource relationshipTypeURIResource = resourceFromURI(model, uri);
		if (resourceInUse(relationshipTypeURIResource)) throw new ModelException("Attempting to create relationship type with URI - '%s'. This URI is already in use.", uri.toString());

		relationshipTypeURIResource.addProperty(RDF.type, OWL.ObjectProperty);
		relationshipTypeURIResource.addProperty(RDFS.label, getAsLiteral(model, label));
		relationshipTypeURIResource.addProperty(RDFS.domain, domain == null ? SKOS.Concept : domain.getResource());
		relationshipTypeURIResource.addProperty(RDFS.subPropertyOf, parentProperty);
		return relationshipTypeURIResource;
		
	}
	private Resource createConceptToConceptRelationshipTypeResource(URI uri, Label label, Property parentProperty, ConceptClass domain, ConceptClass range) throws ModelException {
		Resource relationshipTypeURIResource =  createRelationshipTypeResource(uri, label, parentProperty, domain);
		relationshipTypeURIResource.addProperty(RDFS.range, range == null ? SKOS.Concept : range.getResource());
		return relationshipTypeURIResource;
	}

	private Resource createConceptToLabelRelationshipTypeResource(URI uri, Label label, Property parentProperty, ConceptClass domain) throws ModelException {
		Resource relationshipTypeURIResource =  createRelationshipTypeResource(uri, label, parentProperty, domain);
		relationshipTypeURIResource.addProperty(RDFS.range, SKOSXL.Label);
		return relationshipTypeURIResource;
	}

	public RelationshipType getRelationshipType(URI uri) throws ModelException {
		String sparql = 
				  "select ?relationshipTypeUri ?relationshipBaseType ?inverseRelationshipType where {"
				+ "  ?relationshipTypeUri rdfs:subPropertyOf* ?relationshipBaseType ."
				+ "  VALUES ?relationshipTypeUri { ?suppliedURI }"
				+ "  VALUES ?relationshipBaseType { skos:related skos:broader skos:narrower skosxl:altLabel }"
				+ "  OPTIONAL { ?relationshipTypeUri owl:inverseOf ?inverseRelationshipType }"
				+"}";
		ParameterizedSparqlString findRelationshipTypeSparql = new ParameterizedSparqlString(model);
		findRelationshipTypeSparql.setCommandText(sparql);
		findRelationshipTypeSparql.setParam("suppliedURI", model.getResource(uri.toString()));
		
		Query query = QueryFactory.create(findRelationshipTypeSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = qexec.execSelect();

		RelationshipType relationshipType = null;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource relationshipTypeResource = querySolution.getResource("?relationshipTypeUri");
			Resource inverseRelationshipTypeResource = querySolution.getResource("?inverseRelationshipType");
			Resource relationshipBaseTypeResource = querySolution.getResource("?relationshipBaseType");
			if ("broader".equals(relationshipBaseTypeResource.getLocalName())) {
				relationshipType = new HasBroaderRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
			} else if ("narrower".equals(relationshipBaseTypeResource.getLocalName())) {
				relationshipType = new HasNarrowerRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
			} else if ("related".equals(relationshipBaseTypeResource.getLocalName())) {
				relationshipType = new AssocativeRelationshipType(model, relationshipTypeResource, inverseRelationshipTypeResource);
			} else if ("altLabel".equals(relationshipBaseTypeResource.getLocalName())) {
				relationshipType = new HasEquivalentRelationshipType(model, relationshipTypeResource);
			} else {
				throw new ModelException("Unrecognized base type: '%s' for relationship '%s'", relationshipBaseTypeResource.getLocalName(), uri.toString());
			}
		}
		return relationshipType;
	}
	
	private <T extends RelationshipType> T getRelationshipType(URI uri, Resource baseTypeResource, Class<T> clazz) {
		String sparql = 
				  "select ?relationshipTypeUri ?inverseRelationshipType where {"
				+ "  ?relationshipTypeUri rdfs:subPropertyOf* ?relationshipBaseType ."
				+ "  VALUES ?relationshipTypeUri { ?suppliedURI }"
				+ "  OPTIONAL { ?relationshipTypeUri owl:inverseOf ?inverseRelationshipType }"
				+"}";
		ParameterizedSparqlString findRelationshipTypeSparql = new ParameterizedSparqlString(model);
		findRelationshipTypeSparql.setCommandText(sparql);
		findRelationshipTypeSparql.setParam("suppliedURI", model.getResource(uri.toString()));
		findRelationshipTypeSparql.setParam("relationshipBaseType", baseTypeResource);
		
		Query query = QueryFactory.create(findRelationshipTypeSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = qexec.execSelect();

		T relationshipType = null;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource relationshipTypeResource = querySolution.getResource("?relationshipTypeUri");
			Resource inverseRelationshipTypeResource = querySolution.getResource("?inverseRelationshipType");
			
			try {
				Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[] { Model.class, Resource.class, Resource.class });
				relationshipType = (T)constructor.newInstance(model, relationshipTypeResource, inverseRelationshipTypeResource);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new CodingException("Coding error: %s thrown creating relationship: %s", e.getClass().getName(), e.getMessage());			}
			
		}
		return relationshipType;
	}

	public AssocativeRelationshipType getAssociativeRelationshipType() throws ModelException {
		return new AssocativeRelationshipType(model, SKOS.related);
	}
	public HasBroaderRelationshipType getBroaderRelationshipType() {
		return new HasBroaderRelationshipType(model, SKOS.broader, SKOS.narrower);
	}
	public HasNarrowerRelationshipType getNarrowerRelationshipType() {
		return new HasNarrowerRelationshipType(model, SKOS.narrower, SKOS.broader);
	}
	public HasEquivalentRelationshipType getEquivalentRelationshipType() {
		return new HasEquivalentRelationshipType(model, SKOSXL.altLabel);
	}

	public AssocativeRelationshipType getAssociativeRelationshipType(URI uri) {
		return getRelationshipType(uri, SKOS.related, AssocativeRelationshipType.class);
	}
	public HasBroaderRelationshipType getBroaderRelationshipType(URI uri) {
		return getRelationshipType(uri, SKOS.broader, HasBroaderRelationshipType.class);
	}
	public HasNarrowerRelationshipType getNarrowerRelationshipType(URI uri) {
		return getRelationshipType(uri, SKOS.narrower, HasNarrowerRelationshipType.class);
	}
	public HasEquivalentRelationshipType getEquivalentRelationshipType(URI uri) {
		return getRelationshipType(uri, SKOSXL.altLabel, HasEquivalentRelationshipType.class);
	}
	
}
