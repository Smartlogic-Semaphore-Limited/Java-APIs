package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddConceptToReview extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptToReview());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		oeClient.setKRTClient(true);

		ConceptScheme newlyAddedConceptScheme = null;
		Collection<ConceptScheme> conceptSchemes = oeClient.getAllConceptSchemes();
		for (ConceptScheme conceptScheme: conceptSchemes) {
			Collection<Label> labels = conceptScheme.getPrefLabels();
			for (Label label: labels) {
				if (label.getValue().equals("Concept Review - Newly Added")) {
					newlyAddedConceptScheme = conceptScheme;
					break;
				}
			}
			if (newlyAddedConceptScheme != null) {
				break;
			}
		}


		addConcept(oeClient, newlyAddedConceptScheme, "Fruit Bats");
	}

	private void addConcept(OEClientReadWrite oeClient, ConceptScheme conceptScheme, String label) throws OEClientException {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", label));

		Concept concept = new Concept(oeClient,
				"http://example.com/APITest#Concept1" + urlEncode(label.replaceAll(" ", "")), labels);

		oeClient.createConcept(conceptScheme.getUri(), concept);
	}

}
