package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;

public class AddModel extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddModel());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
		Label modelLabel = new Label("", getModelName(oeClient));
		String comment = "Model created for testing the Java OE Client API";

		/* Be careful with this one. If you have built APITest model manually, this test will create another with the same name
		but different URI. If you are pointing to a task in properties file, it'll create a weird model name.
		 */
		Model model = new Model(oeClient.getModelUri(), modelLabel, comment);
		model.setDefaultNamespace("http://example.com/APITest#");
		oeClient.createModel(model);
	}
}
