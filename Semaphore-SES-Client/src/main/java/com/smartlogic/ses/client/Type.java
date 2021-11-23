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

@XmlRootElement(name = "type")
@XmlAccessorType(XmlAccessType.FIELD)
public class Type extends AbstractSimpleNode implements Serializable {
  private static final long serialVersionUID = -800624246763747424L;

  // This is required by the XML Marshalling/Unmarshalling
  public Type() {
  }

  protected Type(Element element) {
    super(element);
  }
}
