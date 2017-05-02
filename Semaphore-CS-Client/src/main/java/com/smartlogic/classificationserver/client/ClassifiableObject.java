package com.smartlogic.classificationserver.client;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parent object of the Classification Objects that can be assigned Classification Scores.
 * These are currently the document and article.
 * @author Smartlogic Semaphore
 *
 */
public abstract class ClassifiableObject extends MetadataHoldingObject {
	protected static final Log logger = LogFactory.getLog(ClassifiableObject.class);

	protected void addMetadata(Element element) {
		NodeList childNodeList = element.getChildNodes();
		if (childNodeList != null) {
			for (int m = 0; m < childNodeList.getLength(); m++) {
				Node childNode = childNodeList.item(m);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element)childNode;
					if ("META".equals(childElement.getNodeName())) {
						String name = childElement.getAttribute("name");
						String value = childElement.getAttribute("value");
						String score = childElement.getAttribute("score");
						String id = childElement.getAttribute("id");
						if (noData(score)) {
							addMeta(name, value);
						} else {
							addCategory(id, name , value, score);
						}

						addMetaNode(childElement);
					}
				}
			}
		}
	}


	private Map<String, Collection<ClassificationScore>> categories = new TreeMap<String, Collection<ClassificationScore>>();
	private Map<String, String> metas = new TreeMap<String, String>();

	protected boolean addCategory(String id, String category, String value, String score) {
		if (noData(category)) return false;
		if (noData(value)) return false;
		if (noData(score)) return false;

		Collection<ClassificationScore> categoryCollection = categories.get(category);
		if (categoryCollection == null) {
			categoryCollection = new TreeSet<ClassificationScore>();
			categories.put(category, categoryCollection);
		}
		try {
			ClassificationScore classificationScore = new ClassificationScore(category, value, score);
			if (!noData(id)) classificationScore.setId(id);
			categoryCollection.add(classificationScore);
			return true;
		} catch (NumberFormatException e) {
			logger.warn("Bad format for score: " + score);
			return false;
		}
	}

	protected void addMeta(String name, String value){
		if (noData(name))
			return;
		if (noData(value))
			return;
		metas.put(name, value);
	}
	private boolean noData(String value) {
		if ((value == null) || (value.length() == 0)) return true;
		return false;
	}
	public Map<String, String> getMetadata() {
		return metas;
	}

	public Map<String, Collection<ClassificationScore>> getAllClassifications() {
		return categories;
	}
	protected Collection<ClassificationScore> getCategory(String category) {
		return categories.get(category);
	}
	protected Collection<String> getCategoryNames() {
		return categories.keySet();
	}


}
