package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;

import java.io.IOException;

public class GetConceptScheme extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptScheme());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept returnedConcept = oeClient.getConcept("http://example.com/APITest#Cheese");
		System.err.println(returnedConcept.getPrefLabels());


	ConceptScheme returnedConceptScheme = oeClient.getConceptScheme("http://example.com/APITest#ConceptScheme/NewConcept");
//      	ConceptScheme returnedConceptScheme = oeClient.getConceptScheme("http://ontology.meta.com/people#ConceptScheme/JaimeConceptScheme");
		System.err.println(returnedConceptScheme);

	}
}
