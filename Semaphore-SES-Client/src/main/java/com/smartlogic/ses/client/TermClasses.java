package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

@XmlRootElement(name = "termClasses")
@XmlAccessorType(XmlAccessType.FIELD)
public class TermClasses implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(TermClasses.class);
  private static final long serialVersionUID = 8305085158624892478L;

  public TermClasses() {
  }

  protected TermClasses(Element element) {
    logger.debug("Constructor - entry");
    termClassList = new ArrayList<TermClass>();

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;

        if ("CLASS".equals(childElement.getNodeName())) {
          addTermClass(new TermClass(childElement));
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

  private List<TermClass> termClassList = new ArrayList<TermClass>();

  public List<TermClass> getTermClasses() {
    return termClassList;
  }

  protected void addTermClass(TermClass termClass) {
    termClassList.add(termClass);
  }

  public void setTermClasses(List<TermClass> termClassList) {
    this.termClassList = termClassList;
  }
}