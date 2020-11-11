package com.smartlogic.classificationserver.client.operations;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.RulebaseClass;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class TestGetRulebaseClasses extends ClassificationTestCase {

	@Test
	public void testGetRulebaseClasses() throws ParseException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseListRulebaseClasses.xml"))));

		Collection<RulebaseClass> rulebaseClasses = classificationClient.getRulebaseClasses();

		assertEquals(26, rulebaseClasses.size(), "Rulebase count");

		for (RulebaseClass rulebaseClass: rulebaseClasses) System.err.println(rulebaseClass);
		
	}
}

