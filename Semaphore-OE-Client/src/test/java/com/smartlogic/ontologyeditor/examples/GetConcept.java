package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConcept extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(true);

		Concept concept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		System.out.println(concept.toString());
	}
}
