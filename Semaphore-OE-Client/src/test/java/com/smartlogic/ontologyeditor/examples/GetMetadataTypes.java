package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.MetadataType;
import com.smartlogic.ontologyeditor.beans.RelationshipType;

import java.io.IOException;
import java.util.Collection;

public class GetMetadataTypes extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetMetadataTypes());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Collection<MetadataType> metadataTypes = oeClient.getMetadataTypes();
		for (MetadataType metadataType: metadataTypes) {
			System.err.println(metadataType);
		}
		System.err.println(String.format("%d metadata types returned", metadataTypes.size()));

	}

}
