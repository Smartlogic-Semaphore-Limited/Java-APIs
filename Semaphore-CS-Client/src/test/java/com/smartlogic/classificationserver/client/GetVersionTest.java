package com.smartlogic.classificationserver.client;

import java.text.ParseException;

public class GetVersionTest extends ClassificationTestCase {

	public void testGetVersion() throws ParseException, ClassificationException {

		String version = classificationClient.getVersion();

		System.out.println("Version:" + version);
		assertEquals("Version", "Semaphore 4.0.26 - Classification Server", version.substring(0,40));
	}



}
