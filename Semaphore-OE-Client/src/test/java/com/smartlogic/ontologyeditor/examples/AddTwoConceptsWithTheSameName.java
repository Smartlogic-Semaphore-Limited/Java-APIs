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

public class AddTwoConceptsWithTheSameName extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddTwoConceptsWithTheSameName());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept Scheme for two concepts"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient,
				"http://example.com/APITest#ConceptSchemeForTwoConcepts", labels);

		oeClient.createConceptScheme(conceptScheme);

		oeClient.setWarningsAccepted(true);
		addConcept(oeClient, conceptScheme, "Duplicate Concept", "first concept");
		addConcept(oeClient, conceptScheme, "Duplicate Concept", "second concept");
	}

	private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label, String localName) throws OEClientException {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept " + label));

		Concept concept = new Concept(oeClient,
				"http://example.com/APITest#Concept" + urlEncode(localName), labels);

		oeClient.createConcept(conceptScheme.getUri(), concept);
	}

}
