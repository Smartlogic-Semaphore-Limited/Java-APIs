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

public class DeleteConceptToConceptRelationships extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DeleteConceptToConceptRelationships());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Delete Linked Concepts"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForDeleteLinkConcepts",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);

		List<Label> labels1 = new ArrayList<Label>();
		labels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
		concept1.addIdentifier(new Identifier("sem:guid", "27"));
		oeClient.createConcept(conceptScheme.getUri(), concept1);

		List<Label> labels2 = new ArrayList<Label>();
		labels2.add(new Label("en", "My second concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labels2);
		concept2.addIdentifier(new Identifier("sem:guid", "28"));
		oeClient.createConcept(conceptScheme.getUri(), concept2);

		oeClient.createRelationship("http://example.com/APITest#isBiggerThan", concept1, concept2);

		concept1 = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");
		concept2 = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		oeClient.deleteRelationship("http://example.com/APITest#isBiggerThan", concept1, concept2);
	}
}
