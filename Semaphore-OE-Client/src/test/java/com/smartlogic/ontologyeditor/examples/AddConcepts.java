// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
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

		// Create multiple concepts in a single request - mix of top concepts and narrower concepts
		List<Label> schemeLabels = new ArrayList<>();
		schemeLabels.add(new Label("en", "Batch Concept Scheme"));
		ConceptScheme batchScheme = new ConceptScheme(oeClient,
				"http://example.com/APITest#BatchConceptScheme", schemeLabels);
		oeClient.createConceptScheme(batchScheme);

		List<Label> parentLabels = new ArrayList<>();
		parentLabels.add(new Label("en", "Batch Parent Concept"));
		Concept parentConcept = new Concept(oeClient,
				"http://example.com/APITest#BatchParentConcept", parentLabels);
		oeClient.createConcept(batchScheme.getUri(), parentConcept);

		List<Concept> batchConcepts = new ArrayList<>();
		List<String> parentUris = new ArrayList<>();
		List<Boolean> asTopConcept = new ArrayList<>();

		batchConcepts.add(buildConcept(oeClient, "BatchTop1"));
		parentUris.add(batchScheme.getUri());
		asTopConcept.add(true);

		batchConcepts.add(buildConcept(oeClient, "BatchTop2"));
		parentUris.add(batchScheme.getUri());
		asTopConcept.add(true);

		batchConcepts.add(buildConcept(oeClient, "BatchNarrower1"));
		parentUris.add(parentConcept.getUri());
		asTopConcept.add(false);

		batchConcepts.add(buildConcept(oeClient, "BatchNarrower2"));
		parentUris.add(parentConcept.getUri());
		asTopConcept.add(false);

		oeClient.createConcepts(batchConcepts, parentUris, asTopConcept);

	}

	private Concept buildConcept(OEClientReadWrite oeClient, String name) {
		List<Label> labels = new ArrayList<>();
		labels.add(new Label("en", name));
		return new Concept(oeClient, "http://example.com/APITest#" + urlEncode(name), labels);
	}

	private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label) throws OEClientException {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept " + label));

		Concept concept = new Concept(oeClient,
				"http://example.com/APITest#Concept" + urlEncode(label), labels);

		oeClient.createConcept(conceptScheme.getUri(), concept);
	}

}
