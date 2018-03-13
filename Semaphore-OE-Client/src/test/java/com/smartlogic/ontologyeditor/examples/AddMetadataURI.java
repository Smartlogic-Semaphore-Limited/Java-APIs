package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class AddMetadataURI extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException, URISyntaxException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept testConcept = oeClient.getConcept("http://example.com/URIMetadata#MySecondConcept");

		String metadataType = "http://example.com/URIMetadata#TestURI";

		URI uri = new URI("http://smartlogic.com/TestURIAgain");

		oeClient.createMetadata(testConcept, metadataType, uri);
	}

}
