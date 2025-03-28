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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "term")
@XmlAccessorType(XmlAccessType.FIELD)
public class Term implements Serializable {
  private static final long serialVersionUID = 4770870508237781876L;
  protected static final Logger logger = LoggerFactory.getLogger(Term.class);

  public Term() {
  }

  protected Term(Element element) {
    logger.debug("Constructor - entry");

    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        if ("SUMMARY".equals(childElement.getNodeName())) {
          String summary = "";
          NodeList subNodeList = childElement.getChildNodes();
          for (int sn = 0; sn < subNodeList.getLength(); sn++) {
            summary = subNodeList.item(sn).getNodeValue();
          }
          logger.debug("Summary " + summary);
          String data[] = summary.split("@");
          setName(new Name(data[1]));
          setId(new Id(data[2]));
        } else if ("NAME".equals(childElement.getNodeName())) {
          setName(new Name(childElement));
        } else if ("ID".equals(childElement.getNodeName())) {
          setId(new Id(childElement));
        } else if ("ZID".equals(childElement.getNodeName())) {
          setZid(new Id(childElement));
        } else if ("DISPLAY_NAME".equals(childElement.getNodeName())) {
          setDisplayName(new DisplayName(childElement));
        } else if ("STATUS".equals(childElement.getNodeName())) {
          setStatus(childElement.getTextContent());
        } else if ("FREQUENCY".equals(childElement.getNodeName())) {
          setFrequency(new Frequency(childElement));
        } else if ("CLASSES".equals(childElement.getNodeName())) {
          setTermClasses(new TermClasses(childElement));
          if ((termClasses != null) &&
              (termClasses.getTermClasses() != null) &&
              (termClasses.getTermClasses().size() > 0)) {
            setTermClass(termClasses.getTermClasses().get(0));
          }
        } else if ("CLASS".equals(childElement.getNodeName())) {
          setTermClass(new TermClass(childElement));
        } else if ("PATH".equals(childElement.getNodeName())) {
          addPath(new Path(childElement));
        } else if ("HIERARCHY".equals(childElement.getNodeName())) {
          addHierarchy(new Hierarchy(childElement));
        } else if ("ASSOCIATED".equals(childElement.getNodeName())) {
          addAssociated(new Associated(childElement));
        } else if ("FACETS".equals(childElement.getNodeName())) {
          setFacets(new Facets(childElement));
        } else if ("ATTRIBUTE".equals(childElement.getNodeName())) {
          setAttribute(new Attribute(childElement));
        } else if ("METADATA".equals(childElement.getNodeName())) {
          setMetadata(new Metadata(childElement));
        } else if ("CREATED_DATE".equals(childElement.getNodeName())) {
          setCreatedDate(new CreatedDate(childElement));
        } else if ("MODIFIED_DATE".equals(childElement.getNodeName())) {
          setModifiedDate(new ModifiedDate(childElement));
        } else if ("SYNONYMS".equals(childElement.getNodeName())) {
          Synonyms synonyms = new Synonyms(childElement);
          addSynonyms(synonyms);
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
        if ("PERCENTAGE".equals(attributeNode.getName())) {
          setPercentage(attributeNode.getValue());
        } else if ("RANK".equals(attributeNode.getName())) {
          setRank(attributeNode.getValue());
        } else if ("SRC".equals(attributeNode.getName())) {
          setSrc(attributeNode.getValue());
        } else if ("WEIGHT".equals(attributeNode.getName())) {
          setWeight(attributeNode.getValue());
        } else if ("SCORE".equals(attributeNode.getName())) {
          setWeight(attributeNode.getValue());
        } else if ("URI".equals(attributeNode.getName())) {
          setURI(attributeNode.getValue());
        } else if ("INDEX".equals(attributeNode.getName())) {
          setIndex(attributeNode.getValue());
        } else {
          logger.trace("Unrecognized attribute: '" +
              attributeNode.getName() +
              "' (" +
              this.getClass().getName() +
              ")");
        }
      }
    }

    // If we already have paths, then just return them otherwise create one path with just this term
    // in it
    if (getPaths().isEmpty()) {
      addPath(new Path(this));
    }
    for (Path path : getPaths()) {
      if (path.getFields().isEmpty()) {
        path.addField(new Field(this));
      }
    }

