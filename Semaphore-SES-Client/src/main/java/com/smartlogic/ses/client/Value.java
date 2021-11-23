package com.smartlogic.ses.client;
// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.FIELD)
public class Value implements Serializable {
  protected final static Log logger = LogFactory.getLog(Value.class);
  private static final long serialVersionUID = -7004907657877821941L;

  // This is required by the XML Marshalling/Unmarshalling
  public Value() {
  }

  protected Value(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    boolean emRead = false;
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.TEXT_NODE) {
        if (emRead) {
          this.setPostEm(node.getTextContent());
        } else {
          this.setPreEm(node.getTextContent());
        }
      } else if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("EM".equals(childElement.getNodeName())) {
          setEm(childElement.getTextContent());
          emRead = true;
        } else {
          logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
        }
      } else if ((node.getNodeType() == Node.TEXT_NODE) &&
          (node.getNodeValue() != null) &&
          (node.getNodeValue().trim().length() > 0)) {
        logger.trace("Unrecognized text node");
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        if ("NATURE".equals(attributeNode.getName())) {
          setNature(attributeNode.getValue());
        } else if ("ID".equals(attributeNode.getName())) {
          setId(attributeNode.getValue());
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

  private String nature;

  public String getNature() {
    return nature;
  }

  public void setNature(String nature) {
    this.nature = nature;
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String em = "";

  public String getEm() {
    return em;
  }

  public void setEm(String em) {
    this.em = em;
  }

  private String preEm = "";

  public String getPreEm() {
    return preEm;
  }

  public void setPreEm(String preEm) {
    this.preEm = preEm;
  }

  private String postEm = "";

  public String getPostEm() {
    return postEm;
  }

  public void setPostEm(String postEm) {
    this.postEm = postEm;
  }

  @XmlTransient
  public String getValue() {
    return preEm + em + postEm;
  }

}
