package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;

public class AddRelationshipType extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddRelationshipType());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		oeClient.createRelationshipType(new Label("en", "Forward Label"), "http://example.com/APITest#SForward",
				new Label("en", "Reverse Label"), "http://example.com/APITest#SReverse");

	}

}
