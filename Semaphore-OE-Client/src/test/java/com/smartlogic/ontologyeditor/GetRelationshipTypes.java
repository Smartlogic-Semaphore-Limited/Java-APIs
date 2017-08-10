package com.smartlogic.ontologyeditor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.beans.ConceptClass;
import com.smartlogic.ontologyeditor.beans.RelationshipType;

public class GetRelationshipTypes extends ModelManipulation {
	protected final static Logger logger = LoggerFactory.getLogger(ConceptClass.class);

	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Collection<RelationshipType> hierarchicalRelationshipTypes = oeClient.getHierarchicalRelationshipTypes();
		for (RelationshipType relationshipType: hierarchicalRelationshipTypes) {
			System.out.println(relationshipType);
		}
		System.out.println(String.format("%d hierarchical relationship types returned", hierarchicalRelationshipTypes.size()));

		Collection<RelationshipType> associativeRelationshipTypes = oeClient.getAssociativeRelationshipTypes();
		for (RelationshipType relationshipType: associativeRelationshipTypes) {
			System.out.println(relationshipType);
		}
		System.out.println(String.format("%d associative relationship types returned", associativeRelationshipTypes.size()));
	}

}
