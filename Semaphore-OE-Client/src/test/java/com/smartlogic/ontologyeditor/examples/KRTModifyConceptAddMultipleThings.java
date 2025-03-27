package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class KRTModifyConceptAddMultipleThings extends ModelManipulation {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTModifyConceptAddMultipleThings());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Modify Concept Add Multiple"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTModifyConceptAddMultiple",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> conceptLabels = new ArrayList<Label>();
    conceptLabels.add(new Label("en", "KRT Modify Concept Add Multiple: My concept"));
    Concept concept = new Concept(oeClient, "http://example.com/APITest#KRTModifyConceptAddMultiple_MyConcept", conceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), concept);

    oeClient.setKRTClient(true);

    Label alt1 = new Label("en", "My alternative label 1");
    oeClient.createLabel(concept.getUri(), "skosxl:altLabel", alt1);

    Label alt2 = new Label("en", "My alternative label 2");
    oeClient.createLabel(concept.getUri(), "skosxl:altLabel", alt2);

    oeClient.createMetadata(concept, "skos:note", "This is a note", "en");

    try {
      URI uri = new URI("http://smartlogic.com/TestURIAgain");
      oeClient.createMetadata(concept, "http://example.com/APITest#uriMetadata", uri);
    } catch (URISyntaxException use) {
      System.err.println("URL Syntax exception - shouldn't occur");
    }

  }
}
