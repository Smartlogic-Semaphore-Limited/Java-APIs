package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

public class GetMetadata extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept testConcept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		String metadataType = "skos:historyNote";

		oeClient.populateMetadata(metadataType, testConcept);
		
		System.out.println("Metadata: " + metadataType);
		for (MetadataValue metadataValue: testConcept.getMetadata(metadataType)) {
			System.out.println(metadataValue);
		}
	}

}
