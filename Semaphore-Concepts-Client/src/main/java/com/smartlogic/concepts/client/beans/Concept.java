package com.smartlogic.concepts.client.beans;

import java.nio.file.Path;
import java.util.Arrays;

public class Concept implements ObjectWithId {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    private String uri;

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String[] getTypeUris() {
        return typeUris;
    }

    public void setTypeUris(String[] typeUris) {
        this.typeUris = typeUris;
    }

    private String[] typeUris;

    public Label[] getPrefLabels() {
        return prefLabels;
    }

    public void setPrefLabels(Label[] prefLabels) {
        this.prefLabels = prefLabels;
    }

    public Label[] getAltLabels() {
        return altLabels;
    }

    public void setAltLabels(Label[] altLabels) {
        this.altLabels = altLabels;
    }

    private Label[] prefLabels;

    public Metadata[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata[] metadata) {
        this.metadata = metadata;
    }

    public RelatedConcept[] getBroaderConcepts() {
        return broaderConcepts;
    }

    public void setBroaderConcepts(RelatedConcept[] broaderConcepts) {
        this.broaderConcepts = broaderConcepts;
    }

    public RelatedConcept[] getNarrowerConcepts() {
        return narrowerConcepts;
    }

    public void setNarrowerConcepts(RelatedConcept[] narrowerConcepts) {
        this.narrowerConcepts = narrowerConcepts;
    }

    public RelatedConcept[] getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(RelatedConcept[] relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }


    public String getTextForEmbedding() {
        return textForEmbedding;
    }

    public void setTextForEmbedding(String textForEmbedding) {
        this.textForEmbedding = textForEmbedding;
    }

    public CollectionMembership[] getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(CollectionMembership[] memberOf) {
        this.memberOf = memberOf;
    }

    private Label[] altLabels;

    private Metadata[] metadata;

    private RelatedConcept[] broaderConcepts;

    private RelatedConcept[] narrowerConcepts;

    public RelatedConceptScheme[] getTopConceptOf() {
        return topConceptOf;
    }

    public void setTopConceptOf(RelatedConceptScheme[] topConceptOf) {
        this.topConceptOf = topConceptOf;
    }

    private RelatedConcept[] relatedConcepts;

    public PathElement[][] getPaths() {
        return paths;
    }

    public void setPaths(PathElement[][] paths) {
        this.paths = paths;
    }

    private PathElement[][] paths;

    private String textForEmbedding;

    private CollectionMembership[] memberOf;

    private RelatedConceptScheme[] topConceptOf;


    public Details get_details() {
        return _details;
    }

    public void set_details(Details _details) {
        this._details = _details;
    }

    private Details _details;

    @Override
    public String toString() {
        return "Concept{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                ", typeUris=" + Arrays.toString(typeUris) +
                ", prefLabels=" + Arrays.toString(prefLabels) +
                ", altLabels=" + Arrays.toString(altLabels) +
                ", metadata=" + Arrays.toString(metadata) +
                ", broaderConcepts=" + Arrays.toString(broaderConcepts) +
                ", narrowerConcepts=" + Arrays.toString(narrowerConcepts) +
                ", relatedConcepts=" + Arrays.toString(relatedConcepts) +
                ", paths=" + Arrays.toString(paths) +
                ", textForEmbedding='" + textForEmbedding + '\'' +
                ", memberOf=" + Arrays.toString(memberOf) +
                ", topConceptOf=" + Arrays.toString(topConceptOf) +
                ", _details=" + _details +
                '}';
    }
}
