//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "termHint")
@XmlAccessorType(XmlAccessType.FIELD)
public class TermHint implements Serializable {
	private static final long serialVersionUID = 5472204320325686302L;
	protected final static Log logger = LogFactory.getLog(TermHint.class);

	// This is required by the XML Marshalling/Unmarshalling
	public TermHint() {};

	protected TermHint(Element element) {
		logger.debug("Constructor - entry");

		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if ("HINT".equals(childElement.getNodeName())) {
					setHint(childElement.getTextContent());
					addValue(new Value(childElement));
				} else if ("CLASSES".equals(childElement.getNodeName())) {
					setTermClasses(new TermClasses(childElement));
				} else if ("FACET".equals(childElement.getNodeName())) {
					Facet facet = new Facet(childElement);
					setFacet(facet);
					addFacet(facet);
				} else {
					logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
				}
			} else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue() != null) && (node.getNodeValue().trim().length() > 0)) {
				logger.trace("Unrecognized text node");
			}
		}

		NamedNodeMap namedNodeMap = element.getAttributes();
		if (namedNodeMap != null) {
			for (int a = 0; a < namedNodeMap.getLength(); a++) {
				Attr attributeNode = (Attr) namedNodeMap.item(a);
				if ("NAME".equals(attributeNode.getName())) {
					setName(attributeNode.getValue());
				} else if ("ID".equals(attributeNode.getName())) {
					setId(attributeNode.getValue());
				} else if ("INDEX".equals(attributeNode.getName())) {
					setIndex(attributeNode.getValue());
				} else if ("WEIGHT".equals(attributeNode.getName())) {
					setWeight(attributeNode.getValue());
				} else if ("CLASS".equals(attributeNode.getName())) {
					setTermClass(attributeNode.getValue());
				} else {
					logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "' (" + this.getClass().getName() + ")");
				}
			}
		}

		logger.debug("Constructor - exit");
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String index;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
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

	private String termClass;
	public String getTermClass() {
		return termClass;
	}
	public void setTermClass(String termClass) {
		this.termClass = termClass;
	}

	private Facet facet;
	/*
	 * @deprecated - Please use getFacets instead as that supports the idea that terms might have multiple facets
	 */
	@Deprecated
	public Facet getFacet() {
		return facet;
	}
	/*
	 * @deprecated - Please use setFacets instead as that supports the idea that terms might have multiple facets
	 */
	@Deprecated
	public void setFacet(Facet facet) {
		this.facet = facet;
	}

	private Facets facets;
	public Facets getFacets() {
		return facets;
	}
	public void addFacet(Facet facet) {
		if (facets == null) facets = new Facets();
		facets.addFacet(facet);
	}
	public void setFacets(Facets facets) {
		this.facets = facets;
	}

	private TermClasses termClasses;
    public TermClasses getTermClasses() {
		return termClasses;
	}
	public void setTermClasses(TermClasses termClasses) {
		this.termClasses = termClasses;
	}


	private String hint;
	/*
	 * @deprecated - Please use getValues instead as that supports the idea that PTs terms might have multiple matching NPTs
	 */
	@Deprecated
	@XmlTransient
	public String getHint() {
		return hint;
	}
	/*
	 * @deprecated - Please use setValues instead as that supports the idea that PTs terms might have multiple matching NPTs
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	public Values values;
	public Values getValues() {
		return values;
	}
	public void addValue(Value value) {
		if (values == null) values = new Values();
		values.addValue(value);
	}
	public void setValues(Values values) {
		this.values = values;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(name);
		stringBuilder.append(" [" + id + "]");
		stringBuilder.append(" (" + facet.getName() + " - " + termClass + ")");
		return stringBuilder.toString();
	}

}
