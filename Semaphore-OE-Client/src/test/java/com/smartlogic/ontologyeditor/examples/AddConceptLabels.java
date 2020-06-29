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

public class AddConceptLabels extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptLabels());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for multiple labels"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient,
				"http://example.com/APITest#ConceptSchemeForMultipleLabels", csLabels);

		oeClient.createConceptScheme(conceptScheme);

		Concept concept1 = addConcept(oeClient, conceptScheme, "Concept 1");
		Concept concept2 = addConcept(oeClient, conceptScheme, "Concept 2");
		Concept concept3 = addConcept(oeClient, conceptScheme, "Concept 3");

		String[] uris = new String[] { concept1.getUri(), concept2.getUri(), concept3.getUri() };

		Label[] labels = new Label[] { new Label("de", "F1"), new Label("fr", "F2"), new Label("de", "D3") };

		oeClient.createLabels(uris, labels);
	}

	private Concept addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label) {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept " + label));

		Concept concept = new Concept(oeClient,
				"http://example.com/APITest#Concept" + urlEncode(label), labels);
		return concept;

	}
	
}
