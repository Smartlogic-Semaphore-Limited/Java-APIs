package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class UpdateConceptLabel extends ModelManipulation {

	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept concept = oeClient.getConcept("http://example.com/APITest#MyFourthConcept");
		System.out.println("Before Change");
		System.out.println(concept.toString());

		String languageCode = "en";
		String oldLabelValue = "My fourh concept";
		String newLabelValue = "My fourth concept";

		Label label = concept.getPrefLabelByLanguageAndValue(languageCode, oldLabelValue);
		if (label == null) {
			System.err.println(String.format("Concept <%s> has no label '%s'@%s", concept.getUri(), oldLabelValue, languageCode));
			System.exit(1);
		}

		oeClient.updateLabel(label, languageCode, newLabelValue);

		Concept afterConcept = oeClient.getConcept("http://example.com/APITest#MyFourthConcept");
		System.out.println("After Change");
		System.out.println(afterConcept.toString());

	}
}
