package com.smartlogic.classificationserver.client.operations;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class TestPublishPakFiles extends ClassificationTestCase {

	@Test
	public void publishPakFiles() throws ClassificationException {
		classificationClient.clearPublishSet("TestPublishSet");
		
		Collection<File> pakFiles = new HashSet<File>();
		pakFiles.add(new File("src/test/resources/pakfiles/NamedEntityRules.pak"));
		classificationClient.sendPakfiles("TestPublishSet", pakFiles);
		
		classificationClient.commitPublishSet("TestPublishSet");

		classificationClient.deactivatePublishSet("TestPublishSet");
		
	}
	
}
