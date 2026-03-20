package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;

import java.io.IOException;

public class LinkModels extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new LinkModels());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		Label modelLabel1 = new Label("", "Linking model");
		String comment1 = "Model created for testing the Java OE Client API";
		oeClient.setModelUri("model:LinkingModel");
		Model model1 = new Model(oeClient.getModelUri(), modelLabel1, comment1);
		oeClient.createModel(model1);

		Label modelLabel2 = new Label("", "Linked model");
		String comment2 = "Model created for testing the Java OE Client API";
		oeClient.setModelUri("model:LinkedModel");
		Model model2 = new Model(oeClient.getModelUri(), modelLabel2, comment2);
		oeClient.createModel(model2);

		oeClient.setModelUri(model1.getUri());
		oeClient.linkModel(model2.getUri());

	}
}
