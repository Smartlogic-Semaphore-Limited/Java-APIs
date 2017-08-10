package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConcepts extends ModelManipulation {

	public static void main(String[] args) throws IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		addConcept(oeClient, "Concepts with a +");
		addConcept(oeClient, "Concepts with : problems");
		addConcept(oeClient, "Concepts - things");
		addConcept(oeClient, "Concepts && Onions");
		addConcept(oeClient, "Concepts || Parakeets");
		addConcept(oeClient, "! a concept");
		addConcept(oeClient, "Concepts with (brackets)");
		addConcept(oeClient, "Concepts with {curly} brackets");
		addConcept(oeClient, "Concepts with [square] brackets");
		addConcept(oeClient, "^Concepts)");
		addConcept(oeClient, "\"Quoted Concepts\"");
		addConcept(oeClient, "~ is a cat");
		addConcept(oeClient, "Are you sure?");
		addConcept(oeClient, "Sometimes you just need a /");


	}

	private static void addConcept(OEClientReadWrite oeClient, String label) throws UnsupportedEncodingException {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Concept " + label));
		
		Concept concept = new Concept(oeClient, "http://example.com/APITest#Concept" + URLEncoder.encode(label, "UTF-8"), labels);

		String conceptSchemeURI = "http://example.com/APITest#" + URLEncoder.encode(label, "UTF-8");
		oeClient.createConcept(conceptSchemeURI, concept);
		
		
	}


}
