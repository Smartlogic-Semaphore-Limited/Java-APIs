// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.util.Date;

public class SESFilter {

  public final static int UNDEFINED_INT = -1;

  private Date modifiedAfterDate;
  private Date modifiedBeforeDate;
  private String[] filterIncludeAttributes;
  private String[] filterExcludeAttributes;
  private String[] startTermZthesIds;
  private String[] facets;
  private String[] classes;
  private int minDocs = UNDEFINED_INT;
  private int maxResultCount = UNDEFINED_INT;

  public String[] getStartTermZthesIds() {
    return startTermZthesIds;
  }

  public void setStartTermZthesIds(String[] startTermZthesIds) {
    this.startTermZthesIds = startTermZthesIds;
  }

  public String[] getFacets() {
    return facets;
  }

  public void setFacets(String[] facets) {
    this.facets = facets;
  }

  public String[] getClasses() {
    return classes;
  }

  public void setClasses(String[] classes) {
    this.classes = classes;
  }

  public int getMinDocs() {
    return minDocs;
  }

  public void setMinDocs(int minDocs) {
    this.minDocs = minDocs;
  }

  public Date getModifiedAfterDate() {
    return modifiedAfterDate;
  }

  public void setModifiedAfterDate(Date modifiedAfterDate) {
    this.modifiedAfterDate = modifiedAfterDate;
  }

  public Date getModifiedBeforeDate() {
    return modifiedBeforeDate;
  }

  public void setModifiedBeforeDate(Date modifiedBeforeDate) {
    this.modifiedBeforeDate = modifiedBeforeDate;
  }

  public String[] getIncludeAttributes() {
    return filterIncludeAttributes;
  }

  public void setIncludeAttributes(String[] attributeNames) {
    filterIncludeAttributes = attributeNames;
  }

  public String[] getExcludeAttributes() {
    return filterExcludeAttributes;
  }

  public void setExcludeAttributes(String[] attributeNames) {
    filterExcludeAttributes = attributeNames;
  }

  public int getMaxResultCount() {
    return maxResultCount;
  }

  public void setMaxResultCount(int maxResultCount) {
    this.maxResultCount = maxResultCount;
  }

}
