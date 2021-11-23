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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "classType")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassType implements Serializable {
  protected final static Log logger = LogFactory.getLog(ClassType.class);
  private static final long serialVersionUID = -2448957173216646436L;

  // This is required by the XML Marshalling/Unmarshalling
  public ClassType() {
  }

  protected ClassType(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logger.trace("Unrecognized child node: '" + element.getNodeName() + "'");
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        this.setName(node.getNodeValue());
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        if ("ID".equals(attributeNode.getName())) {
          setId(attributeNode.getValue());
        } else if ("PARENT_ID".equals(attributeNode.getName())) {
          setParentId(attributeNode.getValue());
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

  private String parentId;

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
