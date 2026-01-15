package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Concept;

import java.util.Arrays;
import java.util.Map;

public class TestGetConceptsInCollection extends AbstractTestRunner {

  public static void main(String[] args) throws ConceptsException {

    Map<String, Concept> concepts = getConceptsClient().getConceptsInCollection("http://ontologies.smartlogic.com/Space-Missions#MyCollection");

    for (Concept concept : concepts.values()) {
      System.out.println(Arrays.toString(concept.getPrefLabels()));
    }

  }

}
