// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

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

@XmlRootElement(name = "terms")
@XmlAccessorType(XmlAccessType.FIELD)
public class Terms implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(Terms.class);
  private static final long serialVersionUID = 7147624986939139947L;

  public Terms() {
  }

  protected Terms(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;

        if ("TERM".equals(childElement.getNodeName())) {
          addTerm(new Term(childElement));
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
        if ("ID".equals(attributeNode.getName())) {
          this.setId(attributeNode.getValue());
        } else {
          logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
        }
      }
    }

    logger.debug("Constructor - exit");
  }

  private Map<String, Term> termMap = new LinkedHashMap<String, Term>();;

  public Map<String, Term> getTerms() {
    return termMap;
  }

  public void setTerms(Map<String, Term> termMap) {
    this.termMap = termMap;
  }

  private void addTerm(Term term) {
    termMap.put(term.getId().getValue(), term);
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
