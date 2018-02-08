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

	private static DocumentBuilderFactory documentBuilderFactory = null;
	
	private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(false);
		}
		return documentBuilderFactory;
	}
	
	protected Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder documentBuilder = getDocumentBuilderFactory().newDocumentBuilder();
		InputStream inputStream = new ByteArrayInputStream(data);
		InputSource inputSource = new InputSource(inputStream);
		return documentBuilder.parse(inputSource);
	}

	protected Element getRootElement(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		return getDocument(data).getDocumentElement();
	}

	protected String toString(byte[] data) throws ClassificationException {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ClassificationException("UnsupportedEncodingException raised: " + e.getMessage());
		}
	}

}
