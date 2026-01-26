package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddMetadataType extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddMetadataType());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		oeClient.createMetadataTypeString(new Label("en", "String"), "http://example.com/APITest#String");
		oeClient.createMetadataTypeInteger(new Label("en", "Integer"), "http://example.com/APITest#Integer");
		oeClient.createMetadataTypeDecimal(new Label("en", "Decimal"), "http://example.com/APITest#Decimal");
		oeClient.createMetadataTypeDate(new Label("en", "Date"), "http://example.com/APITest#Date");
		oeClient.createMetadataTypeAnyURI(new Label("en", "AnyURI"), "http://example.com/APITest#AnyURI");
		oeClient.createMetadataTypeBoolean(new Label("en", "Boolean"), "http://example.com/APITest#Boolean");

	}

}
