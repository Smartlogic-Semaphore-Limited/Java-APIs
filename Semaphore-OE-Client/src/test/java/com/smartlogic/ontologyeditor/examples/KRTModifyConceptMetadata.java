package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KRTModifyConceptMetadata extends ModelManipulation {
  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTModifyConceptMetadata());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Modify Concept Metadata"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTModifyConceptMetadata",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> conceptLabels = new ArrayList<Label>();
    conceptLabels.add(new Label("en", "KRT Modify Concept Metadata: My concept"));
    Concept concept = new Concept(oeClient, "http://example.com/APITest#KRTModifyConceptMetadata_MyConcept", conceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), concept);
    oeClient.createMetadata(concept, "skos:note", "This is the original note", "en");

    oeClient.setKRTClient(true);

    oeClient.populateMetadata("skos:note", concept);
    MetadataValue md = concept.getMetadata("skos:note").stream().findFirst().get();
    oeClient.updateMetadata( concept,
        "skos:note",
        md.getLanguageCode(),
        md.getValue(),
        "en",
        "new value");
  }
}
