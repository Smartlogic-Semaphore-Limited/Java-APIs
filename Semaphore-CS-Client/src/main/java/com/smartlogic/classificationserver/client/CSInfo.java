package com.smartlogic.classificationserver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CSInfo extends XMLReader {

	public CSInfo(byte[] data) throws ClassificationException {
		// If there is no data provided, then just return an empty category list
		if (data == null) return;
		
		try {
			Element element = getRootElement(data);
			
			NodeList nodeList = element.getElementsByTagName("info");
			if ((nodeList == null) || (nodeList.getLength() == 0)) {
				throw new ClassificationException("No info element returned by classification server: " + toString(data));
			}
			Element infoElement = (Element)nodeList.item(0);


			parentProcesses = new ArrayList<CSProcess>();
			NodeList parentNodeList = infoElement.getElementsByTagName("parent");
			if (parentNodeList != null) {
				for (int p = 0; p < parentNodeList.getLength(); p++) {
					Element parentElement = (Element)parentNodeList.item(p);
					parentProcesses.addAll(getProcesses(parentElement));
				}
			}

			childrenProcesses = new ArrayList<CSProcess>();
			NodeList chilrenNodeList = infoElement.getElementsByTagName("children");
			if (chilrenNodeList != null) {
				for (int p = 0; p < chilrenNodeList.getLength(); p++) {
					Element childrenElement = (Element)chilrenNodeList.item(p);
					childrenProcesses.addAll(getProcesses(childrenElement));
				}
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
	
	private Collection<CSProcess> getProcesses(Element containingElement) {
		Collection<CSProcess> processes = new ArrayList<CSProcess>();
		NodeList processNodeList = containingElement.getElementsByTagName("process");
		if (processNodeList != null) {
			for (int p = 0; p < processNodeList.getLength(); p++) {
				Element processElement = (Element)processNodeList.item(p);
				processes.add(new CSProcess(processElement));
			}
		}
		return processes;
	}

	private Collection<CSProcess> parentProcesses;
	public Collection<CSProcess> getParentProcesses() {
		return parentProcesses;
	}
	public void setParentProcesses(Collection<CSProcess> parentProcesses) {
		this.parentProcesses = parentProcesses;
	}

	private Collection<CSProcess> childrenProcesses;
	public Collection<CSProcess> getChildrenProcesses() {
		return childrenProcesses;
	}
	public void setChildrenProcesses(Collection<CSProcess> childrenProcesses) {
		this.childrenProcesses = childrenProcesses;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CS Info:\n");
		sb.append("Parent: \n");
		for (CSProcess csProcess: parentProcesses) {
			sb.append("     " + csProcess + "\n");
		}
		
		sb.append("Children: \n");
		for (CSProcess csProcess: childrenProcesses) {
			sb.append("     " + csProcess + "\n");
		}

		return sb.toString();
	}
}
