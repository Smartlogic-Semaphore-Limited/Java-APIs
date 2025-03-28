package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadOnly;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.BooleanMetadataValue;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class CreateBooleanMetadata extends ModelManipulation {
	private final static String conceptURI = "http://example.com/APITest#BooleanConcept";
	private final static String metadataType = "http://example.com/APITest#isGreen";

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new CreateBooleanMetadata());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Boolean Metadata"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddBooleanMetadata",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "Concept for boolean metadata"));
		Concept concept = new Concept(oeClient, conceptURI, cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);

		testMetadataValue(oeClient, null);

		oeClient.createMetadata(concept, metadataType, true);		
		testMetadataValue(oeClient, Boolean.TRUE);
		
		oeClient.updateMetadata(concept, metadataType, true, false);		
		testMetadataValue(oeClient, Boolean.FALSE);
		
		oeClient.deleteMetadata(concept, metadataType, false);		
		testMetadataValue(oeClient, null);

		oeClient.createMetadata(concept, metadataType, true);
		testMetadataValue(oeClient, Boolean.TRUE);

	}

	private void testMetadataValue(OEClientReadOnly oeClient, Boolean expected) throws OEClientException {
		Concept testConcept = oeClient.getConcept(conceptURI);
		
		oeClient.populateBooleanMetadata(metadataType, testConcept);
		
		BooleanMetadataValue actual = testConcept.getBooleanMetadata(metadataType);
		
		if (expected == null) {
			if (actual == null) {
				System.err.println("OK: Both null");
			} else {
				System.err.println("ERROR: null expected, " + actual.getValue() + " received");
			}
		} else if (actual == null) {
			System.err.println("ERROR: " + expected + " expected, null received");
		} else if (expected.equals(actual.getValue())) {
			System.err.println("OK: Both " + expected);
		} else {
			System.err.println("ERROR: " + expected + " expected, " + actual.getValue() + " received");
		}
		 
	}
}
