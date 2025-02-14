// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

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

@XmlRootElement(name = "omStructure")
@XmlAccessorType(XmlAccessType.FIELD)
public class OMStructure implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(OMStructure.class);
  private static final long serialVersionUID = -335815614955062079L;

  // This is required by the XML Marshalling/Unmarshalling
  public OMStructure() {
  }

  public OMStructure(Element element) {
    logger.debug("Constructor - entry");
    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("TERM_CLASSES".equals(childElement.getNodeName())) {
          setClassTypes(this.getList(childElement, "TERM_CLASS", new ClassTypeFactory()));
        } else if ("TERM_FACETS".equals(childElement.getNodeName())) {
          setFacets(this.getList(childElement, "FIELD", new FieldFactory()));
        } else if ("TERM_ATTRIBUTES".equals(childElement.getNodeName())) {
          setAttributes(this.getList(childElement, "TERM_ATTRIBUTE", new AttributeFactory()));
        } else if ("TERM_NOTES".equals(childElement.getNodeName())) {
          setNotes(this.getList(childElement, "TERM_NOTE", new NoteFactory()));
        } else if ("TERM_METADATA".equals(childElement.getNodeName())) {
          setChoices(this.getList(childElement, "METADATA_DEF", new ChoiceFactory()));
        } else if ("EQUIVALENCE_RELATIONS".equals(childElement.getNodeName())) {
          setEquivalenceRelations(
              this.getList(childElement, "RELATION_DEF", new RelationFactory()));
        } else if ("HIERARCHICAL_RELATIONS".equals(childElement.getNodeName())) {
          setHierarchicalRelations(
              this.getList(childElement, "RELATION_DEF", new RelationFactory()));
        } else if ("ASSOCIATIVE_RELATIONS".equals(childElement.getNodeName())) {
          setAssociativeRelations(
              this.getList(childElement, "RELATION_DEF", new RelationFactory()));
        } else if ("USERS".equals(childElement.getNodeName())) {
          setUsers(this.getList(childElement, "USER_DEF", new UserFactory()));
        } else if ("INDEX_METADATA".equals(childElement.getNodeName())) {
          setIndexMetadata(childElement);
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

  private <T> List<T> getList(Element element, String childTagName, Factory<T> factory) {
    TreeSet<T> children = new TreeSet<>(factory);

    logger.debug("Constructor - entry");
    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if (childTagName.equals(childElement.getNodeName())) {
          T child = factory.make(childElement);
          children.add(child);
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
    return new ArrayList<>(children);
  }

  private interface Factory<T> extends Comparator<T> {
    T make(Element elemen);
  }

  private class ClassTypeFactory implements Factory<ClassType> {
    @Override
    public ClassType make(Element element) {
      return new ClassType(element);
    }

    @Override
    public int compare(ClassType classType1, ClassType classType2) {
      return classType1.getName().compareToIgnoreCase(classType2.getName());
    }
  }

  private class FieldFactory implements Factory<Field> {
    @Override
    public Field make(Element element) {
      return new Field(element);
    }

    @Override
    public int compare(Field field1, Field field2) {
      return field1.getValue().compareToIgnoreCase(field2.getValue());
    }
  }

  private class AttributeFactory implements Factory<AttributeType> {
    @Override
    public AttributeType make(Element element) {
      return new AttributeType(element);
    }

    @Override
    public int compare(AttributeType attributeType1, AttributeType attributeType2) {
      return attributeType1.getName().compareToIgnoreCase(attributeType2.getName());
    }
  }

  private class NoteFactory implements Factory<NoteType> {
    @Override
    public NoteType make(Element element) {
      return new NoteType(element);
    }

    @Override
    public int compare(NoteType noteType1, NoteType noteType2) {
      return noteType1.getName().compareToIgnoreCase(noteType2.getName());
    }
  }

  private class ChoiceFactory implements Factory<ChoiceType> {
    @Override
    public ChoiceType make(Element element) {
      return new ChoiceType(element);
    }

    @Override
    public int compare(ChoiceType choiceType1, ChoiceType choiceType2) {
      return choiceType1.getName().compareToIgnoreCase(choiceType2.getName());
    }
  }

  private class RelationFactory implements Factory<RelationType> {
    @Override
    public RelationType make(Element element) {
      return new RelationType(element);
    }

    @Override
    public int compare(RelationType relationType1, RelationType relationType2) {
      return relationType1.getName().compareToIgnoreCase(relationType2.getName());
    }
  }

  private class UserFactory implements Factory<User> {
    @Override
    public User make(Element element) {
      return new User(element);
    }

    @Override
    public int compare(User user1, User user2) {
      return user1.getName().compareToIgnoreCase(user2.getName());
    }
  }

  private List<AttributeType> attributes;

  public List<AttributeType> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<AttributeType> attributes) {
    this.attributes = attributes;
  }

  private List<Field> facets;

  public List<Field> getFacets() {
    return facets;
  }

  public void setFacets(List<Field> facets) {
    this.facets = facets;
  }

  private List<NoteType> notes;

  public List<NoteType> getNotes() {
    return notes;
  }

  public void setNotes(List<NoteType> notes) {
    this.notes = notes;
  }

  private List<ClassType> classTypes;

  public List<ClassType> getClassTypes() {
    return classTypes;
  }

  public void setClassTypes(List<ClassType> classTypes) {
    this.classTypes = classTypes;
  }

  private List<ChoiceType> choices;

  public List<ChoiceType> getChoices() {
    return choices;
  }

  public void setChoices(List<ChoiceType> choices) {
    this.choices = choices;
  }

  private List<RelationType> equivalenceRelations;

  public List<RelationType> getEquivalenceRelations() {
    return equivalenceRelations;
  }

  public void setEquivalenceRelations(List<RelationType> equivalenceRelations) {
    this.equivalenceRelations = equivalenceRelations;
  }

  private List<RelationType> hierarchicalRelations;

  public List<RelationType> getHierarchicalRelations() {
    return hierarchicalRelations;
  }

  public void setHierarchicalRelations(List<RelationType> hierarchicalRelations) {
    this.hierarchicalRelations = hierarchicalRelations;
  }

  private List<RelationType> associativeRelations;

  public List<RelationType> getAssociativeRelations() {
    return associativeRelations;
  }

  public void setAssociativeRelations(List<RelationType> associativeRelations) {
    this.associativeRelations = associativeRelations;
  }

  private List<User> users;

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  private IndexMetadata indexmetas;

  public IndexMetadata getIndexMetadata() {
    return indexmetas;
  }

  public void setIndexMetadata(IndexMetadata metas) {
    this.indexmetas = metas;
  }

  private void setIndexMetadata(Element e) {
    this.indexmetas = new IndexMetadata(e);
  }

}
