package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetConceptScheme extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConceptScheme());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Get Concept Scheme"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForGetConceptScheme",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);

		ConceptScheme returnedConceptScheme = oeClient.getConceptScheme("http://example.com/APITest#ConceptSchemeForGetConceptScheme");
		System.err.println(returnedConceptScheme);

	}
}
