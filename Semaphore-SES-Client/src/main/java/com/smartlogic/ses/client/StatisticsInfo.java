// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "statisticsInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsInfo implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(StatisticsInfo.class);
  private static final long serialVersionUID = 6729391679309758805L;
  private static final XPath xPath = XPathFactory.newInstance().newXPath();

  // This is required by the XML Marshalling/Unmarshalling
  public StatisticsInfo() {
  }

  public StatisticsInfo(Element element) {
    logger.debug("Constructor - entry");

    setNumOfIndexes(element.getAttribute("indexes"));
    setTotalNumOfRequests(element.getAttribute("requests"));

    try {
      NodeList nodeList = (NodeList) xPath.evaluate("/SEMAPHORE/STATS/Indexes/Index", element,
          XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node n = nodeList.item(i);
        String name = n.getAttributes().getNamedItem("name").getNodeValue();

        int termCount = 0;
        NodeList labelsNodeList = (NodeList) xPath.evaluate("./Labels/*/Language[@code='en']/Label",
            n, XPathConstants.NODESET);
        for (int j = 0; j < labelsNodeList.getLength(); j++) {
          Node labelNode = labelsNodeList.item(j);
          String val = labelNode.getAttributes().getNamedItem("count").getNodeValue();
          if (val != null && val.length() > 0) {
            termCount += Integer.parseInt(val);
          }
        }
        termCounts.put(name, termCount);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
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
    this.avgNumOfRequestsPerSecOverLastHour =
        Double.parseDouble(avgNumOfRequestsPerSecOverLastHour);
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
