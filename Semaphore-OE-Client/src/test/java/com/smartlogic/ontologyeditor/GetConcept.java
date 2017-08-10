package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConcept extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept concept = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");

		System.out.println(concept.toString());
	}
}
