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

public class KRTAddConceptClasses extends ModelManipulation {
  public static void main(String args[]) throws IOException, CloudException, OEClientException {
    runTests(new KRTAddConceptClasses());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    List<Label> csLabels = new ArrayList<Label>();
    csLabels.add(new Label("en", "Concept Scheme for KRT Add Concept Classes"));
    ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForKRTAddConceptClasses",
        csLabels);
    oeClient.createConceptScheme(conceptScheme);

    List<Label> cLabels = new ArrayList<Label>();
    cLabels.add(new Label("en", "My concept for classes"));
    Concept concept1 = new Concept(oeClient, "http://example.com/APITest#ConceptForKRTAddConceptClasses", cLabels);

    oeClient.createConcept(conceptScheme.getUri(), concept1);

    oeClient.setKRTClient(true);

    oeClient.addClass(concept1, "http://example.com/APITest#Bluery");

    oeClient.addClass(concept1, "http://example.com/APITest#Greenery");

  }


}
