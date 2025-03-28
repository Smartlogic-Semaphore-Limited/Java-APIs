package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.*;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;

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

		List<Label> cLabels3 = new ArrayList<Label>();
		cLabels3.add(new Label("en", "Six"));
		Concept concept3 = new Concept(oeClient, "http://example.com/APITest#MyAddedConcept3",
				cLabels3);
		Map<String, Collection<MetadataValue>> mdMap = new HashMap<>();
		mdMap.put("skos:note", List.of(new MetadataValue(null, "How many wives did Henry VIII have over the course of his life?")));
		oeClient.createConcept(conceptScheme.getUri(), concept3, mdMap);

		/* populate the note data in the concept */
		Concept returnedConcept3 = oeClient.getConcept(concept3.getUri());
		oeClient.populateMetadata("skos:note", returnedConcept3);

		{
			List<Label> cLabels4 = new ArrayList<Label>();
			cLabels4.add(new Label("en", "Two"));
			Concept concept4 = new Concept(oeClient, "http://example.com/APITest#MyAddedConcept4",
					cLabels4);
			Map<String, Collection<MetadataValue>> mdMap1 = new HashMap<>();
			mdMap1.put("skos:note", List.of(new MetadataValue(null, "How many of his wives did Henry VIII execute?")));
			oeClient.createConceptBelowConcept(concept3.getUri(), concept4, mdMap1);

			/* populate the note data in the concept */
			Concept returnedConcept4 = oeClient.getConcept(concept4.getUri());
			oeClient.populateMetadata("skos:note", returnedConcept4);
			System.out.println(returnedConcept4.getMetadata("skos:note").stream().findFirst().orElse(null).toString());
		}

	}

}
