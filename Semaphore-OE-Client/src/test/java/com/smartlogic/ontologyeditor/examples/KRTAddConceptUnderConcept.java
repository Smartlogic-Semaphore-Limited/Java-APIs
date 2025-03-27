package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

import java.io.IOException;
import java.util.*;

public class KRTAddConceptUnderConcept extends ModelManipulation {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConceptUnderConcept());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concept Under Concept"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptUnderConcept",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> parentConceptLabels = new ArrayList<>();
    parentConceptLabels.add(new Label("en", "KRT Add Concept to Scheme: My Parent concept"));
    Concept parentConcept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptUnderConcept_MyParentConcept", parentConceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), parentConcept1);

    /* Now we turn on KRT mode and add a concept first to the scheme. */
    oeClient.setKRTClient(true);

    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "KRT Add Concept to Scheme: My Added concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptToScheme_MyAddedConcept1", cLabels1);
    String conceptUUID = UUID.randomUUID().toString();
    System.out.println("concept label: " + cLabels1.get(0).getValue());
    System.out.println("concept uuid: " + conceptUUID);
    concept1.setGuid(conceptUUID);

    Map<String, Collection<MetadataValue>> metadata = new HashMap<>();
    metadata.put("skos:note", List.of(new MetadataValue("en", "This is a note about the concept")));
    oeClient.createConceptBelowConcept(parentConcept1.getUri(), concept1, metadata);

  }
}
