package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class GetEnvironments extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetEnvironments());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Map<String, Environment> environments = oeClient.getEnvironments();

		for (String envName : environments.keySet()) {
			Environment env = environments.get(envName);
			System.out.println("Environment name: " + env.getName() + " URI: " + env.getUri());
		}
	}
}
