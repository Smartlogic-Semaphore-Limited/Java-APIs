package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class DeleteConceptToConceptRelationships extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DeleteConceptToConceptRelationships());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {


		Concept concept1 = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");
		Concept concept2 = oeClient.getConcept("http://example.com/APITest#MySecondConcept");

		oeClient.deleteRelationship("skos:related", concept1, concept2);
	}
}
