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

public class AddConcept extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConcept());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Concept"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddConcept",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels1 = new ArrayList<Label>();
		cLabels1.add(new Label("en", "My Added concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyAddedConcept", cLabels1);

		oeClient.createConcept(conceptScheme.getUri(), concept1);
		
		List<Label> cLabels2 = new ArrayList<Label>();
		cLabels2.add(new Label("en", "My child concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MyChildConcept", cLabels2);

		oeClient.createConceptBelowConcept(concept1.getUri(), concept2);
	}

}
