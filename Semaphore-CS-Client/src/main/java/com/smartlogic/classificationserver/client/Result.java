package com.smartlogic.classificationserver.client;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Result extends ClassifiableObject {
	protected static final Log logger = LogFactory.getLog(Result.class);

	public Result(Document document) throws ClassificationException {
		// If there is no data provided, then just return an empty category list
		if (document == null) return;
		
		Element element = document.getDocumentElement();
		
		NodeList nodeList = element.getElementsByTagName("STRUCTUREDDOCUMENT");
		if ((nodeList == null) || (nodeList.getLength() == 0)) {
			throw new ClassificationException("No STRUCTUREDDOCUMENT element returned by classification server: " + toString(document));
		}
		Element structuredDocumentElement = (Element)nodeList.item(0);

		addMetadata(structuredDocumentElement);

		// The hash value can be present as a HASH element on the Structured Document element, as a SYSTEM node, or as a META node
		NodeList hashNodeList = element.getElementsByTagName("HASH");
		if ((hashNodeList != null) && (hashNodeList.getLength() > 0)) {
			Element hashNode = (Element)hashNodeList.item(0);
			setHash(hashNode.getAttribute("value"));
		}
		if (hash == null) getHashFromElements(element, "SYSTEM");
		if (hash == null) getHashFromElements(element, "META");
		
		NodeList articleNodeList = structuredDocumentElement.getElementsByTagName("ARTICLE");
		if (articleNodeList != null) {
			for (int a = 0; a < articleNodeList.getLength(); a++) {
				Element articleElement = (Element)articleNodeList.item(a);
				addArticle(new Article(articleElement));
			}
		}
	}
	
	
	
	private void getHashFromElements(Element parentElement, String candidateName) {
		NodeList candidateNodeList = parentElement.getElementsByTagName(candidateName);
		if ((candidateNodeList != null) && (candidateNodeList.getLength() > 0)) {
			for (int n = 0; n < candidateNodeList.getLength(); n++) {
				Element candidateElement = (Element)candidateNodeList.item(0);
				if ("HASH".equals(candidateElement.getAttribute("name"))) {
					setHash(candidateElement.getAttribute("value"));
				}
			}
		}
	}

	private List<Article> articles = new LinkedList<Article>();
	/**
	 * Return the set of articles within the one results
	 * @return the articles
	 */
	public List<Article> getArticles() {
		return articles;
	}
	private void addArticle(Article article) {
		articles.add(article);
	}
	
	private String hash = null;
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
