package com.smartlogic.concepts.client.beans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Structure {

    public ConceptClass[] getConceptClasses() {
        return conceptClasses;
    }

    public void setConceptClasses(ConceptClass[] conceptClasses) {
        this.conceptClasses = conceptClasses;
    }

    private ConceptClass[] conceptClasses;

    public RelationshipType[] getConceptSchemeRelationships() {
        return conceptSchemeRelationships;
    }
    private final Map<String, RelationshipType> relationshipTypeMap = new HashMap<String, RelationshipType>();
    private void addRelationshipTypesToMap(RelationshipType[] conceptSchemeRelationships) {
        for (RelationshipType relationshipType: conceptSchemeRelationships) {
            relationshipTypeMap.put(relationshipType.getUri(), relationshipType);
        }
    }

    public RelationshipType getRelationshipTypeByUri(String uri) {
        return relationshipTypeMap.get(uri);
    }


    public void setConceptSchemeRelationships(RelationshipType[] conceptSchemeRelationships) {
        this.conceptSchemeRelationships = conceptSchemeRelationships;
        addRelationshipTypesToMap(conceptSchemeRelationships);
    }


    private RelationshipType[] conceptSchemeRelationships;

    public ConceptRelationships getConceptRelationships() {
        return conceptRelationships;
    }

    public void setConceptRelationships(ConceptRelationships conceptRelationships) {
        this.conceptRelationships = conceptRelationships;
        addRelationshipTypesToMap(conceptRelationships.getAssociative());
        addRelationshipTypesToMap(conceptRelationships.getBroader());
        addRelationshipTypesToMap(conceptRelationships.getNarrower());
    }

    private ConceptRelationships conceptRelationships;

    public RelationshipType[] getLabelRelationships() {
        return labelRelationships;
    }

    public void setLabelRelationships(RelationshipType[] labelRelationships) {
        this.labelRelationships = labelRelationships;
        addRelationshipTypesToMap(labelRelationships);
    }

    private RelationshipType[] labelRelationships;

    private MetadataType[] metadataTypes;
    private final Map<String, MetadataType> metadataTypeMap = new HashMap<String, MetadataType>();
    private void addMetadataTypesToMap(MetadataType[] metadataTypes) {
        for (MetadataType metadataType: metadataTypes) {
            metadataTypeMap.put(metadataType.getUri(), metadataType);
        }
    }
    public MetadataType getMetadataTypeByUri(String uri) {
        return metadataTypeMap.get(uri);
    }

    public MetadataType[] getMetadataTypes() {
        return metadataTypes;
    }

    public void setMetadataTypes(MetadataType[] metadataTypes) {
        this.metadataTypes = metadataTypes;
    }

    @Override
    public String toString() {
        return "Structure{" +
                "conceptClasses=" + Arrays.toString(conceptClasses) +
                ", conceptSchemeRelationships=" + Arrays.toString(conceptSchemeRelationships) +
                ", conceptRelationships=" + conceptRelationships +
                ", labelRelationships=" + Arrays.toString(labelRelationships) +
                ", metadataTypes=" + Arrays.toString(metadataTypes) +
                '}';
    }
}
