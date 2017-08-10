package com.smartlogic.ontologyeditor;

import java.io.IOException;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptLabel extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept testFirstConcept = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");

		String relationshipType = "skosxl:altLabel";

		Label label = new Label("fr", "Concept Une");

		oeClient.createLabel(testFirstConcept, relationshipType, label);
	}
}
