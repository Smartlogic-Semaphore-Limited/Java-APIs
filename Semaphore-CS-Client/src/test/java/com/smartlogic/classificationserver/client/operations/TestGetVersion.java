package com.smartlogic.classificationserver.client.operations;

import static org.testng.Assert.assertTrue;

import java.text.ParseException;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class TestGetVersion extends ClassificationTestCase {

	@Test
	public void testGetVersion() throws ParseException, ClassificationException {

		String version = classificationClient.getVersion();

		assertTrue(version.indexOf("Classification Server") > -1, "Version");
		assertTrue(version.indexOf("built on") > -1, "Version");
	}
}
