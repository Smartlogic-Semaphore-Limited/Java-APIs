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

public class AddConcepts extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConcepts());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept Scheme for multiple concepts"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient,
				"http://example.com/APITest#ConceptSchemeForMultipleConcepts", labels);

		oeClient.createConceptScheme(conceptScheme);

		addConcept(oeClient, conceptScheme, "Concepts with a +");
		addConcept(oeClient, conceptScheme, "Concepts with : problems");
		addConcept(oeClient, conceptScheme, "Concepts - things");
		addConcept(oeClient, conceptScheme, "Concepts && Onions");
		addConcept(oeClient, conceptScheme, "Concepts || Parakeets");
		addConcept(oeClient, conceptScheme, "! a concept");
		addConcept(oeClient, conceptScheme, "Concepts with (brackets)");
		addConcept(oeClient, conceptScheme, "Concepts with {curly} brackets");
		addConcept(oeClient, conceptScheme, "Concepts with [square] brackets");
		addConcept(oeClient, conceptScheme, "^Concepts)");
		addConcept(oeClient, conceptScheme, "\"Quoted Concepts\"");
		addConcept(oeClient, conceptScheme, "~ is a cat");
		addConcept(oeClient, conceptScheme, "Are you sure?");
		addConcept(oeClient, conceptScheme, "Sometimes you just need a /");

	}

	private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label) {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept " + label));

		Concept concept = new Concept(oeClient,
				"http://example.com/APITest#Concept" + urlEncode(label), labels);

		oeClient.createConcept(conceptScheme.getUri(), concept);
	}

}
