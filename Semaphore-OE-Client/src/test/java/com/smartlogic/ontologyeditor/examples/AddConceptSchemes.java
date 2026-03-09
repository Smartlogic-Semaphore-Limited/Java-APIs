package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptSchemes extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptSchemes());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException  {

		addConceptScheme(oeClient, "Concepts with a +");
		addConceptScheme(oeClient, "Concepts with : problems");
		addConceptScheme(oeClient, "Concepts - things");
		addConceptScheme(oeClient, "Concepts && Onions");
		addConceptScheme(oeClient, "Concepts || Parakeets");
		addConceptScheme(oeClient, "! a concept");
		addConceptScheme(oeClient, "Concepts with (brackets)");
		addConceptScheme(oeClient, "Concepts with {curly} brackets");
		addConceptScheme(oeClient, "Concepts with [square] brackets");
		addConceptScheme(oeClient, "^Concepts)");
		addConceptScheme(oeClient, "\"Quoted Concepts\"");
		addConceptScheme(oeClient, "~ is a cat");
		addConceptScheme(oeClient, "Are you sure?");
		addConceptScheme(oeClient, "Sometimes you just need a /");

		// Create multiple concept schemes in a single request
		List<ConceptScheme> batchSchemes = new ArrayList<ConceptScheme>();
		batchSchemes.add(buildConceptScheme(oeClient, "Batch Scheme Alpha"));
		batchSchemes.add(buildConceptScheme(oeClient, "Batch Scheme Beta"));
		batchSchemes.add(buildConceptScheme(oeClient, "Batch Scheme Gamma"));

		oeClient.createConceptSchemes(batchSchemes);
	}
	
	public void addConceptScheme(OEClientReadWrite oeClient, String schemeName) throws OEClientException {
		
		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", schemeName));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#" + urlEncode(schemeName), labels);

		oeClient.createConceptScheme(conceptScheme);
	}

	private ConceptScheme buildConceptScheme(OEClientReadWrite oeClient, String schemeName) {
		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", schemeName));
		return new ConceptScheme(oeClient, "http://example.com/APITest#" + urlEncode(schemeName), labels);
	}


}
