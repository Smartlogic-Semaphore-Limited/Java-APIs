package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.OEFilter;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetFilteredLabelSearchConcepts extends ModelManipulation {

  public static void main(String args[]) throws IOException, CloudException, OEClientException {
    runTests(new GetFilteredLabelSearchConcepts());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> labels = new ArrayList<Label>();
    labels.add(new Label("en", "Concept Scheme for searching for concepts"));

    ConceptScheme conceptScheme = new ConceptScheme(oeClient,
        "http://example.com/APITest#ConceptSchemeForSearchingConcepts", labels);

    oeClient.createConceptScheme(conceptScheme);

    addConcept(oeClient, conceptScheme, "My favorite mistake");
    addConcept(oeClient, conceptScheme, "My favorite things");
    addConcept(oeClient, conceptScheme, "My favorite season");
    addConcept(oeClient, conceptScheme, "My things");
    addConcept(oeClient, conceptScheme, "If it makes you happy");
    addConcept(oeClient, conceptScheme, "It can't be that bad.");

    OEFilter oeFilter = new OEFilter();
    oeFilter.setLabelPrefix("my favorite");

    Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
    for (Concept concept: concepts) {
      System.err.println(concept);
    }

    System.err.println(String.format("%d concepts returned", concepts.size()));

  }

  private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label) throws OEClientException {

    List<Label> labels = new ArrayList<Label>();
    labels.add(new Label("en", label));

    Concept concept = new Concept(oeClient,
        "http://example.com/APITest#Concept" + urlEncode(label), labels);

    oeClient.createConcept(conceptScheme.getUri(), concept);
  }

}
