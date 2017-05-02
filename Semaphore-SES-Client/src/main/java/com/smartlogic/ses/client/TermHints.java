//----------------------------------------------------------------------
// Product:     Search Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.LinkedHashMap;
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

@XmlRootElement(name = "termHints")
@XmlAccessorType(XmlAccessType.FIELD)
public class TermHints implements Serializable
{
	protected final static Log logger = LogFactory.getLog(TermHints.class);
	private static final long serialVersionUID = 3384249145735729707L;

	// This is required by the XML Marshalling/Unmarshalling
	public TermHints() {}

	protected TermHints(Element element) {
		logger.debug("Constructor - entry");
		termHintsMap = new LinkedHashMap<String, TermHint>();

		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;

				if ("TERM_HINT".equals(childElement.getNodeName())) {
					addTermHint(new TermHint(childElement));
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

	private Map<String, TermHint> termHintsMap;
    public Map<String, TermHint> getTermHints() {
        return termHintsMap;
    }
    private void addTermHint(TermHint termHint) {
    	termHintsMap.put(termHint.getId(), termHint);
    }
	public void setTermHints(Map<String, TermHint> termHintsMap) {
		this.termHintsMap = termHintsMap;
	}


}
