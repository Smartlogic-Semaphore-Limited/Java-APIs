package com.smartlogic.concepts.client.beans;

public class CollectionMembership {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    @Override
    public String toString() {
        return "CollectionMembership{" +
                "id='" + id + '\'' +
                '}';
    }
}
