package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KRTCreateConceptToConceptRelationship extends ModelManipulation {

  public static void main(String args[]) throws IOException, CloudException, OEClientException {
    runTests(new KRTCreateConceptToConceptRelationship());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for Link Concepts"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTLinkConcepts",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> labels1 = new ArrayList<Label>();
    labels1.add(new Label("en", "KRT Link Concepts: My first concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
    concept1.addIdentifier(new Identifier("sem:guid", "27"));
    oeClient.createConcept(conceptScheme.getUri(), concept1);

    List<Label> labels2 = new ArrayList<Label>();
    labels2.add(new Label("en", "KRT Link Concepts: My second concept"));
    Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labels2);
    concept2.addIdentifier(new Identifier("sem:guid", "28"));
    oeClient.createConcept(conceptScheme.getUri(), concept2);

    /* enable KRT mode and create the association */
    oeClient.setKRTClient(true);

    oeClient.createRelationship("http://example.com/APITest#isBiggerThan", concept1, concept2);


  }
}
