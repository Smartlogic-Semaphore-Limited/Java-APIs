package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

public class GetConceptByGuid extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptByGuid());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Get Concept By Guid"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForGetConceptByGuid",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);

		List<Label> labels1 = new ArrayList<Label>();
		labels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
		String guid = UUID.randomUUID().toString();
		System.err.println("Concept generated guid: " + guid);
		concept1.addIdentifier(new Identifier("sem:guid", guid));
		oeClient.createConcept(conceptScheme.getUri(), concept1);

		Concept concept = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");

		Concept conceptByGuid = oeClient.getConceptByGuid(concept.getGuid());
		System.err.println("Concept returned GUID: " + conceptByGuid.getGuid());
	}
}
