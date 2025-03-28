package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import org.apache.commons.compress.utils.Lists;

import java.io.IOException;
import java.util.*;

public class KRTAddConceptsUnderConceptWithMetadataWithError extends ModelManipulation{

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConceptsUnderConceptWithMetadataWithError());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concepts with Metadata Error"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptsWithMetadataWithError",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> parentConceptLabels = new ArrayList<Label>();
    parentConceptLabels.add(new Label("en", "KRT Add Concepts with Metadata With Error: My Parent concept"));
    Concept parentConcept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptsWithMetadataWithError_MyParentConcept", parentConceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), parentConcept1);

    oeClient.setKRTClient(true);

    List<Concept> newConcepts = Lists.newArrayList();
    List<Map<String, Collection< MetadataValue>>> metadataValues = Lists.newArrayList();

    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "AddConcepts: My Added concept"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#AddConcepts_KRT_MyAddedConcept1", cLabels1);
    String concept1UUID = UUID.randomUUID().toString();
    concept1.setGuid(concept1UUID);
    System.out.println("concept label: " + cLabels1.get(0).getValue());
    System.out.println("concept uuid: " + concept1UUID);
    newConcepts.add(concept1);
    metadataValues.add(
        Map.of("skos:note",
            Set.of(new MetadataValue("en", "This is the note for concept 1"))));

    /* second concept has same pref label so should fail with warning */
    List<Label> cLabels2 = new ArrayList<>();
    cLabels2.add(new Label("en", "AddConcepts: My Added concept"));
    Concept concept2 = new Concept(oeClient, "http://example.com/APITest#AddConcepts_KRT_MyAddedConcept2", cLabels2);
    String concept2UUID = UUID.randomUUID().toString();
    concept2.setGuid(concept2UUID);
    System.out.println("concept label: " + cLabels2.get(0).getValue());
    System.out.println("concept uuid: " + concept2UUID);
    newConcepts.add(concept2);
    metadataValues.add(
        Map.of("skos:note",
            Set.of(new MetadataValue("en", "This is the note for concept 2"))));

    /* third concept is ok */
    List<Label> cLabels3 = new ArrayList<>();
    cLabels3.add(new Label("en", "AddConcepts: My Added concept 2"));
    Concept concept3 = new Concept(oeClient, "http://example.com/APITest#AddConcepts_KRT_MyAddedConcept3", cLabels3);
    String concept3UUID = UUID.randomUUID().toString();
    concept3.setGuid(concept3UUID);
    System.out.println("concept label: " + cLabels3.get(0).getValue());
    System.out.println("concept uuid: " + concept3UUID);
    newConcepts.add(concept3);
    metadataValues.add(null);

    /* should complete with a warning in log. */
    try {
      oeClient.createConceptsBelowConcept(parentConcept1.getUri(), newConcepts, metadataValues);
      System.out.println("Completed ok!");
    } catch (Throwable t) {
      System.out.println("caught an error on the multiple concept create call:");
      t.printStackTrace();
    }
  }
}
