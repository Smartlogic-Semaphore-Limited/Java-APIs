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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "displayName")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisplayName implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(DisplayName.class);
  private static final long serialVersionUID = 7442240134525015441L;

  // This is required by the XML Marshalling/Unmarshalling
  public DisplayName() {
  }

  protected DisplayName(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        logger.trace("Unrecognized child node: '" +
            childElement.getNodeName() +
            "' (" +
            this.getClass().getName() +
            ")");
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        this.setValue(node.getNodeValue());
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        if ("NPT".equals(attributeNode.getName())) {
          setMatchOnNPT("1".equals(attributeNode.getValue()));
        } else {
          logger.trace("Unrecognized attribute: '" +
              attributeNode.getName() +
              "' (" +
              this.getClass().getName() +
              ")");
        }
      }
    }

    logger.debug("Constructor - exit");
  }

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  private boolean matchOnNPT;

  public boolean isMatchOnNPT() {
    return matchOnNPT;
  }

  public void setMatchOnNPT(boolean matchOnNPT) {
    this.matchOnNPT = matchOnNPT;
  }

  @Override
  public String toString() {
    return value;
  }

  public boolean equals(String stringValue) {
    return value.equals(stringValue);
  }

}
