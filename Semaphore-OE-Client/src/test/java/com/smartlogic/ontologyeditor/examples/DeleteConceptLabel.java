package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class DeleteConceptLabel extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DeleteConceptLabel());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Concept concept = oeClient.getConcept("http://example.com/APITest#MyConceptForAddConceptLabel");
		
		oeClient.populateAltLabels("skosxl:altLabel", concept);
		Label altLabel = concept.getAltLabels("skosxl:altLabel").toArray(new Label[0])[0];
		oeClient.deleteLabel("skosxl:altLabel", concept, altLabel);
		
		oeClient.populateAltLabels("http://example.com/APITest#CodeName", concept);
		Label codeLabel = concept.getAltLabels("http://example.com/APITest#CodeName").toArray(new Label[0])[0];
		oeClient.deleteLabel("http://example.com/APITest#CodeName", concept, codeLabel);
	}
}
