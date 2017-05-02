package com.smartlogic.classificationserver.client;

import com.smartlogic.classificationserver.client.ClassificationScore;
import com.smartlogic.classificationserver.client.NotAScoreException;

import junit.framework.TestCase;

public class TestClassificationScore extends TestCase {

	public void testOldFormat() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.16227:0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "16227", classificationScore.getName());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}

	public void testNewFormat() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream Cheese(231):0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream Cheese", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}

	public void testDudFormat() throws NotAScoreException {
		try {
			@SuppressWarnings("unused")
			ClassificationScore classificationScore = new ClassificationScore("http");
			fail("Should have raised exception");
		} catch (NotAScoreException e) {}
	}


	public void testProtectedBrackets() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\(Cheese\\)(231):0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream (Cheese)", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}

	public void testProtectedSlash() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\\\Cheese(231):0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream \\Cheese", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}

	public void testProtectedColon() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\:Cheese(231):0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream :Cheese", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}
	public void testProtectedColons() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\:Chee\\:se(231):0.54");
		
		assertEquals("Class", "Generic_UPWARD_ID", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream :Chee:se", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}
	public void testPhone() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Phones.\\(928\\) 373-5149:0.35");
		
		assertEquals("Class", "Phones", classificationScore.getRulebaseClass());
		assertEquals("Name", "(928) 373-5149", classificationScore.getName());
		assertEquals("Id", null, classificationScore.getId());
		assertEquals("Score", 0.35f, classificationScore.getScore());
	}
	public void test23() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("test.23-214\\(A\\):0.35");
		
		assertEquals("Class", "test", classificationScore.getRulebaseClass());
		assertEquals("Name", "23-214(A)", classificationScore.getName());
		assertEquals("Id", null, classificationScore.getId());
		assertEquals("Score", 0.35f, classificationScore.getScore());
	}
	public void testEndSlash() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("test.23-214\\\\(12):0.35");
		
		assertEquals("Class", "test", classificationScore.getRulebaseClass());
		assertEquals("Name", "23-214\\", classificationScore.getName());
		assertEquals("Id", "12", classificationScore.getId());
		assertEquals("Score", 0.35f, classificationScore.getScore());
	}
	public void testBracketedClass() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("\\(Generic_UPWARD_ID\\).Cream Cheese(231):0.54");
		
		assertEquals("Class", "(Generic_UPWARD_ID)", classificationScore.getRulebaseClass());
		assertEquals("Name", "Cream Cheese", classificationScore.getName());
		assertEquals("Id", "231", classificationScore.getId());
		assertEquals("Score", 0.54f, classificationScore.getScore());
	}
	
}
