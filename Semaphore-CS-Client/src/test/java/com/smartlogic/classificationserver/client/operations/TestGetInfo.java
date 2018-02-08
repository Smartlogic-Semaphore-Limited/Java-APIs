package com.smartlogic.classificationserver.client.operations;

import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.CSInfo;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class TestGetInfo extends ClassificationTestCase {

	@Test
	public void testGetInfo() throws ClassificationException {
		try {
			@SuppressWarnings("unused")
			CSInfo csInfo = classificationClient.getInfo();
		} catch (Exception e) {
			fail("Exception thrown getting info: " + e.getMessage());
		}
	}

}
