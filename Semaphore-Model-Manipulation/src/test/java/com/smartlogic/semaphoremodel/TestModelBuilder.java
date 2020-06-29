package com.smartlogic.semaphoremodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

public class TestModelBuilder {

	public static void main(String[] args) throws ModelException, URISyntaxException, FileNotFoundException {
		Language english = Language.getLanguage("en");
		Language french = Language.getLanguage("fr");
		Language italian = Language.getLanguage("it");
		
		SemaphoreModel semaphoreModel = new SemaphoreModel();
		
		ConceptScheme conceptScheme = semaphoreModel.createConceptScheme(new URI("http://kma.com/ConceptScheme"), new Label("Concept Scheme", english), null);

		ConceptClass conceptClass1 = semaphoreModel.createConceptClass(new URI("http://kma.com/Class1"), new Label("Class 1", english));
		ConceptClass conceptClass2 = semaphoreModel.createConceptClass(new URI("http://kma.com/Class2"), new Label("Class 2", english));
		
		Concept concept1 = semaphoreModel.createConcept(new URI("http://kma.com/Concept1"), new Label("Concept1", english));
		conceptScheme.addTopConcept(concept1);
		conceptScheme.addLabel(new Label("Concepti Schema", italian));
		
		CalendarMetadataType metadataCreationDate = semaphoreModel.createCalendarMetadataType(new URI("http://kma.com/CreationDate"), new Label("Creation Date", english));
		concept1.addMetadata(metadataCreationDate, Calendar.getInstance());
		metadataCreationDate.setAlwaysVisibleProperty();
		metadataCreationDate.removeAlwaysVisibleProperty();
		
		IntegerMetadataType metadataCount = semaphoreModel.createIntegerMetadataType(new URI("http://kma.com/Count"), new Label("Count", english));
		metadataCount.setAlwaysVisibleProperty();
		concept1.addMetadata(metadataCount, 43);

		Concept concept2 = semaphoreModel.createConcept(new URI("http://kma.com/Concept2"), new Label("Second concept", english));
		conceptScheme.addTopConcept(concept2);
		
		AssocativeRelationshipType relationshipType = semaphoreModel.getAssociativeRelationshipType();
		concept1.addRelationship(relationshipType, concept2);
		
		HasBroaderRelationshipType isPartOf = semaphoreModel.createHasBroaderRelationshipType(new URI("http://kma.com/isPartOf"), new Label("is part of", english), new URI("http://kma.com/hasPart"), new Label("has part", english));
		isPartOf.addLabel(new Label("est un parte de", french));
		
		HasNarrowerRelationshipType hasPart = semaphoreModel.getNarrowerRelationshipType(new URI("http://kma.com/hasPart"));
		Concept concept3 = semaphoreModel.createConcept(new URI("http://kma.com/Concept3"), new Label("concept that is part of something", english));
		concept1.addRelation(hasPart, concept3);

		semaphoreModel.createAssociativeRelationshipType(new URI("http://kma.com/RT1"), new Label("RT1", english), new URI("http://kma.com/iRT1"), new Label("iRT1", english));
		semaphoreModel.createAssociativeRelationshipType(new URI("http://kma.com/RT2"), new Label("RT2", english), new URI("http://kma.com/iRT2"), new Label("iRT2", english), conceptClass1, conceptClass2);
		semaphoreModel.write(new File("C:/temp/model.ttl"));
	}

}
