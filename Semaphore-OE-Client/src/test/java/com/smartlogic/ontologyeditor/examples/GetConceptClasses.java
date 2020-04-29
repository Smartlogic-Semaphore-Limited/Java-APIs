package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptClass;

public class GetConceptClasses extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptClasses());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		Collection<ConceptClass> conceptClasses = oeClient.getConceptClasses();
		for (ConceptClass conceptClass: conceptClasses) {
			System.err.println(conceptClass);
		}

		System.err.println(String.format("%d concept classes returned", conceptClasses.size()));

	}

}
