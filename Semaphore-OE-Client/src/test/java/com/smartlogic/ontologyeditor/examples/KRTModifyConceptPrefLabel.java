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

/* Test of KRT modification. This test currently requires a model call KRTTest and concept called
BConcept. The concept URI should be <http://example.com/KRTTest#BConcept>
 */
public class KRTModifyConceptPrefLabel extends ModelManipulation {
  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new KRTModifyConceptPrefLabel());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Modify Concept Pref Label"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTModifyConceptPrefLabel",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> conceptLabels = new ArrayList<Label>();
    conceptLabels.add(new Label("en", "KRT Modify Concept Pref Label: My concept"));
    Concept concept = new Concept(oeClient, "http://example.com/APITest#KRTModifyConceptPrefLabel_MyConcept", conceptLabels);

    oeClient.createConcept(conceptScheme.getUri(), concept);

    oeClient.setKRTClient(true);

    Concept fetchedConcept = oeClient.getConcept(concept.getUri());
    Label prefLabel = fetchedConcept.getPrefLabels().stream().findFirst().get();
    oeClient.updateLabel(prefLabel, concept.getUri(), "skosxl:prefLabel", "en", "The Updated Label");
  }
}
