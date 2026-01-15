package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class RelatedConcept {

    private String propertyUri;

    private String objectId;

    private boolean objectExcludedByFilters;

    public String getPropertyUri() {
        return propertyUri;
    }

    public void setPropertyUri(String propertyUri) {
        this.propertyUri = propertyUri;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isObjectExcludedByFilters() {
        return objectExcludedByFilters;
    }

    public void setObjectExcludedByFilters(boolean objectExcludedByFilters) {
        this.objectExcludedByFilters = objectExcludedByFilters;
    }

    public RelationshipMetadataGroup[] getRelationshipMetadataGroups() {
        return relationshipMetadataGroups;
    }

    public void setRelationshipMetadataGroups(RelationshipMetadataGroup[] relationshipMetadataGroups) {
        this.relationshipMetadataGroups = relationshipMetadataGroups;
    }

    private RelationshipMetadataGroup[] relationshipMetadataGroups;

    @Override
    public String toString() {
        return "RelatedConcept{" +
                "propertyUri='" + propertyUri + '\'' +
                ", objectId='" + objectId + '\'' +
                ", objectExcludedByFilters=" + objectExcludedByFilters +
                ", relationshipMetadataGroups=" + Arrays.toString(relationshipMetadataGroups) +
                '}';
    }
}
