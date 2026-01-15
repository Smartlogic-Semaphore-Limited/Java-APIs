package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class ConceptRelationships {


    private RelationshipType[] narrower;

    private RelationshipType[] broader;

    public RelationshipType[] getBroader() {
        return broader;
    }

    public void setBroader(RelationshipType[] broader) {
        this.broader = broader;
    }

    public RelationshipType[] getAssociative() {
        return associative;
    }

    public void setAssociative(RelationshipType[] associative) {
        this.associative = associative;
    }

    private RelationshipType[] associative;

    public RelationshipType[] getNarrower() {
        return narrower;
    }

    public void setNarrower(RelationshipType[] narrower) {
        this.narrower = narrower;
    }

    @Override
    public String toString() {
        return "ConceptRelationships{" +
                "narrower=" + Arrays.toString(narrower) +
                ", broader=" + Arrays.toString(broader) +
                ", associative=" + Arrays.toString(associative) +
                '}';
    }
}
