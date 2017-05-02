package com.smartlogic.classificationserver.deprecated;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationScore;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Title;

public class ClassifyUrl extends ClassificationTestCase {
	@SuppressWarnings("deprecation")
	public void testClassifyUrl() throws MalformedURLException, ClassificationException {
		
		Map<String, Collection<ClassificationScore>> classificationScores1 = classificationClient.classifyUrl(new URL("http://www.bbc.co.uk"));
		for (String rulebaseClass: classificationScores1.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: classificationScores1.get(rulebaseClass)) {
				System.out.println(classificationScore.getName() + ":" + classificationScore.getScore());
			}
		}

		Map<String, Collection<ClassificationScore>> classificationScores2 = classificationClient.classifyUrl(new URL("http://www.bbc.co.uk"), new Title("BBC web site"), null);
		for (String rulebaseClass: classificationScores2.keySet()) {
			System.out.println(rulebaseClass);
			for (ClassificationScore classificationScore: classificationScores2.get(rulebaseClass)) {
				System.out.println(classificationScore.getName() + ":" + classificationScore.getScore());
			}
		}

			
	}

}
