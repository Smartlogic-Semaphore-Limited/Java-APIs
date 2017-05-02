//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "statisticsInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsInfo implements Serializable {
	protected final static Log logger = LogFactory.getLog(Terms.class);
	private static final long serialVersionUID = 6729391679309758805L;

	// This is required by the XML Marshalling/Unmarshalling
	public StatisticsInfo() {}

	public StatisticsInfo(Element element) {
		logger.debug("Constructor - entry");

		NodeList nodeList = element.getChildNodes();
		for (int n = 0; n < nodeList.getLength(); n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if ("NumOfTerms".equals(childElement.getNodeName())) {
					String indexName = childElement.getAttribute("index");
					int termCount = Integer.parseInt(childElement.getTextContent());
					this.termCounts.put(indexName, termCount);
				} else {
					logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
				}
			} else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue() != null) && (node.getNodeValue().trim().length() > 0)) {
				logger.trace("Unexpected text node (" + this.getClass().getName() + "): '" + node.getNodeValue() + "'");
			}
		}
		NamedNodeMap namedNodeMap = element.getAttributes();
		if (namedNodeMap != null) {
			for (int a = 0; a < namedNodeMap.getLength(); a++) {
				Attr attributeNode = (Attr) namedNodeMap.item(a);
				if ("NumOfIndexes".equals(attributeNode.getName())) {
					setNumOfIndexes(attributeNode.getValue());
				} else if ("NumOfWorkers".equals(attributeNode.getName())) {
					setNumOfWorkers(attributeNode.getValue());
				} else if ("TotalNumOfRequests".equals(attributeNode.getName())) {
					setTotalNumOfRequests(attributeNode.getValue());
				} else if ("AvgNumOfRequestsPerSecOver5Mins".equals(attributeNode.getName())) {
					setAvgNumOfRequestsPerSecOver5Mins(attributeNode.getValue());
				} else if ("AvgNumOfRequestsPerSecOverLastHour".equals(attributeNode.getName())) {
					setAvgNumOfRequestsPerSecOverLastHour(attributeNode.getValue());
				} else if ("PeakNumOfRequestsPerSec".equals(attributeNode.getName())) {
					setPeakNumOfRequestsPerSec(attributeNode.getValue());
				} else {
					logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "' (" + this.getClass().getName() + ")");
				}
			}
		}

		logger.debug("Constructor - exit");
	}


	private int numOfIndexes;
	public int getNumOfIndexes() {
		return numOfIndexes;
	}
	public void setNumOfIndexes(String numOfIndexes) {
		this.numOfIndexes = Integer.parseInt(numOfIndexes);
	}
	public void setNumOfIndexes(int numOfIndexes) {
		this.numOfIndexes = numOfIndexes;
	}

 	private int numOfWorkers;
	public int getNumOfWorkers() {
		return numOfWorkers;
	}
	public void setNumOfWorkers(String numOfWorkers) {
		this.numOfWorkers = Integer.parseInt(numOfWorkers);
	}
	public void setNumOfWorkers(int numOfWorkers) {
		this.numOfWorkers = numOfWorkers;
	}

	private int totalNumOfRequests;
	public int getTotalNumOfRequests() {
		return totalNumOfRequests;
	}
	public void setTotalNumOfRequests(String totalNumOfRequests) {
		this.totalNumOfRequests = Integer.parseInt(totalNumOfRequests);
	}
	public void setTotalNumOfRequests(int totalNumOfRequests) {
		this.totalNumOfRequests = totalNumOfRequests;
	}

	private double avgNumOfRequestsPerSecOver5Mins;
	public double getAvgNumOfRequestsPerSecOver5Mins() {
		return avgNumOfRequestsPerSecOver5Mins;
	}
	public void setAvgNumOfRequestsPerSecOver5Mins(String avgNumOfRequestsPerSecOver5Mins) {
		this.avgNumOfRequestsPerSecOver5Mins = Double.parseDouble(avgNumOfRequestsPerSecOver5Mins);
	}
	public void setAvgNumOfRequestsPerSecOver5Mins(double avgNumOfRequestsPerSecOver5Mins) {
		this.avgNumOfRequestsPerSecOver5Mins = avgNumOfRequestsPerSecOver5Mins;
	}

	private double avgNumOfRequestsPerSecOverLastHour;
	public double getAvgNumOfRequestsPerSecOverLastHour() {
		return avgNumOfRequestsPerSecOverLastHour;
	}
	public void setAvgNumOfRequestsPerSecOverLastHour(String avgNumOfRequestsPerSecOverLastHour) {
		this.avgNumOfRequestsPerSecOverLastHour = Double.parseDouble(avgNumOfRequestsPerSecOverLastHour);
	}
	public void setAvgNumOfRequestsPerSecOverLastHour(double avgNumOfRequestsPerSecOverLastHour) {
		this.avgNumOfRequestsPerSecOverLastHour = avgNumOfRequestsPerSecOverLastHour;
	}

	private double peakNumOfRequestsPerSec;
	public double getPeakNumOfRequestsPerSec() {
		return peakNumOfRequestsPerSec;
	}
	public void setPeakNumOfRequestsPerSec(String peakNumOfRequestsPerSec) {
		this.peakNumOfRequestsPerSec = Double.parseDouble(peakNumOfRequestsPerSec);
	}
	public void setPeakNumOfRequestsPerSec(double peakNumOfRequestsPerSec) {
		this.peakNumOfRequestsPerSec = peakNumOfRequestsPerSec;
	}

	private Map<String, Integer> termCounts = new HashMap<String, Integer>();
	public Map<String, Integer> getTermCounts() {
		return termCounts;
	}
	public void setTermCounts(Map<String, Integer> termCounts) {
		this.termCounts = termCounts;
	}

	public int getTermCount(String indexName) {
		return termCounts.get(indexName);
	}


}
