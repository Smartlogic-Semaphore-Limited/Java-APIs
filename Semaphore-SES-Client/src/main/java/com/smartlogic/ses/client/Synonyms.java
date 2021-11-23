// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

@XmlRootElement(name = "synonyms")
@XmlAccessorType(XmlAccessType.FIELD)
public class Synonyms implements Serializable {
  protected final static Log logger = LogFactory.getLog(Synonyms.class);
  private static final long serialVersionUID = -351261996489773423L;

  // This is required by the XML Marshalling/Unmarshalling
  public Synonyms() {
  }

  protected Synonyms(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;

        if ("SYNONYM".equals(childElement.getNodeName())) {
          addSynonym(new Synonym(childElement));
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

  private List<Synonym> synonymList = new ArrayList<Synonym>();;

  public List<Synonym> getSynonyms() {
    return synonymList;
  }

  public void addSynonym(Synonym synonym) {
    synonymList.add(synonym);
  }

  public void setSynonyms(List<Synonym> synonymList) {
    this.synonymList = synonymList;
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
