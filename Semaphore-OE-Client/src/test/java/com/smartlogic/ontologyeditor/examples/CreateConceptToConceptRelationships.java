package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

public class CreateConceptToConceptRelationships extends ModelManipulation {

	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		String conceptSchemeURI = "http://example.com/APITest#myConceptScheme";

		List<Label> labels1 = new ArrayList<Label>();
		labels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
		concept1.addIdentifier(new Identifier("sem:guid", "27"));
		oeClient.createConcept(conceptSchemeURI, concept1);

		List<Label> labels2 = new ArrayList<Label>();
		labels2.add(new Label("en", "My second concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labels2);
		concept2.addIdentifier(new Identifier("sem:guid", "28"));
		oeClient.createConcept(conceptSchemeURI, concept2);

		oeClient.createRelationship("http://example.com/TestModel#isBiggerThan", concept1, concept2);
	}
}
