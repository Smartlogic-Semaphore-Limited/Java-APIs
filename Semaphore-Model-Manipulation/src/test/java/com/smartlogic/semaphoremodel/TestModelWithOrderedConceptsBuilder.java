package com.smartlogic.semaphoremodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestModelWithOrderedConceptsBuilder {

  public static void main(String[] args)
      throws ModelException, URISyntaxException, FileNotFoundException {
    Language english = Language.getLanguage("en");
    Language french = Language.getLanguage("fr");
    Language italian = Language.getLanguage("it");

    SemaphoreModel semaphoreModel = new SemaphoreModel();

    ConceptScheme conceptScheme = semaphoreModel.createConceptScheme(
        new URI("http://kma.com/ConceptScheme"), new Label("Concept Scheme", english), null);

    Concept concept1 = semaphoreModel.createConcept(new URI("http://kma.com/Concept1"),
        new Label("Concept1", english));
    conceptScheme.addTopConcept(concept1);
    conceptScheme.addLabel(new Label("Concepti Schema", italian));

    HasBroaderRelationshipType isPartOf = semaphoreModel.createHasBroaderRelationshipType(
        new URI("http://kma.com/isPartOf"), new Label("is part of", english),
        new URI("http://kma.com/hasPart"), new Label("has part", english));
    isPartOf.addLabel(new Label("est un parte de", french));

    HasNarrowerRelationshipType hasPart =
        semaphoreModel.getNarrowerRelationshipType(new URI("http://kma.com/hasPart"));

    Concept conceptC = semaphoreModel.createConcept(new URI("http://kma.com/ConceptC"),
        new Label("C concept that is part of cab ordering", english));

    OrderedCollection orderedCollection =
        concept1.addOrderedCollection(OrderedCollection.Type.NARROWER, hasPart, conceptC);

    Concept conceptA = semaphoreModel.createConcept(new URI("http://kma.com/ConceptA"),
        new Label("A concept that is part of cab ordering", english));
    orderedCollection.addValue(semaphoreModel.getNarrowerRelationshipType(), conceptA);

    Concept conceptB = semaphoreModel.createConcept(new URI("http://kma.com/ConceptB"),
        new Label("B concept that is part of cab ordering", english));
    orderedCollection.addValue(hasPart, conceptB);

    semaphoreModel.write(new File("C:/temp/model.ttl"));
  }

}
