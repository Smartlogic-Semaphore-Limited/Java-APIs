package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class RelationshipType {

    private String uri;

    private Label[] labels;

    private String domainUris[];

    private String rangeUris[];

    public String[] getSubPropertyOfUris() {
        return subPropertyOfUris;
    }

    public void setSubPropertyOfUris(String[] subPropertyOfUris) {
        this.subPropertyOfUris = subPropertyOfUris;
    }

    private String subPropertyOfUris[];

    private String subPropertyUris[];

    private boolean symmetrical;

    private String inversePropertyUri;

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

    public String[] getSubPropertyUris() {
        return subPropertyUris;
    }

    public void setSubPropertyUris(String[] subPropertyUris) {
        this.subPropertyUris = subPropertyUris;
    }

    public boolean isSymmetrical() {
        return symmetrical;
    }

    public void setSymmetrical(boolean symmetric) {
        this.symmetrical = symmetrical;
    }

    public String getInversePropertyUri() {
        return inversePropertyUri;
    }

    public void setInversePropertyUri(String inversePropertyUri) {
        this.inversePropertyUri = inversePropertyUri;
    }

    @Override
    public String toString() {
        return "RelationshipType{" +
                "uri='" + uri + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", domainUris=" + Arrays.toString(domainUris) +
                ", rangeUris=" + Arrays.toString(rangeUris) +
                ", subPropertyOfUris=" + Arrays.toString(subPropertyOfUris) +
                ", subPropertyUris=" + Arrays.toString(subPropertyUris) +
                ", symmetrical=" + symmetrical +
                ", inversePropertyUri='" + inversePropertyUri + '\'' +
                '}';
    }
}
