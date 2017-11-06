package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;

public class AddModel extends ModelManipulation {
	public static void main(String[] args) throws OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		System.out.println("-------------------------- Before anything --------------------------");
		Collection<Model> models1 = oeClient.getAllModels();
		for (Model modelInstance: models1) {
			System.out.println(modelInstance.getLabel());
		}
		
		Model model = new Model("http://smartlogic.com/api-test#", new Label("", "API Test"), "A model created for the purposes of testing the Java API");
		oeClient.createModel(model);

		System.out.println("-------------------------- After create --------------------------");
		Collection<Model> models2 = oeClient.getAllModels();
		for (Model modelInstance: models2) {
			System.out.println(modelInstance.getLabel());
		}
		
		
		oeClient.deleteModel(model);

		System.out.println("-------------------------- After delete --------------------------");
		Collection<Model> models3 = oeClient.getAllModels();
		for (Model modelInstance: models3) {
			System.out.println(modelInstance.getLabel());
		}
		

	}
}
