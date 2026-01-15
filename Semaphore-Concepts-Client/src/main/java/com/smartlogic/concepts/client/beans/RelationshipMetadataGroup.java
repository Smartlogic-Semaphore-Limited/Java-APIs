package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class RelationshipMetadataGroup {

    private String uri;
    private Label label;
    private int groupIndex;

    private Metadata[] metadata;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public Metadata[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata[] metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "RelationshipMetadataGroup{" +
                "uri='" + uri + '\'' +
                ", label=" + label +
                ", groupIndex=" + groupIndex +
                ", metadata=" + Arrays.toString(metadata) +
                '}';
    }
}
