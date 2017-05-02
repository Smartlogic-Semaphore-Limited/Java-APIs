package com.smartlogic.classificationserver.client;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestRulebaseClasses extends ClassificationTestCase {

	public void testGetRulebaseClasses() throws ParseException, ClassificationException {

		Collection<RulebaseClass> rulebaseClasses = classificationClient.getRulebaseClasses();

		assertEquals("Rulebase count", 4, rulebaseClasses.size());

		Map<String, RulebaseClass> mappedClasses = new HashMap<String, RulebaseClass>();
		for (RulebaseClass rulebaseClass: rulebaseClasses) {
			mappedClasses.put(rulebaseClass.getName(), rulebaseClass);
		}

		RulebaseClass ipsvClass = mappedClasses.get("IPSV");
		assertEquals("Rulebase class name", "IPSV", ipsvClass.getName());
		assertEquals("Rulebase rule count", 3080, ipsvClass.getRuleCount());
	}
}

