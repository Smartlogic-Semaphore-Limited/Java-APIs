package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.RelationshipType;

public class GetRelationshipTypes extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetRelationshipTypes());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Collection<RelationshipType> hierarchicalRelationshipTypes = oeClient.getHierarchicalRelationshipTypes();
		for (RelationshipType relationshipType: hierarchicalRelationshipTypes) {
			System.err.println(relationshipType);
		}
		System.err.println(String.format("%d hierarchical relationship types returned", hierarchicalRelationshipTypes.size()));

		Collection<RelationshipType> associativeRelationshipTypes = oeClient.getAssociativeRelationshipTypes();
		for (RelationshipType relationshipType: associativeRelationshipTypes) {
			System.err.println(relationshipType);
		}
		System.err.println(String.format("%d associative relationship types returned", associativeRelationshipTypes.size()));
	}

}
