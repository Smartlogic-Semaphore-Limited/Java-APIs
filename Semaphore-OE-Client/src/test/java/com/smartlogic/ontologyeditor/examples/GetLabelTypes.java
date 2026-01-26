package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.RelationshipType;

import java.io.IOException;
import java.util.Collection;

public class GetLabelTypes extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetLabelTypes());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Collection<RelationshipType> labelRelationshipTypes = oeClient.getLabelTypes();
		for (RelationshipType relationshipType: labelRelationshipTypes) {
			System.err.println(relationshipType);
		}
		System.err.println(String.format("%d label relationship types returned", labelRelationshipTypes.size()));

	}

}
