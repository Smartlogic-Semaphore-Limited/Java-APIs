package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Identifier;

public class GetConceptByIdentifier extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptByIdentifier());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept concept = oeClient.getConcept("http://example.com/APITest#Concept%21+a+concept");
		
		Identifier identifier = new Identifier("sem:guid", concept.getGuid());
		Concept conceptByIdentifier = oeClient.getConceptByIdentifier(identifier);

		System.err.println(conceptByIdentifier.toString());
	}
}
