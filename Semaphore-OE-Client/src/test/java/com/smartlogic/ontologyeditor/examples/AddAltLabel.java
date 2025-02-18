package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddAltLabel extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddAltLabel());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		oeClient.setProxyPort(8888);
		oeClient.setProxyHost("localhost");
//		List<Label> csLabels = new ArrayList<Label>();
//		csLabels.add(new Label("en", "Concept Scheme for Add Concept"));
//		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddAltLabel",
//				csLabels);
//		oeClient.createConceptScheme(conceptScheme);
//
//		List<Label> cLabels = new ArrayList<Label>();
//		cLabels.add(new Label("en", "Concept for alt label"));
//		Concept concept = new Concept(oeClient, "http://example.com/APITest#ConceptForAltLabel", cLabels);
//
//		oeClient.createConcept(conceptScheme.getUri(), concept);

		Concept returnedConcept = oeClient.getConcept("http://example.com/APITest#AltLabel");
		oeClient.createLabels(new String[] { returnedConcept.getUri() } , new Label[] { new Label("", "Hello World") });
	}

}
