package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;

public class ModelLanguage {

    private  String uri;
    private  Label title;
    private  String notation;


    public ModelLanguage(JsonObject jsonObject) {
        this.uri = jsonObject.get("@id").getAsString().value();
        JsonObject title = jsonObject.get("dc:title").getAsArray().get(0).getAsObject();
        this.title = new Label(title.get("@language").getAsString().value(), title.get("@value").getAsString().value());
        this.notation = jsonObject.get("skos:notation").getAsArray().get(0).getAsObject().get("@value").getAsString().value();
    }

    public ModelLanguage(String uri, Label title, String notation) {
        this.uri = uri;
        this.title = title;
        this.notation = notation;
    }

    public String getUri() {
        return uri;
    }

    public Label getTitle() {
        return title;
    }

    public String getNotation() {
        return notation;
    }
}
