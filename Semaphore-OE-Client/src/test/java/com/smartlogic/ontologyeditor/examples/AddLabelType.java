package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;

public class AddLabelType extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddLabelType());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		oeClient.createLabelRelationshipType(new Label("en", "Forward Label Type"), "http://example.com/APITest#SForwardLabel");

	}

}
