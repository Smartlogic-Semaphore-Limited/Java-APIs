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

@XmlRootElement(name = "path")
@XmlAccessorType(XmlAccessType.FIELD)
public class Path extends AbstractFieldListElement implements Serializable {
  private static final long serialVersionUID = 4130038987995988658L;

  // This is required by the XML Marshalling/Unmarshalling
  public Path() {
  }

  protected Path(Element element) {
    super(element);
  }

  protected Path(Term term) {
    super(term);
  }

}
