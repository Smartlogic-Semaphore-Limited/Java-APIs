package com.smartlogic.classificationserver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RulebaseClassSet extends XMLReader {

	public RulebaseClassSet(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		if (data == null) throw new ClassificationException("No response from classification server");

		try {
			Element element = getRootElement(data);

			NodeList classesNodeList = element.getElementsByTagName("Classes");
			if ((classesNodeList == null) || (classesNodeList.getLength() == 0)) {
				throw new ClassificationException("No classes element returned by classification server: " + toString(data));
			}

			rulebaseClasses = new LinkedList<RulebaseClass>();
			NodeList classNodeList = ((Element)classesNodeList.item(0)).getElementsByTagName("Class");
			if (classNodeList == null) return;

			for (int i = 0; i < classNodeList.getLength(); i++) {
				Element classElement = (Element)classNodeList.item(i);

				RulebaseClass rulebaseClass = new RulebaseClass();
				rulebaseClass.setName(classElement.getAttribute("Name"));
				rulebaseClass.setRuleCount(Integer.parseInt(classElement.getAttribute("count_rules")));
				rulebaseClasses.add(rulebaseClass);
			}
		} catch (ParserConfigurationException e) {
			throw new ClassificationException("ParserConfigurationException raised: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new ClassificationException("UnsupportedEncodingException raised: " + e.getMessage());
		} catch (SAXException e) {
			throw new ClassificationException("SAXException raised: " + e.getMessage() + "\n" + toString(data));
		} catch (IOException e) {
			throw new ClassificationException("IOException raised: " + e.getMessage() + "\n" + toString(data));
		}
	}

	private List<RulebaseClass>  rulebaseClasses;
	public List<RulebaseClass> getRulebaseClasses() {
		return rulebaseClasses;
	}
}
