package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;

public class DeleteConceptToConceptRelationships extends ModelManipulation {

	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);


		Concept concept1 = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");
		Concept concept2 = oeClient.getConcept("http://example.com/TestModel#MyThirdConcept");

		oeClient.deleteRelationship("http://example.com/TestModel#isBiggerThan", concept1, concept2);
	}
}
