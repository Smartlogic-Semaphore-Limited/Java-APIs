package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Identifier;

public class GetConceptByIdentifier extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Identifier identifier = new Identifier("http://example.com/model#identifier", "iValue");
		Concept concept = oeClient.getConceptByIdentifier(identifier);

		System.out.println("Change");
		System.out.println(concept.toString());
	}
}
