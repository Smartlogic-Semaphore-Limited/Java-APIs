package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;

import com.smartlogic.ontologyeditor.beans.Concept;

public class GetConceptByGuid extends ModelManipulation {

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Concept concept = oeClient.getConceptByGuid("6843a802-4759-43f6-a1e5-d703f0394e14");
		System.out.println("Before Change");
		System.out.println(concept.toString());
	}
}
