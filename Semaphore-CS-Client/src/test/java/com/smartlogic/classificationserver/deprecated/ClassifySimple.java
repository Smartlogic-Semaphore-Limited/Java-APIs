package com.smartlogic.classificationserver.deprecated;

import java.util.Collection;
import java.util.Map;

import com.smartlogic.classificationserver.client.Body;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationScore;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.FileName;
import com.smartlogic.classificationserver.client.Title;

public class ClassifySimple extends ClassificationTestCase {

	@SuppressWarnings("deprecation")
	public void testSimple() throws ClassificationException {
		
		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis ";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.classifyDocument(new Body(body), new Title(title));
		for (String rulebaseClass: classificationScores.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: classificationScores.get(rulebaseClass)) {
				System.out.println(classificationScore.getId() + "     " + classificationScore.getName() + "(" + classificationScore.getScore() + ")");
			}
		}
		
	}

	@SuppressWarnings("deprecation")
	public void testSimpleWithFileName() throws ClassificationException {
		
		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		String filename = "A label to go in audit log";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.classifyDocument(new FileName(filename), new Body(body), new Title(title));
		for (String rulebaseClass: classificationScores.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: classificationScores.get(rulebaseClass)) {
				System.out.println(classificationScore.getId() + "     " + classificationScore.getName() + "(" + classificationScore.getScore() + ")");
			}
		}
		
	}

	@SuppressWarnings("deprecation")
	public void testSimpleWithResults() throws ClassificationException {
		
		String title = "Application Development";
		String body = "Application Development";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.classifyDocument(new Body(body), new Title(title));
		for (String rulebaseClass: classificationScores.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: classificationScores.get(rulebaseClass)) {
				System.out.println(classificationScore.getId() + "     " + classificationScore.getName() + "(" + classificationScore.getScore() + ")");
			}
		}
		
	}

}
