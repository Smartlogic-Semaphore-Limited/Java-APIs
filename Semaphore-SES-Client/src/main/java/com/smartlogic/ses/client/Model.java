//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;

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


@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Model extends AbstractSimpleNode implements Serializable, Comparable<Model>
{
	protected final static Log logger = LogFactory.getLog(Semaphore.class);
	private static final long serialVersionUID = -1766673272194018691L;

	// This is required by the XML Marshalling/Unmarshalling
	public Model() {}

	protected Model(String value) {
		this.name = value;
	}

	@Override
	@XmlTransient
	public String getValue() {
		return this.name;
	}

	protected Model(Element element) {
		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;

				if ("NAME".equals(childElement.getNodeName())) {
					this.name = childElement.getFirstChild().getTextContent();
				} else if ("LANGUAGE".equals(childElement.getNodeName())) {
					languages.add(childElement.getFirstChild().getTextContent());
				} else {
					logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
				}
			} else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue() != null) && (node.getNodeValue().trim().length() > 0)) {
				// This is the old version of SES
				this.name = node.getTextContent();
			}
		}

		NamedNodeMap namedNodeMap = element.getAttributes();
		if (namedNodeMap != null) {
			for (int a = 0; a < namedNodeMap.getLength(); a++) {
				Attr attributeNode = (Attr) namedNodeMap.item(a);
				logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
			}
		}

	}

	@Override
	public int compareTo(Model otherModel) {
		if (this == otherModel) return 0;
		return this.getName().compareTo(otherModel.getName());
	}

	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) return true;
		if (otherObject == null) return false;
		if (!(otherObject instanceof Model)) return false;

		Model otherModel = (Model)otherObject;
		return this.getName().equals(otherModel.getName());
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	public static Collection<Model> readCollection(Element element) {
		Collection<Model> models = new TreeSet<Model>();
		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;

				if ("MODEL".equals(childElement.getNodeName())) {
					models.add(new Model(childElement));
				} else {
					logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
				}
			} else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue() != null) && (node.getNodeValue().trim().length() > 0)) {
				logger.trace("Unexpected text node (" + Model.class.getName() + "): '" + node.getNodeValue() + "'");
			}
		}
		return models;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private Collection<String> languages = new TreeSet<String>();
	public Collection<String> getLanguages() {
		return languages;
	}
	public void setLanguages(Collection<String> languages) {
		this.languages = languages;
	}

}
