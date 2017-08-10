package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import com.smartlogic.ontologyeditor.beans.Concept;

public class GetAllConcepts extends ModelManipulation {
	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Collection<Concept> concepts = oeClient.getAllConcepts();
		for (Concept concept: concepts) {
			System.out.println(concept);
		}

		System.out.println(String.format("%d concepts returned", concepts.size()));

	}

}
