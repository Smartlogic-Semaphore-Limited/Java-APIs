package com.smartlogic.ontologyeditor.threadtest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;

public class ThreadedTester {
	protected final static Logger logger = LoggerFactory.getLogger(ThreadedTester.class);

	public static void main(String[] args) throws OEClientException, InterruptedException {
		OEClientReadWrite oeClient = new OEClientReadWrite();
		oeClient.setBaseURL("http://localhost:8080/workbench-webapp-4.0.18.1-rc2/");
		oeClient.setModelUri("task:LoadTest0:Task");
		oeClient.setToken("WyJrZWl0aCIsMTQ1NzE5MDYyMiwiTUNFQ0R3Q0Zic1lMTnJZSS9iRDFubXZ1ekFJT1RzR1ZsTHFKNWdnQ1hWQzRxMTQ9Il0=");

		String conceptSchemeUri = "http://example.com/LoadTest#ConceptScheme/Snazzy-new-concept-scheme";
		String newConceptUri = "http://smartlogic.com/LoadTest-Thread-%d-Term-%d";
		String newConceptLabel = "Concept - Thread %d Term %d";

		List<TestThread> threads = new ArrayList<TestThread>();
		for (int i = 0; i < 5; i++) {
			TestThread testThread = new TestThread(oeClient, i, conceptSchemeUri, newConceptUri, newConceptLabel);
			threads.add(testThread);
			testThread.start();
		}

		while (threads.size() > 0) {
			TestThread testThread = threads.remove(0);
			testThread.join();
		}
		logger.info("All done");
	}
}
