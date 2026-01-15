package com.smartlogic.concepts.examples;

import java.util.Map;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Concept;

public class TestGetAllConcepts extends AbstractTestRunner {

  public static void main(String[] args) throws ConceptsException {

    Map<String, Concept> concepts = getConceptsClient().getAllConcepts();

    System.out.println(concepts.get("67e1c35a-74d4-465d-98fa-f884d6eff372"));

  }

}
