package com.smartlogic.classificationserver.client;

import junit.framework.TestCase;

import org.junit.Test;

public class TestRtrim extends TestCase {


	@Test
	public void test() {
		ClassificationHistory classificationHistory = new ClassificationHistory("", "", "");

		assertEquals("CH 1", "abcde", classificationHistory.rtrim("abcde"));
		assertEquals("CH 2", " abcde", classificationHistory.rtrim(" abcde"));
		assertEquals("CH 3", "abcde", classificationHistory.rtrim("abcde "));
		assertEquals("CH 4", " abcde", classificationHistory.rtrim(" abcde "));
	}

}
