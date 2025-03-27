package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.apache.commons.compress.utils.Sets;

import java.io.IOException;
import java.util.*;

/**
 * Example of adding multiple concepts in one method call.
 */
public class KRTAddConcepts extends ModelManipulation {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConcepts());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concepts"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptsToScheme",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> parentConceptLabels = new ArrayList<Label>();
    parentConceptLabels.add(new Label("en", "KRT Add Concepts: My Parent concept"));
    Concept parentConcept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptUnderConcept_MyParentConcept", parentConceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), parentConcept1);

    oeClient.setKRTClient(true);

    Set<Concept> newConcepts = Sets.newHashSet();

    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "AddConcepts: My Added concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#AddConcepts_KRT_MyAddedConcept1", cLabels1);
    String concept1UUID = UUID.randomUUID().toString();
    concept1.setGuid(concept1UUID);
    System.out.println("concept label: " + cLabels1.get(0).getValue());
    System.out.println("concept uuid: " + concept1UUID);
    newConcepts.add(concept1);

    List<Label> cLabels2 = new ArrayList<>();
    cLabels2.add(new Label("en", "AddConcepts: My Second Added concept"));
    Concept concept2 = new Concept(oeClient, "http://example.com/APITest#AddConcepts_KRT_MyAddedConcept2", cLabels2);
    String concept2UUID = UUID.randomUUID().toString();
    concept2.setGuid(concept2UUID);
    System.out.println("concept label: " + cLabels2.get(0).getValue());
    System.out.println("concept uuid: " + concept2UUID);
    newConcepts.add(concept2);

    oeClient.createConceptsBelowConcept(parentConcept1.getUri(), newConcepts);

  }
}
