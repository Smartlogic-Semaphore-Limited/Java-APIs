package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.util.ArrayList;
import java.util.List;

public class GetFilteredConceptsByPrefAltLabelsBase extends ModelManipulation {

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> labels = new ArrayList<Label>();
    labels.add(new Label("en", "Concept Scheme for get filtered by label concepts"));

    ConceptScheme conceptScheme = new ConceptScheme(oeClient,
        "http://example.com/APITest#ConceptSchemeForGetFilteredByLabelConcepts", labels);

    oeClient.createConceptScheme(conceptScheme);

    addConcept(oeClient, conceptScheme, "AAA", "en", null, null);
    addConcept(oeClient, conceptScheme, "aaa", "en", null, null);
    addConcept(oeClient, conceptScheme, "AAA", null, null, null);
    addConcept(oeClient, conceptScheme, "aaa", null, null, null);
    addConcept(oeClient, conceptScheme, "AAA", "de", null, null);
    addConcept(oeClient, conceptScheme, "aaa", "de", null, null);
    addConcept(oeClient, conceptScheme, "AAA is a great organization", "en", "ddd", "en");

    addConcept(oeClient, conceptScheme, "Test1", "en", "AAA", "en");
    addConcept(oeClient, conceptScheme, "test1", "en", "aaa", "en");
    addConcept(oeClient, conceptScheme, "Test1", null, "AAA", null);
    addConcept(oeClient, conceptScheme, "test1", null, "aaa", null);
    addConcept(oeClient, conceptScheme, "Test1", "de", "AAA", "de");
    addConcept(oeClient, conceptScheme, "test1", "de", "aaa", "de");

  }

  void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label, String labelLangCode,
                          String altLabelString, String altLabelLangCode) throws OEClientException {
    List<Label> labels = new ArrayList<Label>();
    labels.add(new Label(labelLangCode, label));

    Concept concept = new Concept(oeClient,
        "http://example.com/APITest#Concept" + urlEncode(label)
            + (labelLangCode != null ? "_" + labelLangCode : "_"), labels);

    oeClient.createConcept(conceptScheme.getUri(), concept);

    if (altLabelString != null) {
      Label altLabel = new Label(altLabelLangCode, altLabelString);
      oeClient.createLabel(concept, "skosxl:altLabel", altLabel);
    }
  }

  void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label, String altLabelString) throws OEClientException {
    addConcept(oeClient, conceptScheme, label, null, altLabelString, null);
  }


}
