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
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "My concept to be updated"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MyConceptForUpdateConceptLabel", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);


		String languageCode = "en";
		String oldLabelValue = "My concept to be updated";
		String newLabelValue = "My updated concept label";

		Concept conceptToUpdate = oeClient.getConcept(concept.getUri());
		Label label = conceptToUpdate.getPrefLabelByLanguageAndValue(languageCode, oldLabelValue);
		if (label == null) {
			System.err.println("Unable to retrieve added label");
		}
		oeClient.updateLabel(label, languageCode, newLabelValue);

	}
}
