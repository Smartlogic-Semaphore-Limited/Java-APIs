package com.smartlogic.classificationserver.deprecated;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationScore;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Result;
import com.smartlogic.classificationserver.client.Title;

public class ClassifyDocumentWithHash extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyDocumentWithHash.class);
	
	
	@SuppressWarnings("deprecation")
	public void testBinary() throws IOException, ClassificationException {
		File file = new File("src/test/resources/data/44157109.pdf");
		FileInputStream fileInputStream = new FileInputStream(file);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int available;
		while ((available = fileInputStream.available()) > 0) {
			byte[] data = new byte[available];
			fileInputStream.read(data);
			byteArrayOutputStream.write(data);
		}
		fileInputStream.close();
		byteArrayOutputStream.close();

		Result result = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "44157109.pdf");
		Map<String, Collection<ClassificationScore>> binaryScores1 = result.getAllClassifications();
		String hash = result.getHash();
		System.out.println("Hash value: '" + hash + "'");
		for (String rulebaseClass: binaryScores1.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: binaryScores1.get(rulebaseClass)) {
				System.out.println(classificationScore.getName() + ":" + classificationScore.getScore());
			}
		}

		Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
		Collection<String> cheeses = new Vector<String>();
		cheeses.add("Brie");
		cheeses.add("Camenbert");
		cheeses.add("Cheddar");
		metadata.put("cheeses", cheeses);

		Map<String, Collection<ClassificationScore>> binaryScores2 = classificationClient.classifyBinary(byteArrayOutputStream.toByteArray(), "44157109.pdf", new Title("title"), metadata);
		for (String rulebaseClass: binaryScores2.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: binaryScores2.get(rulebaseClass)) {
				System.out.println(classificationScore.getName() + ":" + classificationScore.getScore());
			}
		}

	}
	
	
}
