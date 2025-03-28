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

public class KRTModifyConceptAddAltLabel extends ModelManipulation {
  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTModifyConceptAddAltLabel());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Modify Concept Add Alt Label"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTModifyConceptAddAltLabel",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> conceptLabels = new ArrayList<>();
    conceptLabels.add(new Label("en", "KRT Modify Concept Add Alt Label: My concept"));
    Concept concept = new Concept(oeClient, "http://example.com/APITest#KRTModifyConceptAddAltLabel_MyConcept", conceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), concept);

    oeClient.setKRTClient(true);

    Label alt1 = new Label("en", "My alternative label 2");
    oeClient.createLabel(concept.getUri(), "skosxl:altLabel", alt1);
  }
}
