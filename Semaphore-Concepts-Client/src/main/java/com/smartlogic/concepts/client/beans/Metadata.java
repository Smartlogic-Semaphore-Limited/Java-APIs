package com.smartlogic.concepts.client.beans;

public class Metadata {

    private String propertyUri;
    private String value;
    private String lang;

    public String getRangeUri() {
        return rangeUri;
    }

    public void setRangeUri(String rangeUri) {
        this.rangeUri = rangeUri;
    }

    private String rangeUri;

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

    @Override
    public String toString() {
        return "Metadata{" +
                "propertyUri='" + propertyUri + '\'' +
                ", value='" + value + '\'' +
                ", lang='" + lang + '\'' +
                ", rangeUri='" + rangeUri + '\'' +
                '}';
    }
}
