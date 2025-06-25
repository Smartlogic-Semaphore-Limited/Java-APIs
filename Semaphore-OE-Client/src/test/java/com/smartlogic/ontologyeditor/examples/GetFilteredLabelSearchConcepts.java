package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.OEFilter;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.LabelFilter;

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

    addConcept(oeClient, conceptScheme, "My favorite mistake", null);
    addConcept(oeClient, conceptScheme, "My favorite things", null);
    addConcept(oeClient, conceptScheme, "My favorite season", null);
    addConcept(oeClient, conceptScheme, "My things", null);
    addConcept(oeClient, conceptScheme, "MY THINGS", null);
    addConcept(oeClient, conceptScheme, "If it makes you happy", null);
    addConcept(oeClient, conceptScheme, "It can't be that bad.", null);
    addConcept(oeClient, conceptScheme, "Will match on alt label", "My things");
    addConcept(oeClient, conceptScheme, "Will also match on alt label", "MY THINGS");
    addConcept(oeClient, conceptScheme, "I have a $ and a ^ in this string", "[There are]^too many $ in the bank.$");

    {
      OEFilter oeFilter = new OEFilter();
      LabelFilter anyLabelFilter = new LabelFilter("my favorite", null, false);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }

      System.out.println(String.format("%d concepts returned", concepts.size()));
    }

    {
      OEFilter oeFilter = new OEFilter();
      LabelFilter anyLabelFilter = new LabelFilter("^My things$", null, true);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }

      System.out.println(String.format("%d concepts returned", concepts.size()));
    }

    {
      OEFilter oeFilter = new OEFilter();
      LabelFilter anyLabelFilter = new LabelFilter("(?i)^My things$", null, true);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }

      System.out.println(String.format("%d concepts returned", concepts.size()));
    }

    {
      OEFilter oeFilter = new OEFilter();
      /*
      Here is the staring string unescaped:
      I have a $ and a ^ in this string
       */
      LabelFilter anyLabelFilter = new LabelFilter("(?i)^I have a \\$ and a \\^ in this string$", null, true);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }

      System.out.println(String.format("%d concepts returned", concepts.size()));
    }

    {
      OEFilter oeFilter = new OEFilter();
      /*
      Here is the staring string unescaped:
      [There are]^too many $ in the bank.$
       */
      LabelFilter anyLabelFilter = new LabelFilter("(?i)^\\[There are\\]\\^too many \\$ in the bank.\\$$", null, true);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }

      System.out.println(String.format("%d concepts returned", concepts.size()));
    }

    {
      OEFilter oeFilter = new OEFilter();
      LabelFilter anyLabelFilter = new LabelFilter("i", null, false);
      oeFilter.setAnyLabelFilter(anyLabelFilter);

      Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
      for (Concept concept : concepts) {
        Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
        if (pl == null) {
          System.out.println("Concept has no prefLabel: " + concept.getUri());
        } else {
          System.out.println("Concept prefLabel: " + pl.getValue());
        }
      }
      System.out.println(String.format("%d concepts returned", concepts.size()));
    }
  }

  private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label, String altLabel) throws OEClientException {

    List<Label> labels = new ArrayList<Label>();
    labels.add(new Label("en", label));

    Concept concept = new Concept(oeClient,
        "http://example.com/APITest#Concept" + urlEncode(label), labels);

    oeClient.createConcept(conceptScheme.getUri(), concept);

    if (altLabel != null) {
      oeClient.createLabel(concept, "skosxl:altLabel", new Label("en", altLabel));
    }
  }
}
