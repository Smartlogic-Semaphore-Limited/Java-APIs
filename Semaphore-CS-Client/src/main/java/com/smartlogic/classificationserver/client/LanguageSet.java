package com.smartlogic.classificationserver.client;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LanguageSet extends XMLReader {
	protected static final Logger logger = LoggerFactory.getLogger(LanguageSet.class);
	public LanguageSet(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		if (data == null) throw new ClassificationException("No response from classification server");
		
		Element element = getRootElement(data);
		
		NodeList languagesNodeList = element.getElementsByTagName("languages");
		if ((languagesNodeList == null) || (languagesNodeList.getLength() == 0)) {
			throw new ClassificationException("No languages returned by classification server: " + toString(data));
		}
		
		languages = new LinkedList<Language>();
		NodeList langNodeList = ((Element)languagesNodeList.item(0)).getElementsByTagName("language");
		if (langNodeList == null) return;
		
		for (int i = 0; i < langNodeList.getLength(); i++) {
			Element langElement = (Element)langNodeList.item(i);
			Language lang = new Language();
			String name = langElement.getAttribute("name");
			lang.setName(name);
			lang.setId(langElement.getAttribute("id"));
			String display = langElement.getAttribute("display");
			if (display == null){
				//old versions of CS did not return the display name
				display = name;
			}
			lang.setDisplay(display);
			logger.info("added language " + lang.toString());
			languages.add(lang);
		}
	}
	
	private List<Language>  languages;
	public List<Language> getLanguages() {
		return languages;
	}
	

}
