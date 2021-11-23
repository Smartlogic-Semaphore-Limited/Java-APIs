// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smartlogic.ses.client.utils.FieldComparator;

public abstract class AbstractFieldMapElement implements Serializable {
  protected final static Log logger = LogFactory.getLog(AbstractFieldMapElement.class);
  private static final long serialVersionUID = -7358409617941350295L;

  // This is required by the XML Marshalling/Unmarshalling
  public AbstractFieldMapElement() {
  }

  private static FieldComparator alphabeticalComparator =
      new FieldComparator(FieldComparator.SortField.ALPHABETICAL);
  private static FieldComparator frequencyComparator =
      new FieldComparator(FieldComparator.SortField.FREQUENCY);

  public enum IdentifierField {
    NAME, ID, UNKNOWN
  }

  abstract IdentifierField getIdentifierField();

  public AbstractFieldMapElement(Element element) {
    logger.debug("Constructor - entry");
    fieldMap = new HashMap<String, Field>();
    alphabeticalFields = new TreeSet<Field>(alphabeticalComparator);
    frequencyFields = new TreeSet<Field>(frequencyComparator);
    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("FIELD".equals(childElement.getNodeName())) {
          addField(new Field(childElement), this.getIdentifierField());
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
        } else if ("QTY".equals(attributeNode.getName())) {
          setCount(attributeNode.getValue());
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

  private Map<String, Field> fieldMap;

  public Map<String, Field> getFields() {
    return fieldMap;
  }

  public void setFields(Map<String, Field> fieldMap) {
    this.fieldMap = fieldMap;
  }

  /**
   * Return the set of fields in alphabetical order
   */
  private Collection<Field> alphabeticalFields;

  public Collection<Field> getAlphabeticalFields() {
    return alphabeticalFields;
  }

  public void setAlphabeticalFields(Collection<Field> alphabeticalFields) {
    this.alphabeticalFields = alphabeticalFields;
  }

  /**
   * Return the set of fields ordered by frequency
   */
  private Collection<Field> frequencyFields;

  public Collection<Field> getFrequencyFields() {
    return frequencyFields;
  }

  public void setFrequencyFields(Collection<Field> frequencyFields) {
    this.frequencyFields = frequencyFields;
  }

  private void addField(Field field, IdentifierField identifierField) {
    if (identifierField == IdentifierField.ID) {
      fieldMap.put(field.getId(), field);
    } else if (identifierField == IdentifierField.NAME) {
      fieldMap.put(field.getName(), field);
    }
    alphabeticalFields.add(field);
    frequencyFields.add(field);
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

  private int count;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setCount(String count) {
    this.count = Integer.parseInt(count);
  }

}
