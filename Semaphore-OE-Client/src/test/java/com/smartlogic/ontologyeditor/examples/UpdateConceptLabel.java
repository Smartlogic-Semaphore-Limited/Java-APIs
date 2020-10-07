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

public class UpdateConceptLabel extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new UpdateConceptLabel());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Update Concept Label"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForUpdateConceptLabel",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		oeClient.setWarningsAccepted(true);
		List<Label> cLabels1 = new ArrayList<Label>();
		cLabels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", cLabels1);
		oeClient.createConcept(conceptScheme.getUri(), concept1);

		List<Label> cLabels2 = new ArrayList<Label>();
		cLabels2.add(new Label("en", "My second concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", cLabels2);
		oeClient.createConcept(conceptScheme.getUri(), concept2);



		String languageCode = "en";
		String oldLabelValue = "My first concept";
		String newLabelValue = "My second concept";

		Concept conceptToUpdate = oeClient.getConcept(concept1.getUri());
		Label label = conceptToUpdate.getPrefLabelByLanguageAndValue(languageCode, oldLabelValue);
		if (label == null) {
			System.err.println("Unable to retrieve added label");
		}
		oeClient.updateLabel(label, languageCode, newLabelValue);

	}
}
