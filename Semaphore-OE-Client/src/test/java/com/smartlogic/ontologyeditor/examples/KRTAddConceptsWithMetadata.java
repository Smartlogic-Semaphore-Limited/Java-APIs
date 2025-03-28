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

/**
 * Example of adding two concepts to KRT review with optional metadata.
 */
public class KRTAddConceptsWithMetadata extends ModelManipulation {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConceptsWithMetadata());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concepts with Metadata"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptsWithMetadata",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> parentConceptLabels = new ArrayList<Label>();
    parentConceptLabels.add(new Label("en", "KRT Add Concepts With Metadata: My Parent concept"));
    Concept parentConcept1 = new Concept(oeClient, "http://example.com/APITest#KRTAddConceptsWithMetadata_MyParentConcept", parentConceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), parentConcept1);

    oeClient.setKRTClient(true);

    List<Concept> newConcepts = Lists.newArrayList();
    List<Map<String, Collection<MetadataValue>>> newConceptMetadatas = Lists.newArrayList();

    List<Label> cLabels1 = new ArrayList<>();
    cLabels1.add(new Label("en", "AddConceptsWithMetadata: My Added concept with metadata"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#AddConceptsMetadata_KRT_MyAddedConcept1", cLabels1);
    MetadataValue md1 = new MetadataValue("", "This is a note about the new concept");
    newConceptMetadatas.add(Map.of("skos:note", Set.of(md1)));
    newConcepts.add(concept1);

    List<Label> cLabels2 = new ArrayList<>();
    cLabels2.add(new Label("en", "AddConceptsWithMetadata: My Second Added concept with metadata"));
    Concept concept2 = new Concept(oeClient, "http://example.com/APITest#AddConceptsMetadata_KRT_MyAddedConcept2", cLabels2);
    MetadataValue md2 = new MetadataValue("", "This is a note about the second new concept");
    newConceptMetadatas.add(Map.of("skos:note", Set.of(md2)));
    newConcepts.add(concept2);

    oeClient.createConceptsBelowConcept(parentConcept1.getUri(), newConcepts, newConceptMetadatas);

  }

}
