package com.smartlogic.concepts.client.beans;

import java.nio.file.Path;
import java.util.Arrays;

public class Label {

    private String uri;
    private String propertyUri;

    private String value;

    private String lang;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPropertyUri() {
        return propertyUri;
    }

    public void setPropertyUri(String propertyUri) {
        this.propertyUri = propertyUri;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Metadata[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata[] metadata) {
        this.metadata = metadata;
    }

    public Settings[] getSettings() {
        return settings;
    }

    public void setSettings(Settings[] settings) {
        this.settings = settings;
    }

    private Metadata[] metadata;

    private Settings[] settings;

    @Override
    public String toString() {
        return "Label{" +
                "uri='" + uri + '\'' +
                ", propertyUri='" + propertyUri + '\'' +
                ", value='" + value + '\'' +
                ", lang='" + lang + '\'' +
                ", metadata=" + Arrays.toString(metadata) +
                ", settings=" + Arrays.toString(settings) +
                '}';
    }
}
