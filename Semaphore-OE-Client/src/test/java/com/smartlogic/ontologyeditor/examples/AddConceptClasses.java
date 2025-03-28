package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptClasses extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptClasses());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Concept Classes"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddConceptClasses",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "My concept for classes"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#ConceptForAddConceptClasses", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept1);
		printClasses(oeClient, 1);
		
		
		oeClient.addClass(concept1, "http://example.com/APITest#Bluery");
		printClasses(oeClient, 1);

		oeClient.addClass(concept1, "http://example.com/APITest#Greenery");
		printClasses(oeClient, 2);

		/*
		oeClient.removeClass(concept1, "http://example.com/APITest#Bluery");
		printClasses(oeClient, 1);

		oeClient.removeClass(concept1, "http://example.com/APITest#Greenery");
		printClasses(oeClient, 1);
		*/
	}

	private void printClasses(OEClientReadWrite oeClient, int expectedSize) throws OEClientException {
		Concept testConcept = oeClient.getConcept("http://example.com/APITest#ConceptForAddConceptClasses");
		oeClient.populateClasses(testConcept);
		if (testConcept.getClassUris().size() == expectedSize) {
			System.err.print("OK ");
		} else {
			System.err.println("ERROR ");
		}
		System.err.println(testConcept.getClassUris());
		
	}

}
