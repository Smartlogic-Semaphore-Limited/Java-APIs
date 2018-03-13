package com.smartlogic.classificationserver.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Defaults extends XMLReader {
	protected final Log logger = LogFactory.getLog(getClass());
	public Defaults(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		defaults = new HashMap<String, Parameter>();
		if (data == null) throw new ClassificationException("No response from classification server");
		
		Element element = getRootElement(data);
		
		NodeList defaultsNodeList = element.getElementsByTagName("Parameters");
		if ((defaultsNodeList == null) || (defaultsNodeList.getLength() == 0)) {
			//do not throw an exception - for backward compatibility return empty defaults
			return;
			
		}
		
		NodeList paramNodeList = ((Element)defaultsNodeList.item(0)).getElementsByTagName("Param");
		if (paramNodeList == null) return;
		
		for (int i = 0; i < paramNodeList.getLength(); i++) {
			Element paramElement = (Element)paramNodeList.item(i);
			String name = paramElement.getAttribute("name");
			if (name != null){
				name = name.toLowerCase();
			}
			String value = paramElement.getAttribute("value");
			Parameter p = new Parameter();
			p.setName(name);
			p.setValue(value);
			String translation = paramElement.getAttribute("translation");
			if (translation != null){
				p.setTranslation(translation);
			}
			defaults.put(name, p);
			logger.debug("for parameter " + name +" extracted default value " + value);
		}
	}
	
	private Map<String, Parameter> defaults;
	public Map<String, Parameter> getDefaults() {
		return defaults;
	}
	

}
