package com.smartlogic.ontologyeditor.cloudexamples;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConcept extends CloudModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException, CloudException {
		OEClientReadWrite oeClient = getCloudOEClient(false);

		Concept concept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		System.out.println(concept.toString());
	}
}
