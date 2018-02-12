package com.smartlogic.classificationserver.client;

@Deprecated
public class ClassificationServerStatus extends XMLReader {

	protected ClassificationServerStatus(byte[] data) throws ClassificationException {
		
		getRootElement(data);
	}

}
