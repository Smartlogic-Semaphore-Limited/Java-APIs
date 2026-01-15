package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class Model {
    private String name;
    private String version;
    private String[] languages;
    private String description;
    private String color;

    private String[] tags;

    private long publishTimestamp;

    private String publishTime;

    private EmbeddingInfo embeddingInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public long getPublishTimestamp() {
        return publishTimestamp;
    }

    public void setPublishTimestamp(long publishTimestamp) {
        this.publishTimestamp = publishTimestamp;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public EmbeddingInfo getEmbeddingInfo() {
        return embeddingInfo;
    }

    public void setEmbeddingInfo(EmbeddingInfo embeddingInfo) {
        this.embeddingInfo = embeddingInfo;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", languages=" + Arrays.toString(languages) +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", publishTimestamp=" + publishTimestamp +
                ", publishTime='" + publishTime + '\'' +
                ", embeddingInfo=" + embeddingInfo +
                '}';
    }
}
