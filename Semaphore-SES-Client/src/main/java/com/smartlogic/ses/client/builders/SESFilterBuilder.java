package com.smartlogic.ses.client.builders;

import java.util.Date;
import java.util.Optional;

import com.smartlogic.ses.client.SESFilter;

public class SESFilterBuilder {

  private Optional<Date> modifiedAfterDate = Optional.empty();
  private Optional<Date> modifiedBeforeDate = Optional.empty();
  private Optional<String[]> filterIncludeAttributes = Optional.empty();
  private Optional<String[]> filterExcludeAttributes = Optional.empty();
  private Optional<String[]> startTermZthesIds = Optional.empty();
  private Optional<String[]> facets = Optional.empty();
  private Optional<String[]> classes = Optional.empty();
  private Optional<Integer> minDocs = Optional.empty();
  private Optional<Integer> maxResultCount = Optional.empty();

  public SESFilterBuilder modifiedAfterDate(Date modifiedAfterDate) {
    this.modifiedAfterDate = Optional.of(modifiedAfterDate);
    return this;
  }

  public SESFilterBuilder modifiedBeforeDate(Date modifiedBeforeDate) {
    this.modifiedBeforeDate = Optional.of(modifiedBeforeDate);
    return this;
  }

  public SESFilterBuilder filterIncludeAttributes(String[] filterIncludeAttributes) {
    this.filterIncludeAttributes = Optional.of(filterIncludeAttributes);
    return this;
  }

  public SESFilterBuilder filterExcludeAttributes(String[] filterExcludeAttributes) {
    this.filterExcludeAttributes = Optional.of(filterExcludeAttributes);
    return this;
  }

  public SESFilterBuilder startTermZthesIds(String[] startTermZthesIds) {
    this.startTermZthesIds = Optional.of(startTermZthesIds);
    return this;
  }

  public SESFilterBuilder facets(String[] facets) {
    this.facets = Optional.of(facets);
    return this;
  }

  public SESFilterBuilder classes(String[] classes) {
    this.classes = Optional.of(classes);
    return this;
  }

  public SESFilterBuilder minDocs(Integer minDocs) {
    this.minDocs = Optional.of(minDocs);
    return this;
  }

  public SESFilterBuilder maxResultCount(Integer maxResultCount) {
    this.maxResultCount = Optional.of(maxResultCount);
    return this;
  }

  public SESFilter build() {
    SESFilter sesFilter = new SESFilter();
    modifiedAfterDate.ifPresent(sesFilter::setModifiedAfterDate);
    modifiedBeforeDate.ifPresent(sesFilter::setModifiedBeforeDate);
    filterIncludeAttributes.ifPresent(sesFilter::setIncludeAttributes);
    filterExcludeAttributes.ifPresent(sesFilter::setExcludeAttributes);
    startTermZthesIds.ifPresent(sesFilter::setStartTermZthesIds);
    facets.ifPresent(sesFilter::setFacets);
    classes.ifPresent(sesFilter::setClasses);
    minDocs.ifPresent(sesFilter::setMinDocs);
    maxResultCount.ifPresent(sesFilter::setMaxResultCount);
    return sesFilter;
  }

}
