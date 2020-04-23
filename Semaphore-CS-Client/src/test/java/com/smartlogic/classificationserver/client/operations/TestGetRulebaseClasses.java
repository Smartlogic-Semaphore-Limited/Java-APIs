package com.smartlogic.classificationserver.client.operations;

import static org.testng.Assert.assertEquals;

import java.text.ParseException;
import java.util.Collection;

import org.testng.annotations.Test;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.RulebaseClass;

public class TestGetRulebaseClasses extends ClassificationTestCase {

	@Test
	public void testGetRulebaseClasses() throws ParseException, ClassificationException {

		Collection<RulebaseClass> rulebaseClasses = classificationClient.getRulebaseClasses();

		assertEquals(17, rulebaseClasses.size(), "Rulebase count");

		for (RulebaseClass rulebaseClass: rulebaseClasses) System.err.println(rulebaseClass);
		
	}
}

