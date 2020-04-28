package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class DeleteMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DeleteMetadata());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MyConceptForAddConceptMetadata");

		String metadataType = "skos:historyNote";

		String metadataValue = "In 1492 Columbus sailed the ocean blue";
		String metadataLanguage = "en";

		oeClient.deleteMetadata(metadataType, testConcept, metadataValue, metadataLanguage);
	}

}
