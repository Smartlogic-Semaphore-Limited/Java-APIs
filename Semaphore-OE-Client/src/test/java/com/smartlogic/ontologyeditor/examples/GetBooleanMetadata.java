package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

public class GetBooleanMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetBooleanMetadata());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Get Concept Boolean Metadata"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForGetConceptBooleanMetadata",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);

		List<Label> labels1 = new ArrayList<Label>();
		labels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
		concept1.addIdentifier(new Identifier("sem:guid", "27"));
		oeClient.createConcept(conceptScheme.getUri(), concept1);

		oeClient.createMetadata(concept1, "http://example.com/APITest#isGreen", true);

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");

		String metadataType = "http://example.com/APITest#isGreen";

		oeClient.populateBooleanMetadata(metadataType, testConcept);
		
		System.err.println("Metadata: " + metadataType);
		System.err.println(testConcept.getBooleanMetadata(metadataType));
	}

}
