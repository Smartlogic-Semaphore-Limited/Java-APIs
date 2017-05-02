package com.smartlogic.classificationserver.client;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Object to contain the information returned from Classification Server 
 * at the Article level.
 * @author Smartlogic Semaphore
 *
 */
public class Article extends ClassifiableObject {

	protected Article(Element articleElement) {
		NodeList childrenNodes = articleElement.getChildNodes();
		for (int n = 0; n < childrenNodes.getLength(); n++) {
			Node childNode = childrenNodes.item(n);
			if (Node.ELEMENT_NODE == childNode.getNodeType()) {
				Element childElement = (Element)childNode;
				if ("META".equals(childElement.getNodeName())) {
					addCategory(childElement.getAttribute("id"), childElement.getAttribute("name"), childElement.getAttribute("value"), childElement.getAttribute("score"));
				} else if ("PARAGRAPH".equals(childElement.getNodeName())) {
					Paragraph paragraph = new Paragraph();
					paragraph.setContent(childElement.getTextContent());
					addParagraph(paragraph);
				} else if ("FIELD".equals(childElement.getNodeName())) {
					Paragraph paragraph = new Paragraph();
					paragraph.setField(childElement.getAttribute("NAME"));
					paragraph.setContent(childElement.getTextContent());
					addParagraph(paragraph);
				}
			}
		}
	}

	private List<Paragraph> paragraphs = new LinkedList<Paragraph>();
	
	/**
	 * Return the list of paragraphs identified within the article
	 * @return List of paragraphs
	 */
	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}
	public void addParagraph(Paragraph paragraph) {
		this.paragraphs.add(paragraph);
	}
	
}
