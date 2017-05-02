//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.util.Date;

public class SESFilter {
	public final  static int UNDEFINED_INT = -1;

	private String[] startTermZthesIds;
	public String[] getStartTermZthesIds() {
		return startTermZthesIds;
	}
	public void setStartTermZthesIds(String[] startTermZthesIds) {
		this.startTermZthesIds = startTermZthesIds;
	}

	private String[] facets;
	public String[] getFacets() {
		return facets;
	}
	public void setFacets(String[] facets) {
		this.facets = facets;
	}

	private String[] classes;
	public String[] getClasses() {
		return classes;
	}
	public void setClasses(String[] classes) {
		this.classes = classes;
	}

	private int minDocs = UNDEFINED_INT;
	public int getMinDocs() {
		return minDocs;
	}
	public void setMinDocs(int minDocs) {
		this.minDocs = minDocs;
	}

	private int prefixResultsLimit = UNDEFINED_INT;
	public int getPrefixResultsLimit() {
		return prefixResultsLimit;
	}
	public void setPrefixResultsLimit(int prefixResultsLimit) {
		this.prefixResultsLimit = prefixResultsLimit;
	}
	private int prefixResultsInternalLimit = UNDEFINED_INT;
	public int getPrefixResultsInternalLimit() {
		return prefixResultsInternalLimit;
	}
	public void setPrefixResultsInternalLimit(int prefixResultsInternalLimit) {
		this.prefixResultsInternalLimit = prefixResultsInternalLimit;
	}

	private Date modifiedAfterDate;
	public Date getModifiedAfterDate() {
		return modifiedAfterDate;
	}
	public void setModifiedAfterDate(Date modifiedAfterDate) {
		this.modifiedAfterDate = modifiedAfterDate;
	}

	private Date modifiedBeforeDate;
	public Date getModifiedBeforeDate() {
		return modifiedBeforeDate;
	}
	public void setModifiedBeforeDate(Date modifiedBeforeDate) {
		this.modifiedBeforeDate = modifiedBeforeDate;
	}

	private String[] filterIncludeAttributes;
	public String[] getIncludeAttributes() {
		return filterIncludeAttributes;
	}
	public void setIncludeAttributes(String[] attributeNames) {
		filterIncludeAttributes = attributeNames;
	}

	private String[] filterExcludeAttributes;
	public String[] getExcludeAttributes() {
		return filterExcludeAttributes;
	}
	public void setExcludeAttributes(String[] attributeNames) {
		filterExcludeAttributes = attributeNames;
	}



}
