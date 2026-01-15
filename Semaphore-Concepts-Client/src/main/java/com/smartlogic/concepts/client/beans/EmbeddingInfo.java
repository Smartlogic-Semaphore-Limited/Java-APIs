package com.smartlogic.concepts.client.beans;

import java.util.Arrays;

public class EmbeddingInfo {

    private String type;

    private String serverUrl;

    private String description;

    private String exampleTextForEmbeddingVerification;

    private double[] exampleTextEmbedding;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExampleTextForEmbeddingVerification() {
        return exampleTextForEmbeddingVerification;
    }

    public void setExampleTextForEmbeddingVerification(String exampleTextForEmbeddingVerification) {
        this.exampleTextForEmbeddingVerification = exampleTextForEmbeddingVerification;
    }

    public double[] getExampleTextEmbedding() {
        return exampleTextEmbedding;
    }

    public void setExampleTextEmbedding(double[] exampleTextEmbedding) {
        this.exampleTextEmbedding = exampleTextEmbedding;
    }

    public double getServiceCalibration() {
        return serviceCalibration;
    }

    public void setServiceCalibration(double serviceCalibration) {
        this.serviceCalibration = serviceCalibration;
    }

    private double serviceCalibration;

    @Override
    public String toString() {
        return "EmbeddingInfo{" +
                "type='" + type + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", description='" + description + '\'' +
                ", exampleTextForEmbeddingVerification='" + exampleTextForEmbeddingVerification + '\'' +
                ", exampleTextEmbedding=" + Arrays.toString(exampleTextEmbedding) +
                ", serviceCalibration=" + serviceCalibration +
                '}';
    }
}
