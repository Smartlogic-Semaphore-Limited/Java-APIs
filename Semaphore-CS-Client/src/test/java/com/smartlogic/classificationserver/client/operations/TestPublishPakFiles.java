package com.smartlogic.classificationserver.client.operations;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.MetaNode;
import com.smartlogic.classificationserver.client.Result;

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
	
	@Test
	public void publishFactsExtractPakFiles() throws ClassificationException {
		classificationClient.clearPublishSet("FactsExtraction");
		
		Collection<File> pakFiles = new HashSet<File>();
		for (File file: new File("src/test/resources/Facts/PakFiles").listFiles()) 
			pakFiles.add(file);
		classificationClient.sendPakfiles("FactsExtraction", pakFiles);
		
		classificationClient.commitPublishSet("FactsExtraction");
		
		for (File file: new File("src/test/resources/Facts/Documents").listFiles()) {
			Result result = classificationClient.getClassifiedDocument(file, "text");
			for (Map.Entry<String, Collection<MetaNode>> entry: result.getMetaNodes().entrySet()) {
				System.out.println(entry.getKey());
				for (MetaNode metaNode: entry.getValue()) System.out.println(metaNode);
			}
		}

		classificationClient.deactivatePublishSet("FactsExtraction");
	}

}
