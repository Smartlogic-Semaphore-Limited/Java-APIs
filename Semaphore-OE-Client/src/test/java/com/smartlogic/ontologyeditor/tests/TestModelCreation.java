package com.smartlogic.ontologyeditor.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;

public class TestModelCreation extends AbstractTest {
	private OEClientReadWrite oeClient;

	public void beforeTest() throws IOException {
		oeClient = getOEClient(false);
	}

	public void afterTest() {
	}

	public void cycleModel() throws OEClientException {
		String modelName = "API Test: " + (new Date()).toString();
		Model model = new Model(null, new Label("", modelName), "A model created for the purposes of testing the Java API");

		Collection<Model> beforeModels = oeClient.getAllModels();
		assertFalse(beforeModels.contains(model));
		
		oeClient.createModel(model);
		
		Collection<Model> afterCreateModels = oeClient.getAllModels();
		assertTrue(afterCreateModels.size() == beforeModels.size() + 1);
		assertTrue(afterCreateModels.contains(model));
		
		oeClient.deleteModel(model);
		
		Collection<Model> afterDeleteModels = oeClient.getAllModels();
		assertTrue(afterDeleteModels.size() == beforeModels.size());
		assertFalse(afterDeleteModels.contains(model));
	}

}
