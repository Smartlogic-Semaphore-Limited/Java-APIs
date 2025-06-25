package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadOnly;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.OEFilter;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.LabelFilter;

import java.io.IOException;
import java.util.Collection;

public class GetFilteredConceptsByPrefAltAltLabelConcepts extends GetFilteredConceptsByPrefAltLabelsBase {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new GetFilteredConceptsByPrefAltAltLabelConcepts());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    super.alterModel(oeClient);

    filterExampleCaseInsensitivePrefix(oeClient, null);
    filterExampleCaseInsensitivePrefix(oeClient, "en");
    filterExampleCaseInsensitivePrefix(oeClient, "l-n");
    filterExampleCaseInsensitivePrefix(oeClient, "de");

    filterExampleCaseSensitiveExactMatch(oeClient, null);
    filterExampleCaseSensitiveExactMatch(oeClient, "l-n");
    filterExampleCaseSensitiveExactMatch(oeClient, "en");
    filterExampleCaseSensitiveExactMatch(oeClient, "de");

    filterExampleCaseInsensitiveExactMatch(oeClient, null);
    filterExampleCaseInsensitiveExactMatch(oeClient, "l-n");
    filterExampleCaseInsensitiveExactMatch(oeClient, "en");
    filterExampleCaseInsensitiveExactMatch(oeClient, "de");
  }

  private void filterExampleCaseInsensitivePrefix(OEClientReadWrite oeClient, String langCode) throws OEClientException {
    OEFilter oeFilter = new OEFilter();

    LabelFilter myLabelFilter = new LabelFilter("aaa", langCode, false);
    myLabelFilter.setAltLabelType("skosxl:altLabel");
    oeFilter.setAltLabelFilter(myLabelFilter);
    System.out.println("Alt label OEFilter: " + oeFilter);
    Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
    for (Concept concept : concepts) {
      Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
      if (pl == null) {
        System.out.println("Concept has no prefLabel: " + concept.getUri());
      } else {
        System.out.println("Concept prefLabel: " + pl.getValue());
      }
    }

    System.out.printf("%d concepts returned%n", concepts.size());
  }

  private void filterExampleCaseSensitiveExactMatch(OEClientReadWrite oeClient, String langCode) throws OEClientException {
    OEFilter oeFilter = new OEFilter();

    LabelFilter myLabelFilter = new LabelFilter("^AAA$", langCode, true);
    myLabelFilter.setAltLabelType("skosxl:altLabel");
    oeFilter.setAltLabelFilter(myLabelFilter);
    System.out.println("Alt label OEFilter: " + oeFilter);
    // should match 2 concepts because we're using regex case-sensitive search with "AAA" no language code
    Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
    for (Concept concept : concepts) {
      Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
      if (pl == null) {
        System.out.println("Concept has no prefLabel: " + concept.getUri());
      } else {
        System.out.println("Concept prefLabel: " + pl.getValue());
      }
    }

    System.out.printf("%d concepts returned%n", concepts.size());
  }

  private void filterExampleCaseInsensitiveExactMatch(OEClientReadOnly oeClient, String langCode) throws OEClientException {
    OEFilter oeFilter = new OEFilter();

    LabelFilter myLabelFilter = new LabelFilter("(?i)^AAA$", langCode, true);
    myLabelFilter.setAltLabelType("skosxl:altLabel");
    oeFilter.setAltLabelFilter(myLabelFilter);
    System.out.println("Alt label OEFilter: " + oeFilter);
    // should match 4 concepts because we're using regex case-insensitive exact match on "AAA" no language code
    Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
    for (Concept concept : concepts) {
      Label pl = concept.getPrefLabels().stream().findFirst().orElse(null);
      if (pl == null) {
        System.out.println("Concept has no prefLabel: " + concept.getUri());
      } else {
        System.out.println("Concept prefLabel: " + pl.getValue());
      }
    }

    System.out.printf("%d concepts returned%n", concepts.size());

  }
}
