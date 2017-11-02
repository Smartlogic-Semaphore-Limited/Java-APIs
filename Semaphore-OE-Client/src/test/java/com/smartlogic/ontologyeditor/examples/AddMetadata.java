package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class AddMetadata extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		String metadataType = "skos:historyNote";

		String metadataValue = "In 1492 Columbus sailed the ocean blue";
		String metadataLanguage = "en";

		oeClient.createMetadata(testConcept, metadataType, metadataValue, metadataLanguage);
	}

}
