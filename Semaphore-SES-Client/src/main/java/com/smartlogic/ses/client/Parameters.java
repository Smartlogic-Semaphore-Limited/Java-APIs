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

@XmlRootElement(name = "parameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters implements Serializable {
  protected final static Log logger = LogFactory.getLog(Parameters.class);
  private static final long serialVersionUID = -3626466302252949328L;

  // This is required by the XML Marshalling/Unmarshalling
  public Parameters() {
  }

  protected Parameters(Element element) {
    logger.debug("Constructor - entry");
    parameterList = new ArrayList<Parameter>();

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;

        if ("PARAMETER".equals(childElement.getNodeName())) {
          addParameter(new Parameter(childElement));
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

  private List<Parameter> parameterList;

  public List<Parameter> getParameters() {
    return parameterList;
  }

  private void addParameter(Parameter parameter) {
    parameterList.add(parameter);
  }

  public void setParameters(List<Parameter> parameterList) {
    this.parameterList = parameterList;
  }
}
