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

public class AddConceptLabel extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptLabel());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Concept Label"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddConceptLabel",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "My Added concept"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MyConceptForAddConceptLabel", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);

		String relationshipType = "skosxl:altLabel";
		Label label = new Label("fr", "Concept Une");
		oeClient.createLabel(concept, relationshipType, label);

		String relationshipType2 = "http://example.com/APITest#CodeName";
		Label label2 = new Label("de", "Die Code");
		oeClient.createLabel(concept, relationshipType2, label2);
	
	}
}
