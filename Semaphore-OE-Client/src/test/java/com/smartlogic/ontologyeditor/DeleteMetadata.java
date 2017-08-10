package com.smartlogic.ontologyeditor;

import java.io.IOException;

import com.smartlogic.ontologyeditor.beans.Concept;

public class DeleteMetadata extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		String metadataType = "skos:historyNote";

		String metadataValue = "In 1492 Columbus sailed the ocean blue";
		String metadataLanguage = "en";

		oeClient.deleteMetadata(metadataType, testConcept, metadataValue, metadataLanguage);
	}

}
