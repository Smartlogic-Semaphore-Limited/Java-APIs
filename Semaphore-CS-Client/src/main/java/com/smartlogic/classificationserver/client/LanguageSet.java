package com.smartlogic.classificationserver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LanguageSet extends XMLReader {
	protected final Log logger = LogFactory.getLog(getClass());
	public LanguageSet(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		if (data == null) throw new ClassificationException("No response from classification server");
		
		try {
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
	
	private List<Language>  languages;
	public List<Language> getLanguages() {
		return languages;
	}
	

}
