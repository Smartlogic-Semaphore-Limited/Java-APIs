package com.smartlogic.ontologyeditor.threadtest;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.Label;

public class TestThread extends Thread {
	protected final static Logger logger = LoggerFactory.getLogger(TestThread.class);

	private final int threadIndex;
	private final String csURI;
	private final String cURITemplate;
	private final String cLabelTemplate;
	private final OEClientReadWrite oeClient;
	public TestThread(OEClientReadWrite oeClient, int threadIndex, String csURI, String cURITemplate, String cLabelTemplate) {
		this.oeClient = oeClient;
		this.threadIndex = threadIndex;
		this.csURI = csURI;
		this.cURITemplate = cURITemplate;
		this.cLabelTemplate = cLabelTemplate;
	}

	public void run() {
		for (int i = 0; i < 100; i++) {
			String conceptUri = String.format(cURITemplate, threadIndex, i);
			String conceptLabel = String.format(cLabelTemplate, threadIndex, i);
			Label label = new Label("en", conceptLabel);
			try {
				Concept concept = new Concept(oeClient, conceptUri, Arrays.asList(new Label[] { label }));
				oeClient.createConcept(csURI, concept);

				Concept returnedConcept = oeClient.getConcept(conceptUri);
				logger.info("Returned {}", returnedConcept);
			} catch (OEClientException e) {
				logger.error("OEClientException adding concept: {}", conceptLabel);
			} catch (Exception e) {
				logger.error("{} adding concept: {}", e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}
}
