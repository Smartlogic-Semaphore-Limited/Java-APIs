package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConcept extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(true);

		Concept concept = oeClient.getConcept("http://example.com/Playpen2#2a5e5c1c-5d63-40ea-a1dd-0611cdf4c036");

		System.out.println(concept.toString());
	}
}
