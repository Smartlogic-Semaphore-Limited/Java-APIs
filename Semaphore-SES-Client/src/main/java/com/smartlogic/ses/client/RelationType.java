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

@XmlRootElement(name = "relationType")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationType implements Serializable {
  protected final static Log logger = LogFactory.getLog(RelationType.class);
  private static final long serialVersionUID = -3015301097391328905L;

  // This is required by the XML Marshalling/Unmarshalling
  public RelationType() {
  }

  protected RelationType(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logger.trace("Unrecognized child node: '" + element.getNodeName() + "'");
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        logger.trace("Unexpected text node: '" + element.getNodeName() + "'");
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr relationsNode = (Attr) namedNodeMap.item(a);
        if ("ID".equals(relationsNode.getName())) {
          setId(relationsNode.getValue());
        } else if ("NAME".equals(relationsNode.getName())) {
          setName(relationsNode.getValue());
        } else if ("FWD_DISP_NAME".equals(relationsNode.getName())) {
          setDisplayName(relationsNode.getValue());
        } else if ("REV_NAME".equals(relationsNode.getName())) {
          setReverseName(relationsNode.getValue());
        } else if ("REV_DISP_NAME".equals(relationsNode.getName())) {
          setReverseDisplayName(relationsNode.getValue());
        } else if ("ABRV".equals(relationsNode.getName())) {
          setAbbreviation(relationsNode.getValue());
        } else if ("REV_ABRV".equals(relationsNode.getName())) {
          setReverseAbbreviation(relationsNode.getValue());
        } else if ("SYMMETRIC".equals(relationsNode.getName())) {
          setSymmetric(Boolean.parseBoolean(relationsNode.getValue()));
        } else if ("SCOPE_NOTE".equals(relationsNode.getName())) {
          setScopeNote(relationsNode.getValue());
        } else {
          logger.trace("Unrecognized attribute: '" + relationsNode.getName() + "'");
        }
      }

    }
    logger.debug("Constructor - exit");
  }

  private boolean symmetric;

  public boolean isSymmetric() {
    return symmetric;
  }

  public void setSymmetric(boolean symmetric) {
    this.symmetric = symmetric;
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String reverseName;

  public String getReverseName() {
    return reverseName;
  }

  public void setReverseName(String reverseName) {
    this.reverseName = reverseName;
  }

  private String displayName;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  private String reverseDisplayName;

  public String getReverseDisplayName() {
    return reverseDisplayName;
  }

  public void setReverseDisplayName(String reverseDisplayName) {
    this.reverseDisplayName = reverseDisplayName;
  }

  private String scopeNote;

  public String getScopeNote() {
    return scopeNote;
  }

  public void setScopeNote(String scopeNote) {
    this.scopeNote = scopeNote;
  }

  private String abbreviation;

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  private String reverseAbbreviation;

  public String getReverseAbbreviation() {
    return reverseAbbreviation;
  }

  public void setReverseAbbreviation(String reverseAbbreviation) {
    this.reverseAbbreviation = reverseAbbreviation;
  }

}
