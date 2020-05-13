package com.smartlogic.ontologyeditor.examples;


import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Model;

public class GetAllModels extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetAllModels());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Collection<Model> models = oeClient.getAllModels();
		for (Model model: models) {
			System.err.println(model);
		}

		System.err.println(String.format("%d models returned", models.size()));
	}
}
