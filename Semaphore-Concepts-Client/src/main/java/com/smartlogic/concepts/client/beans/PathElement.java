package com.smartlogic.concepts.client.beans;

public class PathElement {

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    private String subjectId;

    private String propertyUri;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPropertyUri() {
        return propertyUri;
    }

    public void setPropertyUri(String propertyUri) {
        this.propertyUri = propertyUri;
    }

    @Override
    public String toString() {
        return "PathElement{" +
                "subjectId='" + subjectId + '\'' +
                ", propertyUri='" + propertyUri + '\'' +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
