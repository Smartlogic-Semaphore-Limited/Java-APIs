package com.smartlogic.ontologyeditor.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class AddConcept extends CloudModelManipulation {

	public static void main(String[] args) throws IOException, CloudException {
		OEClientReadWrite oeClient = getCloudOEClient(false);

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "My second concept"));
		Concept concept = new Concept(oeClient, "http://example.com/APITest#MySecondConcept", labels);

		String conceptSchemeURI = "http://example.com/APITest#myConceptScheme";
		oeClient.createConcept(conceptSchemeURI, concept);
	}


}
