package com.smartlogic.ses.client;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlTransient;

public abstract class AbstractSimpleNodeDate extends AbstractSimpleNode {
  private static final long serialVersionUID = -7383419011106091654L;

  public AbstractSimpleNodeDate() {
  }

  public AbstractSimpleNodeDate(String value) {
    super(value);
  }

  public AbstractSimpleNodeDate(Element element) {
    super(element);
  }

  private final static DateTimeFormatter defaultDateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  @XmlTransient
  public ZonedDateTime getZonedDateTime() {
    if ((getValue() == null) || (getValue().length() == 0))
      return null;

    return ZonedDateTime.parse(getValue(), defaultDateTimeFormatter);
  }

  public void setZonedDateTime(ZonedDateTime zonedDateTime) {
    setValue(defaultDateTimeFormatter.format(zonedDateTime));
  }

  public ZonedDateTime getZonedDateTime(DateTimeFormatter dateTimeFormatter) {
    if ((getValue() == null) || (getValue().length() == 0))
      return null;

    return ZonedDateTime.parse(getValue(), dateTimeFormatter);
  }

}
