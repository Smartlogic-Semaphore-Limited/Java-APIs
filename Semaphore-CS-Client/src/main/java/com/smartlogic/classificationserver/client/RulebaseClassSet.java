package com.smartlogic.classificationserver.client;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RulebaseClassSet extends XMLReader {

	public RulebaseClassSet(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		if (data == null) throw new ClassificationException("No response from classification server");

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
	}

	private List<RulebaseClass>  rulebaseClasses;
	public List<RulebaseClass> getRulebaseClasses() {
		return rulebaseClasses;
	}
}
