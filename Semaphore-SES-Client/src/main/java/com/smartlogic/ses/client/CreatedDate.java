package com.smartlogic.ses.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement(name = "createdDate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreatedDate extends AbstractSimpleNodeDate implements Serializable {

  private static final long serialVersionUID = 2725685372690585118L;

  public CreatedDate() {
  }

  protected CreatedDate(String value) {
    super(value);
  }

  protected CreatedDate(Element element) {
    super(element);
  }

}
