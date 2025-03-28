// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smartlogic.ses.client.exceptions.SESException;

@XmlRootElement(name = "semaphore")
@XmlAccessorType(XmlAccessType.FIELD)
public class Semaphore implements Serializable {
  protected static final Logger logger = LoggerFactory.getLogger(Semaphore.class);
  private static final long serialVersionUID = 970605711180551842L;

  // This is required by the XML Marshalling/Unmarshalling
  public Semaphore() {
  }

  protected Semaphore(Element element) throws SESException {
    logger.debug("Constructor - entry");
    NodeList nodeList = element.getChildNodes();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) node;
        logger.debug(childElement.getNodeName());
        if ("PARAMETERS".equals(childElement.getNodeName())) {
          setParameters(new Parameters(childElement));
        } else if ("BROWSE_TERM".equals(childElement.getNodeName())) {
          NodeList termNodeList = childElement.getElementsByTagName("TERM");
          if ((termNodeList != null) && (termNodeList.getLength() > 0)) {
            setBrowseTerm(new Term((Element) termNodeList.item(0)));
          }
        } else if ("TERMS".equals(childElement.getNodeName())) {
          addTerms(new Terms(childElement));
        } else if ("TERM_HINTS".equals(childElement.getNodeName())) {
          setTermHints(new TermHints(childElement));
        } else if ("MODELS".equals(childElement.getNodeName())) {
          setModels(Model.readCollection(childElement));
        } else if ("MAIN_VERSIONS".equals(childElement.getNodeName())) {
          setVersionInfo(new VersionInfo(childElement));
        } else if ("FILES_VERSIONS".equals(childElement.getNodeName())) {
          // Not terribly interesting here I think
        } else if ("STATS".equals(childElement.getNodeName())) {
          setStatisticsInfo(new StatisticsInfo(childElement));
        } else if ("ERROR".equals(childElement.getNodeName())) {
          setError(new Error(childElement));
        } else if ("OM_STRUCTURE".equals(childElement.getNodeName())) {
          setOmStructure(new OMStructure(childElement));
        } else if ("BODY".equals(childElement.getNodeName())) {
          // This is part of the HTML rendering of errors in SES v2 and contains the error message
          setError(new Error(childElement));
        } else if ("HEAD".equals(childElement.getNodeName())) {
          // This is part of the HTML rendering of errors in SES v2 and so should be ignored here
        } else {
          logger.trace("Unrecognized child node: '" + childElement.getNodeName() + "'");
        }
      } else if ((node.getNodeType() == Node.TEXT_NODE) &&
          (node.getNodeValue() != null) &&
          (node.getNodeValue().trim().length() > 0)) {
        logger.trace("Unexpected text node (" +
            this.getClass().getName() +
            "): '" +
            node.getNodeValue() +
            "'");
      }
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    if (namedNodeMap != null) {
      for (int a = 0; a < namedNodeMap.getLength(); a++) {
        Attr attributeNode = (Attr) namedNodeMap.item(a);
        logger.trace("Unrecognized attribute: '" + attributeNode.getName() + "'");
      }
    }

    if (getError() != null) {
      throw new SESException(getError().getMessage().getValue());
    }

    logger.debug("Constructor - exit");
  }

  private Parameters parameters;

  public Parameters getParameters() {
    return parameters;
  }

  public void setParameters(Parameters parameters) {
    this.parameters = parameters;
  }

  private Terms terms;

  public Terms getTerms() {
    return terms;
  }

  private void addTerms(Terms terms) {
    if (this.terms == null) {
      this.terms = terms;
    } else {
      for (String termId : terms.getTerms().keySet()) {
        this.terms.getTerms().put(termId, terms.getTerms().get(termId));
      }
    }
  }

  public void setTerms(Terms terms) {
    this.terms = terms;
  }

  private Term browseTerm;

  public Term getBrowseTerm() {
    return browseTerm;
  }

  public void setBrowseTerm(Term browseTerm) {
    this.browseTerm = browseTerm;
  }

  private TermHints termHints;

  public TermHints getTermHints() {
    return termHints;
  }

  public void setTermHints(TermHints termHints) {
    this.termHints = termHints;
  }

  private Error error;

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  private Collection<Model> models = new TreeSet<>();

  public Collection<Model> getModels() {
    return models;
  }

  public void setModels(Collection<Model> models) {
    this.models = models;
  }

  private VersionInfo versionInfo;

  public VersionInfo getVersionInfo() {
    return versionInfo;
  }

  public void setVersionInfo(VersionInfo versionInfo) {
    this.versionInfo = versionInfo;
  }

  private StatisticsInfo statisticsInfo;

  public StatisticsInfo getStatisticsInfo() {
    return statisticsInfo;
  }

  public void setStatisticsInfo(StatisticsInfo statisticsInfo) {
    this.statisticsInfo = statisticsInfo;
  }

  private OMStructure omStructure;

  public OMStructure getOmStructure() {
    return omStructure;
  }

  public void setOmStructure(OMStructure omStructure) {
    this.omStructure = omStructure;
  }
}
