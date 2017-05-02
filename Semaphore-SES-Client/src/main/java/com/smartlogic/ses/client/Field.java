//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@XmlRootElement(name = "field")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field implements Serializable
{
	protected final static Log logger = LogFactory.getLog(Field.class);
	private static final long serialVersionUID = 2505661564599105412L;

	// This is required by the XML Marshalling/Unmarshalling
	public Field() {}

	protected Field(Element element) {
		logger.debug("Constructor - entry");

		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
			    if ("RELATION_METADATA".equals(childElement.getNodeName())) {
			    	setRelationMetadata(new RelationMetadata(childElement));
			    } else {
			    	logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
			    }
			} else if (node.getNodeType() == Node.TEXT_NODE) {
				this.setValue(node.getNodeValue());
			}
		}

		Map<String, String> facetNameMap = new HashMap<String, String>();
		Map<String, String> facetIdMap = new HashMap<String, String>();

		NamedNodeMap namedNodeMap = element.getAttributes();
		if (namedNodeMap != null) {
			for (int a = 0; a < namedNodeMap.getLength(); a++) {
				Attr attributeNode = (Attr) namedNodeMap.item(a);
				if ("FREQ".equals(attributeNode.getName())) {
					setFrequency(Integer.parseInt(attributeNode.getValue()));
				} else if ("ID".equals(attributeNode.getName())) {
					setId(attributeNode.getValue());
				} else if ("ZID".equals(attributeNode.getName())) {
					setZid(attributeNode.getValue());
				} else if ("CLASS".equals(attributeNode.getName())) {
					setTermClass(attributeNode.getValue());
				} else if ("NAME".equals(attributeNode.getName())) {
					setName(attributeNode.getValue());
				} else if (attributeNode.getName().startsWith("FACET_ID")) {
					facetIdMap.put(attributeNode.getName(), attributeNode.getValue());
				} else if (attributeNode.getName().startsWith("FACET")) {
					facetNameMap.put(attributeNode.getName(), attributeNode.getValue());
				} else {
					logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
				}
			}

			for (int i = 0; i < facetNameMap.size(); i++) {
				addFacet(facetNameMap.get("FACET" + i));
				addFacetId(facetIdMap.get("FACET_ID" + i));
			}
		}
		logger.debug("Constructor - exit");
	}


	public Field(Term term) {
		setName("term");
		if (term.getId() != null) setId(term.getId().getValue());
		if (term.getName() != null) {
			setTermClass(term.getName().getValue());
			setValue(term.getName().getValue());
		}
		try {
			if (term.getFrequency() != null) {
				setFrequency(Integer.parseInt(term.getFrequency().getValue()));
			}
		} catch (NumberFormatException e) {
			// Do nothing
		}
		facets = new ArrayList<String>();
		if (term.getFacets() != null) {
			for (Facet facet: term.getFacets().getFacets()) {
				addFacet(facet.getName());
			}
		}
	}


	private String value;
    public String getValue() {
		return value;
	}
    public void setValue(String value) {
		this.value = value;
	}

    private int frequency;
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

    private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String zid;
	public String getZid() {
		return zid;
	}
	public void setZid(String zid) {
		this.zid = zid;
	}


	private String termClass;
	public String getTermClass() {
		return termClass;
	}
	public void setTermClass(String termClass) {
		this.termClass = termClass;
	}

    private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    private List<String> facetIds = new ArrayList<String>();
	public List<String> getFacetIds() {
		return facetIds;
	}
	public void addFacetId(String facetId) {
		this.facetIds.add(facetId);
	}
	public void setFacetIds(List<String> facetIds) {
		this.facetIds = facetIds;
	}

	private List<String> facets = new ArrayList<String>();
	public List<String> getFacets() {
		return facets;
	}
	public void addFacet(String facet) {
		this.facets.add(facet);
	}
	public void setFacets(List<String> facets) {
		this.facets = facets;
	}

	private RelationMetadata relationMetadata;
	public RelationMetadata getRelationMetadata() {
		return relationMetadata;
	}
	public void setRelationMetadata(RelationMetadata relationMetadata) {
		this.relationMetadata = relationMetadata;
	}

}
