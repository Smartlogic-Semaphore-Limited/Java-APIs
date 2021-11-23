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

public abstract class AbstractNamedElement implements Serializable {
  protected final static Log logger = LogFactory.getLog(AbstractNamedElement.class);
  private static final long serialVersionUID = -7911903180763763696L;

  // This is required by the XML Marshalling/Unmarshalling
  public AbstractNamedElement() {
  }

  public AbstractNamedElement(Element element) {
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
        if ("NAME".equals(attributeNode.getName())) {
          setName(attributeNode.getValue());
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

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
