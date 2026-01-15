package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class MetadataType {

    private String uri;

    private Label[] labels;

    private String[] domainUris;

    private String[] rangeUris;

    private String[] subPropertyOfUris;

    public Label[] getLabels() {
        return labels;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    public String[] getDomainUris() {
        return domainUris;
    }

    public void setDomainUris(String[] domainUris) {
        this.domainUris = domainUris;
    }

    public String[] getRangeUris() {
        return rangeUris;
    }

    public void setRangeUris(String[] rangeUris) {
        this.rangeUris = rangeUris;
    }

    public String[] getSubPropertyOfUris() {
        return subPropertyOfUris;
    }

    public void setSubPropertyOfUris(String[] subPropertyOfUris) {
        this.subPropertyOfUris = subPropertyOfUris;
    }

    public String[] getSubPropertyUris() {
        return subPropertyUris;
    }

    public void setSubPropertyUris(String[] subPropertyUris) {
        this.subPropertyUris = subPropertyUris;
    }

    private String[] subPropertyUris;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "MetadataType{" +
                "uri='" + uri + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", domainUris=" + Arrays.toString(domainUris) +
                ", rangeUris=" + Arrays.toString(rangeUris) +
                ", subPropertyOfUris=" + Arrays.toString(subPropertyOfUris) +
                ", subPropertyUris=" + Arrays.toString(subPropertyUris) +
                '}';
    }
}
