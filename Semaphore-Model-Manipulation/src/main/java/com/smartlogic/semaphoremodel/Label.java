package com.smartlogic.semaphoremodel;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

public class Label {

  public Label(String value, Language language) {
    this.resource = null;
    this.uri = null;
    this.value = value;
    this.language = language;
  }

  protected Label(Resource resource, String value, Language language) throws ModelException {
    try {
      this.resource = resource;
      this.uri = new URI(resource.getURI());
      this.value = value;
      this.language = language;
    } catch (URISyntaxException e) {
      throw new ModelException("Unable to generate URI from resource: '%s'", resource.toString());
    }
  }

  protected Label(Resource resource, Literal literal) throws ModelException {
    try {
      this.resource = resource;
      this.uri = new URI(resource.getURI());
      this.value = literal.getString();
      this.language = Language.getLanguage(literal.getLanguage());
    } catch (URISyntaxException e) {
      throw new ModelException("Unable to generate URI from resource: '%s'", resource.toString());
    }
  }

  protected Label(Literal literal) {
    this.resource = null;
    this.uri = null;
    this.value = literal.getString();
    this.language = Language.getLanguage(literal.getLanguage());
  }

  private Resource resource;
  private final URI uri;
  private final String value;
  private final Language language;

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public String getValue() {
    return value;
  }

  public Language getLanguage() {
    return language;
  }

  public URI getURI() {
    return uri;
  }

  @Override
  public String toString() {
    return String.format("\"%s\"%s", value, language.getCode());
  }

  public String getLanguageCode() {
    return language == null ? null : language.getCode();
  }

  public void setCharacterEscaping(boolean escaping) {
    if (escaping) {
      resource.addProperty(SEM.characterEscaping, SEM.EscapeSpecialCharacters);
    } else {
      resource.addProperty(SEM.characterEscaping, SEM.NoEscaping);
    }

  }

  public void setMatchCasing(boolean casing) {
    if (casing) {
      resource.addProperty(SEM.caseSensitivity, SEM.CaseSensitive);
    } else {
      resource.addProperty(SEM.caseSensitivity, SEM.CaseInsensitive);
    }

  }

  public void setStemming(boolean stemming) {
    if (stemming) {
      resource.addProperty(SEM.stemming, SEM.StemmingOn);
    } else {
      resource.addProperty(SEM.stemming, SEM.StemmingOff);
    }

  }

}
