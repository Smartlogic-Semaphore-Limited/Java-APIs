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

@XmlRootElement(name = "attributeType")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeType implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(AttributeType.class);
  private static final long serialVersionUID = -3015301097391328905L;

  // This is required by the XML Marshalling/Unmarshalling
  public AttributeType() {
  }

  protected AttributeType(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logger.trace("Unrecognized child node: '" + element.getNodeName() + "'");
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        logger.trace("Unexpected text node: '" + element.getNodeName() + "'");
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        if ("ID".equals(attributeNode.getName())) {
          setId(attributeNode.getValue());
        } else if ("NAME".equals(attributeNode.getName())) {
          setName(attributeNode.getValue());
        } else {
          logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
        }
      }

    }
    logger.debug("Constructor - exit");
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
