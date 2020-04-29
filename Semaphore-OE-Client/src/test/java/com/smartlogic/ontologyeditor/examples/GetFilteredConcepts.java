package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.OEFilter;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetFilteredConcepts extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetFilteredConcepts());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		OEFilter oeFilter = new OEFilter();
		oeFilter.setConceptClass("skos:Concept");

		Collection<Concept> concepts = oeClient.getFilteredConcepts(oeFilter);
		for (Concept concept: concepts) {
			System.err.println(concept);
		}

		System.err.println(String.format("%d concepts returned", concepts.size()));
	}
}
