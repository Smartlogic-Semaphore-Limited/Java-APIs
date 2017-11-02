package com.smartlogic.ontologyeditor.cloudexamples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConceptScheme extends CloudModelManipulation {

	public static void main(String[] args) throws IOException, CloudException {
		OEClientReadWrite oeClient = getCloudOEClient(false);

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "My concept scheme"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#myConceptScheme", labels);

		oeClient.createConceptScheme(conceptScheme);
	}


}
