package com.smartlogic.concepts.client.beans;

public class Details {

    public int getResultIndex() {
        return resultIndex;
    }

    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    private int resultIndex;

    public int getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(int totalResultCount) {
        this.totalResultCount = totalResultCount;
    }

    private int totalResultCount;

    @Override
    public String toString() {
        return "Details{" +
                "resultIndex=" + resultIndex +
                ", totalResultCount=" + totalResultCount +
                '}';
    }
}
