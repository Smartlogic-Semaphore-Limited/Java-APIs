package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptScheme extends ModelManipulation {

	public static void main(String[] args) throws IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "My concept scheme 2"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#myConceptScheme2", labels);

		oeClient.createConceptScheme(conceptScheme);
	}


}
