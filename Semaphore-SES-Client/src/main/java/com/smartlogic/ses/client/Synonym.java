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

@XmlRootElement(name = "synonym")
@XmlAccessorType(XmlAccessType.FIELD)
public class Synonym implements Serializable {
  protected final static Log logger = LogFactory.getLog(Synonym.class);
  private static final long serialVersionUID = 8625280096217501919L;

  // This is required by the XML Marshalling/Unmarshalling
  public Synonym() {
  }

  protected Synonym(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("RELATION_METADATA".equals(childElement.getNodeName())) {
          setRelationMetadata(new RelationMetadata(childElement));
        } else {
          logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
        }
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        this.setValue(node.getNodeValue());
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        if ("ID".equals(attributeNode.getName())) {
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

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private RelationMetadata relationMetadata;

  public RelationMetadata getRelationMetadata() {
    return relationMetadata;
  }

  public void setRelationMetadata(RelationMetadata relationMetadata) {
    this.relationMetadata = relationMetadata;
  }

}
