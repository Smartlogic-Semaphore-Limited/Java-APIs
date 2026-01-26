package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddMetadata extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddMetadata());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		oeClient.createMetadataTypeString(new Label("en", "String"), "http://example.com/APITest#String");
		oeClient.createMetadataTypeInteger(new Label("en", "Integer"), "http://example.com/APITest#Integer");
		oeClient.createMetadataTypeDecimal(new Label("en", "Decimal"), "http://example.com/APITest#Decimal");
		oeClient.createMetadataTypeDate(new Label("en", "Date"), "http://example.com/APITest#Date");
		oeClient.createMetadataTypeAnyURI(new Label("en", "AnyURI"), "http://example.com/APITest#AnyURI");
		oeClient.createMetadataTypeBoolean(new Label("en", "Boolean"), "http://example.com/APITest#Boolean");

		List<Label> csLabels = new ArrayList<Label>();
		csLabels.add(new Label("en", "Concept Scheme for Add Metadata"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#ConceptSchemeForAddMetadata",
				csLabels);
		oeClient.createConceptScheme(conceptScheme);
		
		List<Label> cLabels = new ArrayList<Label>();
		cLabels.add(new Label("en", "Concept for metadata"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MyConceptForAddConceptMetadata", cLabels);

		oeClient.createConcept(conceptScheme.getUri(), concept);

		oeClient.createMetadata(concept, "http://example.com/APITest#String", "String Value", "en");
		oeClient.createMetadata(concept, "http://example.com/APITest#Integer", 17);
		oeClient.createMetadata(concept, "http://example.com/APITest#Decimal", 23.876);
		oeClient.createMetadata(concept, "http://example.com/APITest#Date", new Date());
		oeClient.createMetadata(concept, "http://example.com/APITest#AnyURI", URI.create("http://myuri.com/grape#shot"));
		oeClient.createMetadata(concept, "http://example.com/APITest#Boolean", true);

		String metadataType1 = "http://example.com/APITest#NumberOfDonuts";

	}

}
