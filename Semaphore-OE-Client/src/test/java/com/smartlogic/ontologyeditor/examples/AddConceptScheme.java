package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptScheme extends ModelManipulation {

	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddConceptScheme());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "My concept scheme 2"));
		labels.add(new Label(null, "My concept scheme 2"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#myConceptScheme2",
				labels);

		oeClient.createConceptScheme(conceptScheme);
	}

}
