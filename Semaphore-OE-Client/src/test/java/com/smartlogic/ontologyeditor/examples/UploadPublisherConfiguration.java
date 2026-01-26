package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;

import java.io.*;

public class UploadPublisherConfiguration extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new UploadPublisherConfiguration());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		File inputFile = new File("C:\\temp\\publisherConfiguration\\Upload.zip");
		byte[] fileData = new byte[(int) inputFile.length()];
		try (FileInputStream fis = new FileInputStream(inputFile)) {
			fis.read(fileData);

		} catch (IOException e) {
			throw new RuntimeException(e);
        }

		oeClient.uploadPublisherConfiguration(fileData);

	}
}
