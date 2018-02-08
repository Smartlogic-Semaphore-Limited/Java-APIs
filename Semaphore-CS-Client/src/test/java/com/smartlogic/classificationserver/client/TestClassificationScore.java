package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;


public class TestClassificationScore  {

	@Test
	public void testOldFormat() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.16227:0.54");
		
		assertEquals( "Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("16227", classificationScore.getName(), "Name");
		assertEquals(classificationScore.getScore(), 0.54, 0.01f);
	}

	@Test
	public void testNewFormat() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream Cheese(231):0.54");
		
		assertEquals("Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream Cheese", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54, 0.01f);
	}

	@Test
	public void testDudFormat() throws NotAScoreException {
		try {
			@SuppressWarnings("unused")
			ClassificationScore classificationScore = new ClassificationScore("http");
			fail("Should have raised exception");
		} catch (NotAScoreException e) {}
	}


	@Test
	public void testProtectedBrackets() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\(Cheese\\)(231):0.54");
		
		assertEquals("Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream (Cheese)", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54, 0.01f);
	}

	@Test
	public void testProtectedSlash() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\\\Cheese(231):0.54");
		
		assertEquals("Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream \\Cheese", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54f, 0.01f);
	}

	@Test
	public void testProtectedColon() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\:Cheese(231):0.54");
		
		assertEquals("Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream :Cheese", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54f, 0.01f);
	}

	@Test
	public void testProtectedColons() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Generic_UPWARD_ID.Cream \\:Chee\\:se(231):0.54");
		
		assertEquals("Generic_UPWARD_ID", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream :Chee:se", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54f, 0.01f);
	}

	@Test
	public void testPhone() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("Phones.\\(928\\) 373-5149:0.35");
		
		assertEquals("Phones", classificationScore.getRulebaseClass(), "Class");
		assertEquals("(928) 373-5149", classificationScore.getName(), "Name");
		assertEquals(null, classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.35f, 0.01f);
	}

	@Test
	public void test23() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("test.23-214\\(A\\):0.35");
		
		assertEquals("test", classificationScore.getRulebaseClass(), "Class");
		assertEquals("23-214(A)", classificationScore.getName(), "Name");
		assertEquals(null, classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.35f, 0.01f);
	}

	@Test
	public void testEndSlash() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("test.23-214\\\\(12):0.35");
		
		assertEquals("test", classificationScore.getRulebaseClass(), "Class");
		assertEquals("23-214\\", classificationScore.getName(), "Name");
		assertEquals("12", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.35f, 0.01f);
	}

	@Test
	public void testBracketedClass() throws NotAScoreException {
		ClassificationScore classificationScore = new ClassificationScore("\\(Generic_UPWARD_ID\\).Cream Cheese(231):0.54");
		
		assertEquals("(Generic_UPWARD_ID)", classificationScore.getRulebaseClass(), "Class");
		assertEquals("Cream Cheese", classificationScore.getName(), "Name");
		assertEquals("231", classificationScore.getId(), "Id");
		assertEquals(classificationScore.getScore(), 0.54f, 0.01f);
	}
	
}
