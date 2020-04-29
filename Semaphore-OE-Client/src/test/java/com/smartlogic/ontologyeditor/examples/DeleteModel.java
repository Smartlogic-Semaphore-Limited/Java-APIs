package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Model;

public class DeleteModel extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DeleteModel());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		Model model = new Model(oeClient.getModelUri(), null, null);
		oeClient.deleteModel(model);
	}
}
