package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConceptByGuid extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(true);

		Concept concept = oeClient.getConceptByGuid("56441ea5-f0ac-44e9-a015-4627f05ae5af");
		System.out.println("Before Change");
		System.out.println(concept.toString());
	}
}