    logger.debug("Constructor - exit");
  }

  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  private String URI;

  public String getURI() {
    return URI;
  }

  public void setURI(String uRI) {
    URI = uRI;
  }

  private String index;

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  private Name name;

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  private Id id;

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  private Id zid;

  public Id getZid() {
    if (zid == null) {
      return id;
    }
    return zid;
  }

  public void setZid(Id zid) {
    this.zid = zid;
  }

  private DisplayName displayName;

  public DisplayName getDisplayName() {
    return displayName;
  }

  public void setDisplayName(DisplayName displayName) {
    this.displayName = displayName;
  }

  private Frequency frequency;

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  private TermClasses termClasses;

  public TermClasses getTermClasses() {
    return termClasses;
  }

  public void setTermClasses(TermClasses termClasses) {
    this.termClasses = termClasses;
  }

  private TermClass termClass;

  @Deprecated // Use getTermClasses instead
  public TermClass getTermClass() {
    if (termClass != null) {
      return termClass;
    }

    if ((termClasses != null) && termClasses.getTermClasses() != null) {
      if (termClasses.getTermClasses().size() > 0) {
        return termClasses.getTermClasses().get(0);
      }
    }
    return null;
  }

  public void setTermClass(TermClass termClass) {
    this.termClass = termClass;
  }

  private List<Path> pathList = new ArrayList<>();

  public List<Path> getPaths() {
    return pathList;
  }

  public void addPath(Path path) {
    pathList.add(path);
  }

  public void setPaths(List<Path> pathList) {
    this.pathList = pathList;
  }

  private List<Hierarchy> hierarchyList = new ArrayList<>();

  public List<Hierarchy> getHierarchies() {
    return hierarchyList;
  }

  public void addHierarchy(Hierarchy hierarchy) {
    this.hierarchyList.add(hierarchy);
  }

  public void setHierarchies(List<Hierarchy> hierarchyList) {
    this.hierarchyList = hierarchyList;
  }

  private List<Associated> associatedList = new ArrayList<>();

  public List<Associated> getAssociateds() {
    return associatedList;
  }

  private void addAssociated(Associated associated) {
    this.associatedList.add(associated);
  }

  public void setAssociateds(List<Associated> associatedList) {
    this.associatedList = associatedList;
  }

  private Facets facets;

  public Facets getFacets() {
    return facets;
  }

  public void setFacets(Facets facets) {
    this.facets = facets;
  }

  private Attribute attribute;

  public Attribute getAttribute() {
    return attribute;
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  private Metadata metadata;

  public Metadata getMetadata() {
    return metadata;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

  private List<Synonyms> synonymsList = new ArrayList<>();

  public List<Synonyms> getSynonymsList() {
    return synonymsList;
  }

  public void setSynonymsList(List<Synonyms> synonymsList) {
    this.synonymsList = synonymsList;
    if ((synonymsList != null) && (synonymsList.size() > 0)) {
      this.synonyms = synonymsList.get(0);
    }
  }

  public void addSynonyms(Synonyms synonyms) {
    synonymsList.add(synonyms);
    this.synonyms = synonyms;
  }

  private CreatedDate createdDate;

  public CreatedDate getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(CreatedDate createdDate) {
    this.createdDate = createdDate;
  }

  private ModifiedDate modifiedDate;

  public ModifiedDate getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(ModifiedDate modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  private Synonyms synonyms;

  /**
   *
   * @return List of synonyms
   * @deprecated - use getSynonymsList instead for when a term has different types of synonym
   */
  @Deprecated
  public Synonyms getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(Synonyms synonyms) {
    this.synonyms = synonyms;
    this.synonymsList = new ArrayList<>();
    this.synonymsList.add(synonyms);
  }

  private float percentage;

  public float getPercentage() {
    return percentage;
  }

  public void setPercentage(String percentage) {
    this.percentage = Float.parseFloat(percentage);
  }

  public void setPercentage(float percentage) {
    this.percentage = percentage;
  }

  private int rank;

  public int getRank() {
    return rank;
  }

  public void setRank(String rank) {
    this.rank = Integer.parseInt(rank);
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  private int src;

  public int getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = Integer.parseInt(src);
  }

  public void setSrc(int src) {
    this.src = src;
  }

  private float weight;

  public float getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = Float.parseFloat(weight);
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  @Override
  public String toString() {
    return "Term: '" + this.getName() + "' (" + this.getId() + ")";
  }

}
