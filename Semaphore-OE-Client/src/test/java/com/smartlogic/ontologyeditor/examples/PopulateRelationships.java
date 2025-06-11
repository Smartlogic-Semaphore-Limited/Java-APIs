package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example (test) to make sure various types of related concepts are populated
 * when using populateRelatedConceptUris
 */
public class PopulateRelationships extends ModelManipulation {
  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new PopulateRelationships());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for Populate Relationships"));

    ConceptScheme conceptScheme = new ConceptScheme(oeClient,
        "http://example.com/APITest#ConceptSchemeForPopulateRelationships", csLabels);
    oeClient.createConceptScheme(conceptScheme);
    oeClient.setWarningsAccepted(true);
    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "My first concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", cLabels1);
    oeClient.createConcept(conceptScheme.getUri(), concept1);

    List<Label> cLabels2 = new ArrayList<>();
    cLabels2.add(new Label("en", "My second concept"));
    Concept concept2 =
        new Concept(oeClient, "http://example.com/APITest#MySecondConcept", cLabels2);
    oeClient.createConcept(conceptScheme.getUri(), concept2);

    List<Label> cLabels3 = new ArrayList<>();
    cLabels3.add(new Label("en", "My third concept"));
    Concept concept3 =
        new Concept(oeClient, "http://example.com/APITest#MyThirdConcept", cLabels3);

    oeClient.createConceptBelowConcept(concept2.getUri(), concept3);

    oeClient.createRelationship("skos:related", concept3, concept1);


    Concept fetchedConcept3 = oeClient.getConcept(concept3.getUri());
    oeClient.populateRelatedConceptUris("skos:broader", fetchedConcept3);
    oeClient.populateRelatedConceptUris("skos:related", fetchedConcept3);

    System.out.println("Concept 3 broader: " + fetchedConcept3.getRelatedConceptUris("skos:broader").iterator().next());
    System.out.println("Concept 3 related: " + fetchedConcept3.getRelatedConceptUris("skos:related").iterator().next());
  }
}
