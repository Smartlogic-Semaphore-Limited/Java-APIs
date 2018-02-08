package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TestHash extends ClassificationTestCase {

	@Test
	public void testHash() throws ClassificationException {

		Result result = classificationClient.getClassifiedDocument(new Body("Wibble bot"), new Title("Cheese crackers"));
		assertEquals("95bda699dc5f262137a7903d211bcc62", result.getHash());

	}

}
