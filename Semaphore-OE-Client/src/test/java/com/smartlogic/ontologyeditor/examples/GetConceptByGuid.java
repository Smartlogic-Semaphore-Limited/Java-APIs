package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConceptByGuid extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptByGuid());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept concept = oeClient.getConcept("http://example.com/APITest#Concept%21+a+concept");

		Concept conceptByGuid = oeClient.getConceptByGuid(concept.getGuid());
		System.err.println(conceptByGuid.toString());
	}
}
