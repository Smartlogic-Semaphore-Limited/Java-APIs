package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertTrue;

import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;


public class GetClassificationHistoryTest extends ClassificationTestCase {

	public void testGetClassificationHistory() throws ParseException, ClassificationException {

		URL log4jPropertiesLocation = ClassLoader.getSystemResource("log4j.properties");
		if (log4jPropertiesLocation != null) System.out.println(log4jPropertiesLocation.toExternalForm());

		Date lastMidnight = new Date(86400000*((new Date()).getTime()/86400000));
		Date nextMidnight = new Date(86400000*((new Date()).getTime()/86400000 + 1));

		Collection<ClassificationRecord> classificationRecords = classificationClient.getClassificationHistory(lastMidnight, nextMidnight);
		assertTrue(classificationRecords.size() > 0, "There must be records there");
	}
}
