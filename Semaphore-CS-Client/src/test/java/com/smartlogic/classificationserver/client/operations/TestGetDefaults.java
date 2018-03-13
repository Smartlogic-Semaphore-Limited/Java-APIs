package com.smartlogic.classificationserver.client.operations;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Map;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Parameter;

public class TestGetDefaults extends ClassificationTestCase {

	@Test
	public void testGetLanguages() throws ClassificationException {
		try {
			Map<String, Parameter> defaults = classificationClient.getDefaults();
			
			Parameter charCountCutoff = defaults.get("charcountcutoff");
			assertEquals("500000", charCountCutoff.getValue());
		} catch (Exception e) {
			fail("Exception thrown getting defaults: " + e.getMessage());
		}
	}

}
