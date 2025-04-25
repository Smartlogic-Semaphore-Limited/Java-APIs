package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;


public class GetClassificationHistoryTest extends ClassificationTestCase {

	@Test
	public void testGetClassificationHistory() throws ParseException, ClassificationException {

		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseGetClassificationHistory.xml"))));

		URL log4jPropertiesLocation = ClassLoader.getSystemResource("log4j.properties");
		if (log4jPropertiesLocation != null) System.out.println(log4jPropertiesLocation.toExternalForm());

		Date lastMidnight = new Date(86400000*((new Date()).getTime()/86400000));
		Date nextMidnight = new Date(86400000*((new Date()).getTime()/86400000 + 1));

		Collection<ClassificationRecord> classificationRecords = classificationClient.getClassificationHistory(lastMidnight, nextMidnight);
    assertFalse(classificationRecords.isEmpty(), "There must be records in the log. Is the wiremock not working?");

		ClassificationRecord classificationRecord = classificationRecords.iterator().next();
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz").parse("2025-04-23 02:16:26+0000"),
				classificationRecord.getFinishDateTime());
		assertEquals("classify", classificationRecord.getOperation());
		assertEquals(Float.valueOf("254356"), classificationRecord.getTimeTaken());

	}
}
