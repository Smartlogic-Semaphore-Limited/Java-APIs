// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;

/**
 * Represents a linguistic system associated with a Semaphore model.
 */
public class ModelLanguage {

    private String uri;
    private Label title;
    private String notation;

    /**
     * Parse a model language from a JSON-LD object.
     *
     * @param jsonObject the JSON-LD object describing the language
     */
    public ModelLanguage(JsonObject jsonObject) {
        this.uri = jsonObject.get("@id").getAsString().value();
        JsonObject title = jsonObject.get("dc:title").getAsArray().get(0).getAsObject();
        this.title = new Label(title.get("@language").getAsString().value(), title.get("@value").getAsString().value());
        this.notation = jsonObject.get("skos:notation").getAsArray().get(0).getAsObject().get("@value").getAsString().value();
    }

    /**
     * Create a model language with explicit values.
     *
     * @param uri the language URI
     * @param title the display title label
     * @param notation the language notation code
     */
    public ModelLanguage(String uri, Label title, String notation) {
        this.uri = uri;
        this.title = title;
        this.notation = notation;
    }

    /**
     * Get the URI of the language.
     *
     * @return the language URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Get the display title label of the language.
     *
     * @return the title label
     */
    public Label getTitle() {
        return title;
    }

    /**
     * Get the language notation code.
     *
     * @return the notation code, such as {@code en} or {@code fr}
     */
    public String getNotation() {
        return notation;
    }
}
