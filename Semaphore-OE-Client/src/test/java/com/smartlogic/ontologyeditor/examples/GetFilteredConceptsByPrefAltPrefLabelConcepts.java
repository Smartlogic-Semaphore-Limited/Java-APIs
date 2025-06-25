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

public class GetFilteredConceptsByPrefAltPrefLabelConcepts extends GetFilteredConceptsByPrefAltLabelsBase {

  public static void main(String[] args) throws IOException, CloudException, OEClientException {
    runTests(new GetFilteredConceptsByPrefAltPrefLabelConcepts());
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
    // search for concepts with pref label that starts with "aaa" case-insensitive, with a language code
    // if only pref label is lang-neutral, it will be in scope for search.
    LabelFilter myPrefLabelFilter = new LabelFilter("aaa", langCode, false);
    oeFilter.setPrefLabelFilter(myPrefLabelFilter);
    System.out.println("Pref label OEFilter: " + oeFilter);

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

    LabelFilter myPrefLabelFilter = new LabelFilter("^AAA$", langCode, true);
    oeFilter.setPrefLabelFilter(myPrefLabelFilter);
    System.out.println("Pref label OEFilter: " + oeFilter);

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

    LabelFilter myPrefLabelFilter = new LabelFilter("(?i)^AAA$", langCode, true);
    oeFilter.setPrefLabelFilter(myPrefLabelFilter);
    System.out.println("Pref label OEFilter: " + oeFilter);

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

