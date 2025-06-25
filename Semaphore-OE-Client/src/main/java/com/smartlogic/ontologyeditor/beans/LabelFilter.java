package com.smartlogic.ontologyeditor.beans;

/**
 * Represents a label filter value with a boolean flag indicating
 * if the search shall be a regular expression search or a prefix auto-complete
 * search.
 * The regex grammar must match the grammar indicated in the Studio KMM instance in the
 * filter configuration dialog. Any regex special characters must be escaped with a backslash
 * in the normal way.
 */
public class LabelFilter {

  public LabelFilter(String value, String langCode, boolean isRegexSearch){
    this.value = value;
    this.langCode = langCode;
    this.isRegexSearch = isRegexSearch;
  }

  private String value;
  public void setValue(String value) {
    this.value = value;
  }
  public String getValue() {
    return value;
  }

  private String langCode;

  /**
   * Sets the language code for the search.
   * Use "l-n" for language neutral lang code. Otherwise, all languages will be searched
   * @param langCode the language code (e.g. "en", "l-n" for language neutral)
   */
  public void setLangCode(String langCode) {
    this.langCode = langCode;
  }

  /**
   * Gets the current language code for the label filter.
   * @return the language code string (e.g. "en", "l-n" for language neutral)
   */
  public String getLangCode() {
    return langCode;
  }

  private boolean isRegexSearch = false;

  /**
   * Sets whether the label filter is prefix (autocomplete) or by regular expression.
   * @param isRegexSearch set whether the filter is prefix or regex.
   */
  public void setIsRegexSearch(boolean isRegexSearch) {
    this.isRegexSearch = isRegexSearch;
  }

  /**
   * Returns whether the filter is configured for regex label search.
   * @return whether the filter is configured for regex label search.
   */
  public boolean isRegexSearch() {
    return isRegexSearch;
  }

  private String altLabelType;

  /**
   * If an alternative label search, the type of alt label to search
   * @param altLabelType the type of alt label (e.g. skosxl:altLabel)
   */
  public void setAltLabelType(String altLabelType) {
    this.altLabelType = altLabelType;
  }

  /**
   * Return the type of alt label to search for labels/
   * @return the type of alt label
   */
  public String getAltLabelType() {
    return altLabelType;
  }

  /**
   * Utility method to build the language code suffix for a search.
   * A null language does not return value, just empty string which means all label values in all languages will be searched.
   * A value of "l-n" returns just "@", used for searching only language neutral labels.
   * Otherwise, returns "@langCode".
   * @return the language code for search
   */
  public String getLangCodeForSearch() {
    String langCodeForSearch = "";
    if (getLangCode() != null) {
      langCodeForSearch = (getLangCode().equalsIgnoreCase("l-n") ? "@" : "@" + getLangCode());
    }
    return langCodeForSearch;
  }

  /**
   * When sending a regex filter, special regex characters must be escaped if not intended to be used as part of the regex.
   * The API requires an additional escaping backslash for each backslash. This method does the additional required escaping.
   * This matches the UI escape logic in the Studio KMM filter builder.
   * For example, a value of "My string with \$ and \^ in it" will be escaped to "My string with \\$ and \\^ in it"
   * when sent to KMM API for regex search.
   * @return the backlash-escaped value for regex search
   */
  public String getEscapedLeftSlashesValue() {
    if (value == null) {
      return null;
    }
    return value.replace("\\", "\\\\");
  }

  @Override
  public String toString() {
    return "value: \"" + (value != null ? value + "\"" : "<null>")  +
       getLangCodeForSearch() +
        ", is regex search? ["  + isRegexSearch + "]" +
        ", alt label type: [" + (altLabelType != null ? "'" + altLabelType + "'" : "") + "]";
  }
}
