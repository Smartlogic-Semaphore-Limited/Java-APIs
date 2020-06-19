package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConcept extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConcept());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept returnedConcept = oeClient.getConcept("http://example.com/APITest#MySecondConcept");
		oeClient.populateRelatedConceptUris("skos:related", returnedConcept);
		System.err.println(returnedConcept.getRelatedConceptUris("skos:related"));
		
		oeClient.populateRelatedConceptUris("http://example.com/APITest#isSmallerThan", returnedConcept);
		System.err.println(returnedConcept.getRelatedConceptUris("http://example.com/APITest#isSmallerThan"));
		
		oeClient.populateAltLabels("skosxl:altLabel", returnedConcept);
		System.err.println(returnedConcept.getAltLabels("skosxl:altLabel"));
		
		oeClient.populateAltLabels("http://example.com/APITest#CodeName", returnedConcept);
		System.err.println(returnedConcept.getAltLabels("http://example.com/APITest#CodeName"));
			
	}
}
