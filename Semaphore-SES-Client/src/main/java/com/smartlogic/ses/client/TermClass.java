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

@XmlRootElement(name = "termClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class TermClass extends AbstractSimpleNode implements Serializable {
  private static final long serialVersionUID = -2448957173216646436L;

  // This is required by the XML Marshalling/Unmarshalling
  public TermClass() {
  }

  protected TermClass(Element element) {
    super(element);
  }
}
