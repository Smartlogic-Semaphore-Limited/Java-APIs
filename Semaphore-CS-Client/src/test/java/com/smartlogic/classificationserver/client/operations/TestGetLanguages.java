package com.smartlogic.classificationserver.client.operations;

import static org.testng.Assert.fail;

import java.util.Collection;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Language;

public class TestGetLanguages extends ClassificationTestCase {

	@Test
	public void testGetLanguages() throws ClassificationException {
		try {
			@SuppressWarnings("unused")
			Collection<Language> languages = classificationClient.getLanguages();
		} catch (Exception e) {
			fail("Exception thrown getting languages: " + e.getMessage());
		}
	}

}
