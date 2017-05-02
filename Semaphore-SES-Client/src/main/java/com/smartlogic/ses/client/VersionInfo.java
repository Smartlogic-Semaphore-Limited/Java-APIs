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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "versionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class VersionInfo implements Serializable {
	protected final static Log logger = LogFactory.getLog(Terms.class);
	private static final long serialVersionUID = 4595923994840825277L;

	// This is required by the XML Marshalling/Unmarshalling
	public VersionInfo() {}

	public VersionInfo(Element element) {
		logger.debug("Constructor - entry");

		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;

				if ("VERSION".equals(childElement.getNodeName())) {
					String name = childElement.getAttribute("NAME");
					String revision = childElement.getAttribute("REVISION");
					if ("Compatible index structure version".equals(name)) {
						setIndexStructure(revision);
					} else if ("SES API version".equals(name)) {
						setApi(revision);
					} else if ("SES build".equals(name)) {
						setBuild(revision);
					} else {
						logger.trace("Unexpected version information: " + name + " (" + revision + ")");
					}
				} else {
					logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
				}
			} else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue() != null) && (node.getNodeValue().trim().length() > 0)) {
				logger.trace("Unexpected text node (" + this.getClass().getName() + "): '" + node.getNodeValue() + "'");
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

	private String indexStructure;
	public String getIndexStructure() {
		return indexStructure;
	}
	public void setIndexStructure(String indexStructure) {
		this.indexStructure = indexStructure;
	}

	private String api;
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}

	private String build;
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Version Information:\n");
		stringBuilder.append("Build: " + getBuild() + "\n");
		stringBuilder.append("Index: " + getIndexStructure() + "\n");
		stringBuilder.append("API: " + getApi() + "\n");
		return stringBuilder.toString();
	}

}
