package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetBooleanMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetBooleanMetadata());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept testConcept = oeClient.getConcept("https://tags.services.sap.com/01200314690900002133");

		String metadataType = "https://tags.services.sap.com/Rejected";

		oeClient.populateBooleanMetadata(metadataType, testConcept);
		
		System.err.println("Metadata: " + metadataType);
		System.err.println(testConcept.getBooleanMetadata(metadataType));
	}

}
