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
import java.util.UUID;

/**
 * Example of adding a new concept to a KRT review. This adds to a new scheme, but user will
 * have to re-select it on approval. Better to add below an existing concept than just the scheme.
 */
public class KRTAddConceptToScheme extends ModelManipulation {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConceptToScheme());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concept to Scheme"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptToScheme",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    /* Now we turn on KRT mode and add a concept first to the scheme. */
    oeClient.setKRTClient(true);

    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "KRT Add Concept to Scheme: My Added concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptToScheme_MyAddedConcept1", cLabels1);
    String conceptUUID = UUID.randomUUID().toString();
    System.out.println("concept label: " + cLabels1.get(0).getValue());
    System.out.println("concept uuid: " + conceptUUID);
    concept1.setGuid(conceptUUID);

    oeClient.createConcept(conceptScheme.getUri(), concept1);

  }
}
