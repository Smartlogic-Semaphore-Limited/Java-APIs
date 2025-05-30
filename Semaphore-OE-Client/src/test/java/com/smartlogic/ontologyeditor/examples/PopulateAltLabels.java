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
 * Example (test) to make sure various types of alternative labels are populated
 * when using populateAltLabels
 */
public class PopulateAltLabels extends ModelManipulation {
  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new PopulateAltLabels());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for Populate Alt Labels"));

    ConceptScheme conceptScheme = new ConceptScheme(oeClient,
        "http://example.com/APITest#ConceptSchemeForPopulateAltLabels", csLabels);
    oeClient.createConceptScheme(conceptScheme);
    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "My first concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", cLabels1);
    oeClient.createConcept(conceptScheme.getUri(), concept1);

    oeClient.createLabel(concept1, "skosxl:altLabel", new Label("en", "My first concept alt 1"));
    oeClient.createLabel(concept1, "skosxl:altLabel", new Label("en", "My first concept alt 2"));
    Concept fetchedConcept = oeClient.getConcept(concept1.getUri());
    oeClient.populateAltLabels("skosxl:altLabel", fetchedConcept);

    System.out.println("Concept alt labels: \n" + fetchedConcept.getAltLabels("skosxl:altLabel").iterator().next().getValue());
  }

}
