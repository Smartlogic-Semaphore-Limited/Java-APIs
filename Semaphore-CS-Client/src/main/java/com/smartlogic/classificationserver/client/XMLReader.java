package com.smartlogic.classificationserver.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
	
	protected static Document getDocument(byte[] data) throws ClassificationException {
		try {
			DocumentBuilder documentBuilder = getDocumentBuilderFactory().newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(data);
			InputSource inputSource = new InputSource(inputStream);
			return documentBuilder.parse(inputSource);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ClassificationException("Error writing document to string in debug code: %f - %f", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	protected Element getRootElement(byte[] data) throws ClassificationException {
		return getDocument(data).getDocumentElement();
	}

	protected String toString(byte[] data) throws ClassificationException {
		return new String(data, StandardCharsets.UTF_8);
	}
	
	protected String toString(Document document) throws ClassificationException {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException e) {
			throw new ClassificationException("Error writing document to string in debug code: %f - %f", e.getClass().getSimpleName(), e.getMessage());
		}
		
	}



}
