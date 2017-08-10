package com.smartlogic.ontologyeditor;

import java.io.IOException;

import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptLabels extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(true);

		String[] uris = new String[] {
				"http://example.com/APITest#MyFirstConcept", "http://example.com/APITest#MySecondConcept", "http://example.com/APITest#MySecondConcept"
		};

		Label[] labels = new Label[] {
				new Label("de", "F1"), new Label("fr", "F2"), new Label("de", "D3")
		};


		oeClient.createLabels(uris, labels);
	}
}
