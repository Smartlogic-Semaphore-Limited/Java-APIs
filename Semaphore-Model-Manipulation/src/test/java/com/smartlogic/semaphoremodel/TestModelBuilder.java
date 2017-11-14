package com.smartlogic.semaphoremodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import com.smartlogic.semaphoremodel.AssocativeRelationshipType;
import com.smartlogic.semaphoremodel.CalendarMetadataType;
import com.smartlogic.semaphoremodel.Concept;
import com.smartlogic.semaphoremodel.ConceptScheme;
import com.smartlogic.semaphoremodel.HasBroaderRelationshipType;
import com.smartlogic.semaphoremodel.HasNarrowerRelationshipType;
import com.smartlogic.semaphoremodel.Label;
import com.smartlogic.semaphoremodel.Language;
import com.smartlogic.semaphoremodel.ModelException;
import com.smartlogic.semaphoremodel.SemaphoreModel;

public class TestModelBuilder {

	public static void main(String[] args) throws ModelException, URISyntaxException, FileNotFoundException {
		Language english = Language.getLanguage("en");
		Language french = Language.getLanguage("fr");
		Language italian = Language.getLanguage("it");
		
		SemaphoreModel semaphoreModel = new SemaphoreModel();
		
		ConceptScheme conceptScheme = semaphoreModel.createConceptScheme(new URI("http://kma.com/ConceptScheme"), new Label("Concept Scheme", english), null);

		Concept concept1 = semaphoreModel.createConcept(new URI("http://kma.com/Concept1"), new Label("Concept1", english));
		conceptScheme.addTopConcept(concept1);
		conceptScheme.addLabel(new Label("Concepti Schema", italian));
		
		CalendarMetadataType metadataCreationDate = semaphoreModel.createCalendarMetadataType(new URI("http://kma.com/CreationDate"), new Label("Creation Date", english));
		concept1.addMetadata(metadataCreationDate, Calendar.getInstance());

		Concept concept2 = semaphoreModel.createConcept(new URI("http://kma.com/Concept2"), new Label("Second concept", english));
		conceptScheme.addTopConcept(concept2);
		
		AssocativeRelationshipType relationshipType = semaphoreModel.getAssociativeRelationshipType();
		concept1.addRelationship(relationshipType, concept2);
		
		HasBroaderRelationshipType isPartOf = semaphoreModel.createHasBroaderRelationshipType(new URI("http://kma.com/isPartOf"), new Label("is part of", english), new URI("http://kma.com/hasPart"), new Label("has part", english));
		isPartOf.addLabel(new Label("est un parte de", french));
		
		HasNarrowerRelationshipType hasPart = semaphoreModel.getNarrowerRelationshipType(new URI("http://kma.com/hasPart"));
		Concept concept3 = semaphoreModel.createConcept(new URI("http://kma.com/Concept3"), new Label("concept that is part of something", english));
		concept1.addRelation(hasPart, concept3);
		
		semaphoreModel.write(new File("C:/temp/model.ttl"));
	}

}
