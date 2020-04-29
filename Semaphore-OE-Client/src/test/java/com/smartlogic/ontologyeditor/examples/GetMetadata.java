package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

public class GetMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetMetadata());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MyConceptForAddConceptMetadata");

		String metadataType = "skos:historyNote";

		oeClient.populateMetadata(metadataType, testConcept);
		
		System.err.println("Metadata: " + metadataType);
		for (MetadataValue metadataValue: testConcept.getMetadata(metadataType)) {
			System.err.println(metadataValue);
		}
	}

}
