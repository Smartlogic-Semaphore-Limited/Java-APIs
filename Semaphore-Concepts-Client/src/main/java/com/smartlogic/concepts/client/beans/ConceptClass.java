package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class ConceptClass {

    private String uri;
    private Label[] labels;

    private String[] subClassOfUris;

    private String[] subClassUris;

    private String color;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Label[] getLabels() {
        return labels;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    public String[] getSubClassOfUris() {
        return subClassOfUris;
    }

    public void setSubClassOfUris(String[] subClassOfUris) {
        this.subClassOfUris = subClassOfUris;
    }

    public String[] getSubClassUris() {
        return subClassUris;
    }

    public void setSubClassUris(String[] subClassUris) {
        this.subClassUris = subClassUris;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ConceptClass{" +
                "uri='" + uri + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", subClassOfUris=" + Arrays.toString(subClassOfUris) +
                ", subClassUris=" + Arrays.toString(subClassUris) +
                ", color='" + color + '\'' +
                '}';
    }
}
