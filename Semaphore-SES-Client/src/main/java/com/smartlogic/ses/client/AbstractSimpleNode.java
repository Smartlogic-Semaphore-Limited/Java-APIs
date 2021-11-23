// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSimpleNode implements Serializable {
  protected final static Log logger = LogFactory.getLog(AbstractSimpleNode.class);
  private static final long serialVersionUID = -6838090269047496654L;

  // Only preserved to enable Model to extend this class
  public AbstractSimpleNode() {
  }

  public AbstractSimpleNode(String value) {
    this.value = value;
  }

  public AbstractSimpleNode(Element element) {
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
        logger.trace("Unrecognized attribute: '" +
            attributeNode.getName() +
            "' (" +
            this.getClass().getName() +
            ")");
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

  @Override
  public String toString() {
    return value;
  }

  public boolean equals(String stringValue) {
    return value.equals(stringValue);
  }

}
