package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.OEFilter;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetFilteredConcepts extends ModelManipulation {
	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		OEFilter oeFilter = new OEFilter();
		oeFilter.setConceptClass("http://ontologies.smartlogic.com/Content-Intelligence#Advantage");

		Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
		for (Concept concept: concepts) {
			System.out.println(concept);
		}

		System.out.println(String.format("%d concepts returned", concepts.size()));
	}
}
