package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadPublisherConfiguration extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new DownloadPublisherConfiguration());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		byte[] publisherConfiguration = oeClient.downloadPublisherConfiguration();

		File outputFile = new File("C:/temp/publisherConfiguration.zip");
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(publisherConfiguration);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
