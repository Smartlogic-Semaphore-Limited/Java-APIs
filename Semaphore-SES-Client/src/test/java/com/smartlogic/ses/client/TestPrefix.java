package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import junit.framework.TestCase;

import java.util.Map;

public class TestPrefix extends TestCase {
	private static SESClient sesClient;

	public void setUp() {
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
		}
	}

	public void testPrefixSea() throws SESException {
		Map<String, TermHint> termHints = sesClient.getTermHints("sea");

		TermHint termHintSea = termHints.get("8bac881f-21a9-533e-86c9-7cd22d0d8971");
		assertEquals("THS", "8bac881f-21a9-533e-86c9-7cd22d0d8971", termHintSea.getId());
//		assertEquals("THS", "disp_taxonomy", termHintSea.getIndex());
		assertEquals("THS", "Air Sea Rescue Service", termHintSea.getName());
		assertEquals("THS", "Public order, justice and rights", termHintSea.getFacets().getFacets().get(0).getName());
		assertEquals("THS", "Air ", termHintSea.getValues().getValues().get(0).getPreEm());
		assertEquals("THS", "Sea", termHintSea.getValues().getValues().get(0).getEm());
		assertEquals("THS", " Rescue Service", termHintSea.getValues().getValues().get(0).getPostEm());
//		assertTrue("THS",Math.abs(termHintSea.getWeight()-16.3078) < 0.1);
		assertEquals("THS", 10, termHints.size());
	}

	public void testPrefix1() throws SESException {
		Map<String, TermHint> termHints = sesClient.getTermHints("chi");

		TermHint termHint1 = termHints.get("9e933b80-b6b1-5d31-9f77-8d25c3dc5816");
		assertEquals("TH1", "Environment", termHint1.getFacets().getFacets().get(0).getName());
		assertEquals("TH1", 10, termHints.size()); //NOTE: this is the default we ship with.
	}

	public void testPrefix2() throws SESException {
		Map<String, TermHint> termHints = sesClient.getTermHints("apt");

		assertEquals("TH2", 0, termHints.size());
	}

	public void testMultiple() throws SESException {
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		for (int i = 0; i < 10; i++) {
			char c1 = alphabet[(int)(26*Math.random())];
			char c2 = alphabet[(int)(26*Math.random())];
			String prefix = Character.toString(c1) + Character.toString(c2);
			sesClient.getTermHints(prefix);
		}
	}
}
