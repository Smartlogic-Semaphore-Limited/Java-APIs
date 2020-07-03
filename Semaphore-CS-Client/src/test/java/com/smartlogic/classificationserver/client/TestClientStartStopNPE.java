package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class TestClientStartStopNPE {
	@Test
	public void testClientStartStopNPE() {
		try {
			try (ClassificationClient client = new ClassificationClient()) {
				System.out.println("Client created, closing immediately.");
			}
		} catch (NullPointerException npe) {
			fail("NullPointerException caught when creating and immediately closing client.", npe);
		} catch (Exception e) {
			fail("Exception caught when creating and immediately closing client", e);
		}
	}
}
