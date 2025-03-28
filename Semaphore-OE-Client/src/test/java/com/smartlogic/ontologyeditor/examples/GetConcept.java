package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Identifier;
import com.smartlogic.ontologyeditor.beans.Label;

public class GetConcept extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetConcept());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Get Concept"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForGetConcept",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);

		List<Label> labels1 = new ArrayList<Label>();
		labels1.add(new Label("en", "My first concept"));
		Concept concept1 = new Concept(oeClient, "http://example.com/APITest#MyFirstConcept", labels1);
		concept1.addIdentifier(new Identifier("sem:guid", "27"));
		oeClient.createConcept(conceptScheme.getUri(), concept1);

		List<Label> labels2 = new ArrayList<Label>();
		labels2.add(new Label("en", "My second concept"));
		Concept concept2 = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labels2);
		concept2.addIdentifier(new Identifier("sem:guid", "28"));
		oeClient.createConcept(conceptScheme.getUri(), concept2);

		oeClient.createRelationship("http://example.com/APITest#isBiggerThan", concept1, concept2);

		Label altLabel1 = new Label("en", "First Alt Label");
		oeClient.createLabel(concept1.getUri(), "skosxl:altLabel", altLabel1);

		Label altLabel2 = new Label("en", "CodeName Alt Label");
		oeClient.createLabel(concept1.getUri(), "http://example.com/APITest#CodeName", altLabel2);

		Concept returnedConcept1 = oeClient.getConcept("http://example.com/APITest#MyFirstConcept");
		oeClient.populateRelatedConceptUris("http://example.com/APITest#isBiggerThan", returnedConcept1);
		System.err.println(returnedConcept1.getRelatedConceptUris("http://example.com/APITest#isBiggerThan"));

		oeClient.populateRelatedConceptUris("skos:broader", returnedConcept1);
		System.err.println(returnedConcept1.getBroaderConceptUris());

		oeClient.populateRelatedConceptUris("skos:narrower", returnedConcept1);
		System.err.println(returnedConcept1.getNarrowerConceptUris());

		Concept returnedConcept2 = oeClient.getConcept("http://example.com/APITest#MySecondConcept");
		oeClient.populateRelatedConceptUris("http://example.com/APITest#isSmallerThan", returnedConcept2);
		System.err.println(returnedConcept2.getRelatedConceptUris("http://example.com/APITest#isSmallerThan"));
		
		oeClient.populateAltLabels("skosxl:altLabel", returnedConcept1);
		System.err.println(returnedConcept1.getAltLabels("skosxl:altLabel"));

		oeClient.populateAltLabels("http://example.com/APITest#CodeName", returnedConcept1);
		System.err.println(returnedConcept1.getAltLabels("http://example.com/APITest#CodeName"));
		
	}
}
