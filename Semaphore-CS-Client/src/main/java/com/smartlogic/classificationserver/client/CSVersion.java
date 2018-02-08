package com.smartlogic.classificationserver.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CSVersion extends XMLReader {

	private String version;

	protected CSVersion(byte[] data) throws ClassificationException {
		try {
			Document document = getDocument(data);

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression xpathExpression = xpath.compile("/response/version/text()");
			version = (String) xpathExpression.evaluate(document, XPathConstants.STRING);

		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			throw new ClassificationException("%s thrown parsing XML returned from Version request: %s", e.getClass().getSimpleName(), new String(data, StandardCharsets.UTF_8));
		}
	}

	public String getVersion() {
		return version;
	}

}
