package com.smartlogic.concepts.client.beans;

import java.lang.reflect.Member;
import java.util.Arrays;

public class Collection  implements ObjectWithId {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    private Label[] labels;

    public Details get_details() {
        return _details;
    }

    public void set_details(Details _details) {
        this._details = _details;
    }

    public Metadata[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata[] metadata) {
        this.metadata = metadata;
    }

    public Label[] getLabels() {
        return labels;
    }

    private Details _details;

    private Metadata[] metadata;

    public RelatedConcept[] getMembers() {
        return members;
    }

    public void setMembers(RelatedConcept[] members) {
        this.members = members;
    }

    private RelatedConcept[] members;

    @Override
    public String toString() {
        return "Collection{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", _details=" + _details +
                ", metadata=" + Arrays.toString(metadata) +
                ", members=" + Arrays.toString(members) +
                '}';
    }
}
