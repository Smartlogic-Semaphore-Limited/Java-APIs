package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.LabelFilter;

public class OEFilter {

	private String conceptClass;

	/**
	 * Gets the concept class filter. Example: skos:Concept
	 * @return the concept class filter string
	 */
	public String getConceptClass() {
		return conceptClass;
	}

	/**
	 * Sets the concept class filter string. For example: skos:Concept
	 * @param conceptClass the concept class filter string
	 */
	public void setConceptClass(String conceptClass) {
		this.conceptClass = conceptClass;
	}

	private LabelFilter anyLabelFilter;

	/**
	 * Gets the current any label filter object.
	 * @return the LabelFilter for any label search
	 */
	public LabelFilter getAnyLabelFilter() {
		return anyLabelFilter;
	}

	/**
	 * Sets the current any label filter.
	 * @param anyLabelFilter the label filter object for searching for any labels.
	 */
	public void setAnyLabelFilter(LabelFilter anyLabelFilter) {
		this.anyLabelFilter = anyLabelFilter;
	}

	private LabelFilter prefLabelFilter;

	/**
	 * Gets the current preferred label filter object.
	 * @return the LabelFilter for preferred label search
	 */
	public LabelFilter getPrefLabelFilter() {
		return prefLabelFilter;
	}

	/**
	 * Sets the preferred label filter.
	 * @param prefLabelFilter the label filter object for searching for preferred labels.
	 */
	public void setPrefLabelFilter(LabelFilter prefLabelFilter) {
		this.prefLabelFilter = prefLabelFilter;
	}

	private LabelFilter altLabelFilter;

	/**
	 * Gets the alternative label filter.
	 * @return the LabelFilter for alternative label search.
	 */
	public LabelFilter getAltLabelFilter() {
		return altLabelFilter;
	}

	public void setAltLabelFilter(LabelFilter altLabelFilter) {
		this.altLabelFilter = altLabelFilter;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "\n[" +
                "conceptClass = " + conceptClass  +
                "],\n" +
                "[any label = " + (anyLabelFilter == null ? "null" : anyLabelFilter.toString()) + "],\n" +
                "[prefLabel = " + (prefLabelFilter == null ? "null" : prefLabelFilter.toString()) + "],\n" +
                "[altLabel  = " + (altLabelFilter == null ? "null" : altLabelFilter.toString()) + "]";
	}
}
