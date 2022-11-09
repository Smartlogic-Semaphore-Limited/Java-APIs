package com.smartlogic.classificationserver.client;

import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;

public class CSVersion extends XMLReader {

  private String version;

  protected CSVersion(byte[] data) throws ClassificationException {
    try {
      Document document = getDocument(data);

      XPathFactory xpathFactory = XPathFactory.newInstance();
      xpathFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      XPath xpath = xpathFactory.newXPath();
      XPathExpression xpathExpression = xpath.compile("/response/version/text()");
      version = (String) xpathExpression.evaluate(document, XPathConstants.STRING);

    } catch (XPathExpressionException e) {
      throw new ClassificationException("%s thrown parsing XML returned from Version request: %s",
          e.getClass().getSimpleName(), new String(data, StandardCharsets.UTF_8));
    } catch (XPathFactoryConfigurationException e) {
      throw new ClassificationException("%s thrown creating XML parser in Version request: %s",
          e.getClass().getSimpleName(), e.getMessage());
    }
  }

  public String getVersion() {
    return version;
  }

}
