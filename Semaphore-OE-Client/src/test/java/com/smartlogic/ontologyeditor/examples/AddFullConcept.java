// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.*;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptClass;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

/**
 * Example demonstrating how to create a complete concept with all its properties
 * in a single API request: preferred labels, alternative labels, custom classes,
 * metadata, identifiers, and relationships to other concepts.
 */
public class AddFullConcept extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddFullConcept());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		// 0. Create a custom class
		oeClient.createClass(new Label("en", "My Custom Class"), "http://example.com/APITest#MyCustomClass", new ConceptClass[]{});

		// 1. Create a concept scheme
		List<Label> csLabels = new ArrayList<>();
		csLabels.add(new Label("en", "Concept Scheme for Full Concept"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient,
				"http://example.com/APITest#ConceptSchemeForFullConcept", csLabels);
		oeClient.createConceptScheme(conceptScheme);

		// 2. Create a related concept to link to
		List<Label> relatedLabels = new ArrayList<>();
		relatedLabels.add(new Label("en", "Related Concept"));
		Concept relatedConcept = new Concept(oeClient,
				"http://example.com/APITest#RelatedConcept", relatedLabels);
		oeClient.createConcept(conceptScheme.getUri(), relatedConcept);

		// 3. Build a full concept with ALL properties set before creation
		List<Label> prefLabels = new ArrayList<>();
		prefLabels.add(new Label("en", "Full Concept Example"));
		prefLabels.add(new Label("fr", "Exemple de Concept Complet"));

		Concept fullConcept = new Concept(oeClient,
				"http://example.com/APITest#FullConceptExample", prefLabels);

		// Alt labels (standard and custom type)
		fullConcept.addAltLabel("skosxl:altLabel", new Label("en", "Complete Concept"));
		fullConcept.addAltLabel("skosxl:altLabel", new Label("de", "Vollständiges Konzept"));

		// Custom classes
		fullConcept.addClass("http://example.com/APITest#MyCustomClass");

		// Identifier
		fullConcept.addIdentifier(new Identifier("sem:guid", "a500a11c-6f0a-46dd-836e-1aac80144d09"));

		// Relationship to existing concept
		fullConcept.addRelationship("skos:related", relatedConcept.getUri());

		// Metadata
		Map<String, Collection<MetadataValue>> metadata = new HashMap<>();
		metadata.put("skos:note", List.of(
				new MetadataValue("en", "This is a note about the full concept")));
		metadata.put("skos:editorialNote", List.of(
				new MetadataValue("en", "Created via single-request full concept creation")));

		// 4. Create the concept with everything in a single request
		oeClient.createConcept(conceptScheme.getUri(), fullConcept, metadata);

		System.out.println("Full concept created successfully with prefLabels, altLabels, class, metadata, and relationship.");

		// 5. Also demonstrate with createConceptBelowConcept
		List<Label> childPrefLabels = new ArrayList<>();
		childPrefLabels.add(new Label("en", "Child Full Concept"));

		Concept childConcept = new Concept(oeClient,
				"http://example.com/APITest#ChildFullConcept", childPrefLabels);

		childConcept.addAltLabel("skosxl:altLabel", new Label("en", "Narrower Full Concept"));
		childConcept.addClass("http://example.com/APITest#MyCustomClass");
		childConcept.addRelationship("skos:related", relatedConcept.getUri());

		Map<String, Collection<MetadataValue>> childMetadata = new HashMap<>();
		childMetadata.put("skos:note", List.of(
				new MetadataValue("en", "Child concept note")));

		oeClient.createConceptBelowConcept(fullConcept.getUri(), childConcept, childMetadata);

		System.out.println("Child full concept created successfully below parent.");
	}
}
