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

public class AddMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddMetadata());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Metadata"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddMetadata",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "Concept for metadata"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MyConceptForAddConceptMetadata", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);


		String metadataType = "skos:historyNote";

		String metadataValue = "In 1492 Columbus sailed the ocean blue";
		String metadataLanguage = "en";

		oeClient.createMetadata(concept, metadataType, metadataValue, metadataLanguage);
	}

}
