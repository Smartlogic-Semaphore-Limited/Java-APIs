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

@XmlRootElement(name = "facets")
@XmlAccessorType(XmlAccessType.FIELD)
public class Facets implements Serializable {
  protected final static Log logger = LogFactory.getLog(Facets.class);
  private static final long serialVersionUID = 8305085158624892478L;

  public Facets() {
  }

  protected Facets(Element element) {
    logger.debug("Constructor - entry");
    facetList = new ArrayList<Facet>();

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;

        if ("FACET".equals(childElement.getNodeName())) {
          addFacet(new Facet(childElement));
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

  private List<Facet> facetList = new ArrayList<Facet>();

  public List<Facet> getFacets() {
    return facetList;
  }

  protected void addFacet(Facet facet) {
    facetList.add(facet);
  }

  public void setFacets(List<Facet> facetList) {
    this.facetList = facetList;
  }
}
