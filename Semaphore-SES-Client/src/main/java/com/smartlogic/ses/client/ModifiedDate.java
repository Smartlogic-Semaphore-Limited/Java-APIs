package com.smartlogic.ses.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement(name = "modifiedDate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModifiedDate extends AbstractSimpleNodeDate implements Serializable {

  private static final long serialVersionUID = -5011054020631168933L;

  public ModifiedDate() {
  }

  protected ModifiedDate(String value) {
    super(value);
  }

  protected ModifiedDate(Element element) {
    super(element);
  }

}
