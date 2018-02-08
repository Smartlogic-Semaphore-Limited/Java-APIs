package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;


public class TestRtrim {


	@Test
	public void test() {
		ClassificationHistory classificationHistory = new ClassificationHistory("", "", "");

		assertEquals("abcde", classificationHistory.rtrim("abcde"), "CH 1");
		assertEquals(" abcde", classificationHistory.rtrim(" abcde"), "CH 2");
		assertEquals("abcde", classificationHistory.rtrim("abcde "), "CH 3");
		assertEquals(" abcde", classificationHistory.rtrim(" abcde "), "CH 4");
	}

}
