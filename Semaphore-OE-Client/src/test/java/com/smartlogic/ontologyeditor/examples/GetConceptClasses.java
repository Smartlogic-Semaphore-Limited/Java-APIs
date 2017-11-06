package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptClass;

public class GetConceptClasses extends ModelManipulation {
	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Collection<ConceptClass> conceptClasses = oeClient.getConceptClasses();
		for (ConceptClass conceptClass: conceptClasses) {
			System.out.println(conceptClass);
		}

		System.out.println(String.format("%d concept classes returned", conceptClasses.size()));

	}

}
