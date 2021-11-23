// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractFieldListElement implements Serializable {
  protected final static Log logger = LogFactory.getLog(AbstractFieldListElement.class);
  private static final long serialVersionUID = 8798207670666532501L;

  // This is required by the XML Marshalling/Unmarshalling
  public AbstractFieldListElement() {
  }

  public AbstractFieldListElement(Element element) {
    logger.debug("Constructor - entry");
    fieldList = new Vector<Field>();

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("FIELD".equals(childElement.getNodeName())) {
          addField(new Field(childElement));
        } else {
          logger.trace("Unrecognized child node: '" +
              childElement.getNodeName() +
              "' (" +
              this.getClass().getName() +
              ")");
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
        if ("TYPE".equals(attributeNode.getName())) {
          setType(attributeNode.getValue());
        } else if ("ABBR".equals(attributeNode.getName())) {
          setAbbreviation(attributeNode.getValue());
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

  public AbstractFieldListElement(Term term) {
    fieldList = new Vector<Field>();
    fieldList.add(new Field(term));
  }

  private List<Field> fieldList;

  public List<Field> getFields() {
    return fieldList;
  }

  public void addField(Field field) {
    fieldList.add(field);
  }

  public void setFields(List<Field> fieldList) {
    this.fieldList = fieldList;
  }

  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  private String abbreviation;

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }
}
