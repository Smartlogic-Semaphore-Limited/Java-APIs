package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class ConceptScheme implements ObjectWithId {
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

    private Label[] labels;

    private Metadata[] metadata;

    private boolean excludedByFilters;

    private RelatedConcept[] topConcepts;

    public Label[] getLabels() {
        return labels;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    public Metadata[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata[] metadata) {
        this.metadata = metadata;
    }

    public boolean isExcludedByFilters() {
        return excludedByFilters;
    }

    public void setExcludedByFilters(boolean excludedByFilters) {
        this.excludedByFilters = excludedByFilters;
    }

    public RelatedConcept[] getTopConcepts() {
        return topConcepts;
    }

    public void setTopConcepts(RelatedConcept[] topConcepts) {
        this.topConcepts = topConcepts;
    }

    public Details get_details() {
        return _details;
    }

    public void set_details(Details _details) {
        this._details = _details;
    }

    private Details _details;

    @Override
    public String toString() {
        return "ConceptScheme{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", metadata=" + Arrays.toString(metadata) +
                ", excludedByFilters=" + excludedByFilters +
                ", topConcepts=" + Arrays.toString(topConcepts) +
                ", _details=" + _details +
                '}';
    }
}
