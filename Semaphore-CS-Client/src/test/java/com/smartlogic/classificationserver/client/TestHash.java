package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TestHash extends ClassificationTestCase {

	@Test
	public void testHash() throws ClassificationException {

		Result result = classificationClient.getClassifiedDocument(new Body("Wibble bot"), new Title("Cheese crackers"));
		assertEquals("b86a6ace2b05946eaad628b32ebc7603", result.getHash());

	}

}
