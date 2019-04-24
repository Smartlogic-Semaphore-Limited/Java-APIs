package com.smartlogic.semaphoremodel;

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

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemaphoreModel {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

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

	private static boolean uriInUse(Model model, URI uri) {
		if (model.contains(resourceFromURI(model, uri), null)) return true;
		return false;
	}
	
	protected static Resource resourceFromURI(Model model, URI uri) {
		return model.createResource(uri.toString());
	}

	protected static Literal getAsLiteral(Model model, Label prefLabel) {
		return model.createLiteral(prefLabel.getValue(), prefLabel.getLanguageCode()).asLiteral();
	}

	private final Model model;
	
	public SemaphoreModel() {
		model = ModelFactory.createDefaultModel();
		setPrefixes();
	}
	
	public SemaphoreModel(Model model) {
		this.model = model;
		setPrefixes();
	}
	
	public SemaphoreModel(File inputFile) {
		Date startDate = new Date();
		
		model = ModelFactory.createDefaultModel();
		addFile(inputFile);

		Date readDate = new Date();
		logger.info(String.format("Data read in %#.3f seconds", 0.001*(readDate.getTime() - startDate.getTime())));
		
		setPrefixes();
	}

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

	
	private void addFile(File inputFile) {
		logger.info("initialise - reading: {}", inputFile.getAbsolutePath());
		Path path = Paths.get(inputFile.getParent(), inputFile.getName());
		String fileType = "RDF/XML";
		if (inputFile.getName().endsWith("rdf") || inputFile.getName().endsWith("owl")) {
			fileType = "RDF/XML";
		} else if (inputFile.getName().endsWith("ttl")) {
			fileType = "TTL";
		}
		model.read(path.toUri().toString(), fileType);
		logger.info("initialise - read: {} total records: {}", inputFile, model.size());
	}

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

	public long size() {
		return model.size();
	}

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


	public Concept createConcept(URI uri, Label prefLabel) throws ModelException {
		return createConcept(uri, prefLabel, (Resource[])null, null);
	}
	
	@Deprecated
	public Concept createConcept(URI uri, Label prefLabel, URI[] classURIs) throws ModelException {
		Resource[] classResources = new Resource[classURIs.length];
		for (int i = 0; i < classURIs.length; i++) 
			classResources[i] = resourceFromURI(model, classURIs[i]);
		
		return createConcept(uri, prefLabel, classResources, null);
	}
	
	public Concept createConcept(URI uri, Label prefLabel, ConceptClass[] conceptClasses) throws ModelException {
		return createConcept(uri, prefLabel, conceptClasses, null);
	}

	public Concept createConcept(URI uri, Label prefLabel, ConceptClass[] conceptClasses, UUID uuid) throws ModelException {
		Resource[] classResources = new Resource[conceptClasses.length];
		for (int i = 0; i < conceptClasses.length; i++) 
			classResources[i] = conceptClasses[i].getResource();
		return createConcept(uri, prefLabel, classResources, uuid);
	}

	public Concept createConcept(URI uri, Label prefLabel, UUID uuid) throws ModelException {
		return createConcept(uri, prefLabel, (Resource[])null, uuid);
	}

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
	
	private boolean resourceInUse(Resource resource) {
		if (model.contains(resource, null)) return true;
		return false;
	}

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
	
	public ConceptScheme createConceptScheme(URI uri, Label prefLabel, UUID uuid) throws ModelException {
		Resource conceptSchemeURIResource = resourceFromURI(model, uri);

		if (resourceInUse(conceptSchemeURIResource)) throw new ModelException(String.format("Attempting to create concept scheme with URI - '%s'. This URI is already in use.", uri.toString()));
		
		conceptSchemeURIResource.addProperty(RDF.type, SKOS.ConceptScheme);
		
		conceptSchemeURIResource.addLiteral(SEM.guid, (uuid == null ? Utils.generateGuid(uri.toString()) : uuid.toString()));
		conceptSchemeURIResource.addLiteral(RDFS.label, getAsLiteral(model, prefLabel));
			
		return new ConceptScheme(model, conceptSchemeURIResource);
	}

	public ConceptClass createConceptClass(URI uri, Label label) throws ModelException {
		return createConceptClass(uri, label, SKOS.Concept);
	}
	
	public ConceptClass createConceptClass(URI uri, Label label, URI parentURI) throws ModelException {
		return createConceptClass(uri, label, resourceFromURI(model, parentURI));
	}

	private ConceptClass createConceptClass(URI uri, Label label, Resource parentResource) throws ModelException {
		Resource conceptClassURIResource = resourceFromURI(model, uri);

		if (resourceInUse(conceptClassURIResource)) throw new ModelException(String.format("Attempting to create concept class with URI - '%s'. This URI is already in use.", uri.toString()));
		
		conceptClassURIResource.addProperty(RDF.type, OWL.Class);
		conceptClassURIResource.addLiteral(RDFS.label, getAsLiteral(model, label));
		conceptClassURIResource.addProperty(RDFS.subClassOf, parentResource);	
		return new ConceptClass(model, conceptClassURIResource);
	}

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
