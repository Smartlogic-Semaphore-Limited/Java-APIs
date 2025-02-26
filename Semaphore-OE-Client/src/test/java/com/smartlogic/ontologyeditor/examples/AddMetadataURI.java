package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

//TODO This class must be updated when we have the ability to add bespoke metadata items

public class AddMetadataURI extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddMetadataURI());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Metadata URI"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddMetadataURI",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "Concept for metadata URI"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MyConceptForAddConceptMetadataURI", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);

		String metadataType = "skos:related";

		oeClient.setWarningsAccepted(true);

		try {
			URI uri = new URI("http://smartlogic.com/TestURIAgain");
			oeClient.createMetadata(concept, metadataType, uri);
		} catch (URISyntaxException e) {
			System.err.println("URL Syntax exception - shouldn't occur");
		}

	}

}
