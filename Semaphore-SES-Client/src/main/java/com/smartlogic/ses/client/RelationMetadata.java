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

@XmlRootElement(name = "relationMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationMetadata implements Serializable {
  private static final long serialVersionUID = 8742022211556685180L;
  protected static final Logger logger = LoggerFactory.getLogger(RelationMetadata.class);

  // This is required by the XML Marshalling/Unmarshalling
  public RelationMetadata() {
  }

  protected RelationMetadata(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("METADATA".equals(childElement.getNodeName())) {
          setMetadata(new Metadata(childElement));
        } else if ("ATTRIBUTE".equals(childElement.getNodeName())) {
          setAttribute(new Attribute(childElement));
        } else {
          logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
        }
      } else if ((node.getNodeType() == Node.TEXT_NODE) &&
          (node.getNodeValue() != null) &&
          (node.getNodeValue().trim().length() > 0)) {
        logger.trace("Unexpected text node (" +
            this.getClass().getName() +
            "): '" +
            node.getNodeValue() +
            "'");
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
      }
    }

    logger.debug("Constructor - exit");
  }

  private Attribute attribute;

  public Attribute getAttribute() {
    return attribute;
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  private Metadata metadata;

  public Metadata getMetadata() {
    return metadata;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

}
