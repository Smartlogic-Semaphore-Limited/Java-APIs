package com.smartlogic.classificationserver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ClassificationServerStatus extends XMLReader {

	protected ClassificationServerStatus(byte[] data) throws ClassificationException {
		
		try {
			getRootElement(data);
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

}
