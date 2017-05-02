package com.smartlogic.classificationserver.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class XMLReader {
	protected Element getRootElement(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream inputStream = new ByteArrayInputStream(data);
		InputSource inputSource = new InputSource(inputStream);
		Document document = documentBuilder.parse(inputSource);

		Element element = document.getDocumentElement();
		return element;
	}

	protected String toString(byte[] data) throws ClassificationException {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ClassificationException("UnsupportedEncodingException raised: " + e.getMessage());
		}
	}

}
