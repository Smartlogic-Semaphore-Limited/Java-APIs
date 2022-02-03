package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConceptByName extends ModelManipulation {
  public static void main(String args[]) throws IOException, CloudException, OEClientException {
    runTests(new GetConceptByName());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    Concept conceptByName = oeClient.getConceptByName("skosxl:prefLabel", "Soyuz TMA-8", "en");
    System.err.println(conceptByName.toString());

    try {
      oeClient.getConceptByName("http://models.smartlogic.com/SpaceMissions#cosparId", "1969-018A",
          "en");
    } catch (OEClientException e) {
      System.err.println("Should get here 1: " + e.getMessage());
    }

    Concept conceptByAltNameLn = oeClient
        .getConceptByName("http://models.smartlogic.com/SpaceMissions#cosparId", "1969-018A", "");
    System.err.println(conceptByAltNameLn.toString());

    Concept conceptByAltName = oeClient
        .getConceptByName("http://models.smartlogic.com/SpaceMissions#cosparId", "1969-018A");
    System.err.println(conceptByAltName.toString());

    Collection<Concept> conceptByAltNames = oeClient.getConceptsByName(
        "http://models.smartlogic.com/SpaceMissions#cosparId", "1965-100A", "en");
    for (Concept concept : conceptByAltNames) {
      System.err.println(concept.toString());
    }

    try {
      oeClient.getConceptByName("skosxl:prefLabel", "Baïkonour LC31", "en");
    } catch (OEClientException e) {
      System.err.println("Should get here 2: " + e.getMessage());
    }

    Concept conceptByFrenchName =
        oeClient.getConceptByName("skosxl:prefLabel", "Baïkonour LC31", "fr");
    System.err.println(conceptByFrenchName.toString());

  }
}
