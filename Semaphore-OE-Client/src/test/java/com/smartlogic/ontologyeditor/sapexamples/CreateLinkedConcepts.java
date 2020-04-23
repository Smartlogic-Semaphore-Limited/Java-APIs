package com.smartlogic.ontologyeditor.sapexamples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.examples.ModelManipulation;

public class CreateLinkedConcepts extends ModelManipulation {

	public static void main(String[] args) throws IOException, OEClientException, CloudException {
		OEClientReadWrite oeClient = getOEClient(false);

		List<Label> labelsCS = new ArrayList<Label>();
		labelsCS.add(new Label("en", "MyConceptScheme"));
		String conceptSchemeURI = "http://example.com/APITest#MyConceptScheme";
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, conceptSchemeURI, labelsCS);
		oeClient.createConceptScheme(conceptScheme);

		List<Label> labelsC1 = new ArrayList<Label>();
		labelsC1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labelsC1);
		oeClient.createConcept(conceptSchemeURI, concept1);
		
		List<Label> labelsC2 = new ArrayList<Label>();
		labelsC2.add(new Label("en", "My second concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labelsC2);
		oeClient.createConcept(conceptSchemeURI, concept2);
		
		oeClient.createRelationship("http://example.com/TestModel#isBiggerThan", concept1, concept2);
		oeClient.createRelationship("skos:related", concept2, concept1);
	}
}
