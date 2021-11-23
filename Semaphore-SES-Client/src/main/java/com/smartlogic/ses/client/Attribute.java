// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement(name = "attribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute extends AbstractFieldMapElement implements Serializable {
  private static final long serialVersionUID = -1493118185000578499L;

  // This is required by the XML Marshalling/Unmarshalling
  public Attribute() {
  }

  protected Attribute(Element element) {
    super(element);
  }

  @Override
  IdentifierField getIdentifierField() {
    return IdentifierField.NAME;
  }
}
