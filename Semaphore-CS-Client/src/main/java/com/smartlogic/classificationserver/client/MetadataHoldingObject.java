package com.smartlogic.classificationserver.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetadataHoldingObject extends XMLReader {
	private final Map<String, Collection<MetaNode>> metaNodes = new TreeMap<>();

	protected void addMetaNode(Element metaElement) {
		String name = metaElement.getAttribute("name");
		String value = metaElement.getAttribute("value");
		String score = metaElement.getAttribute("score");
		String id = metaElement.getAttribute("id");
		MetaNode metaNode = new MetaNode(name, value, score, id);

		Collection<MetaNode> metaNodesForName = metaNodes.get(name);
		if (metaNodesForName == null) {
			metaNodesForName = new ArrayList<>();
			metaNodes.put(name, metaNodesForName);
		}
		metaNodesForName.add(metaNode);

		
		
		
		NodeList nodeList = metaElement.getChildNodes();

		for (int n = 0; n < nodeList.getLength(); n++) {
			Node childNode = nodeList.item(n);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)childNode;
				if ("META".equals(childElement.getNodeName())) {
					metaNode.addMetaNode(childElement);
				}
			}
		}
	}

	public Map<String, Collection<MetaNode>> getMetaNodes() {
		return metaNodes;
	}
	
}
