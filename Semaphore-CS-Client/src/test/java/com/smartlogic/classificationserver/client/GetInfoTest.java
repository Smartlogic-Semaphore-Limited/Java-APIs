package com.smartlogic.classificationserver.client;

import java.text.ParseException;

public class GetInfoTest extends ClassificationTestCase {
	
	public void testGetInfo() throws ParseException, ClassificationException {
		
		CSInfo csInfo = classificationClient.getInfo();
		
		assertEquals("Children coun", 2, csInfo.getChildrenProcesses().size());
	}
	
}
