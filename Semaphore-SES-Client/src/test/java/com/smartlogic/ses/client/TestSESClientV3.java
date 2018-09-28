package com.smartlogic.ses.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smartlogic.ses.client.exceptions.SESException;

public class TestSESClientV3 extends PrintingTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	public void setUp() {
		if (sesClient == null) {
			sesClient = new SESClient();
			sesClient.setConnectionTimeoutMS(0);
			sesClient.setHost("build-reference");
			sesClient.setOntology("disp_taxonomy");
			sesClient.setPath("/ses");
			sesClient.setPort(80);
			sesClient.setProtocol("http");
			sesClient.setSocketTimeoutMS(0);

		}
	}


	public void testGetAllTerms() throws SESException {
		logger.info("testGetAllTerms - entry");

		Map<String, Term> terms = sesClient.getAllTerms();
		assertEquals("Term count", 3080, terms.size());
		assertEquals("Term Name", "Education and skills", terms.get("OMITERMO439").getName().getValue());
//		print(term);
		logger.info("testGetAllTerms - exit");
	}

	public void testGetAllTermsFilteredByDate() throws SESException, ParseException {
		logger.info("testGetAllTermsFilteredByDate - entry");

		SESFilter sesFilter = new SESFilter();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sesFilter.setModifiedBeforeDate(sdf.parse("20120112"));
		sesFilter.setModifiedAfterDate(sdf.parse("201201012"));

		Map<String, Term> terms = sesClient.getAllTerms(sesFilter);
		assertEquals("Term count", 0, terms.size());
		logger.info("testGetAllTermsFilteredByDate - exit");
	}
	public void testTermDetailsOneTerm() throws SESException {
		logger.info("testTermDetailsOneTerm - entry");

		Term term = sesClient.getTermDetails("OMITERMO692");
		assertEquals("Term Name", "Business and industry", term.getName().getValue());
//		print(term);
		logger.info("testTermDetailsOneTerm - exit");
	}

	public void testTermDetailsThreeTerm() throws SESException {
		logger.info("testTermDetailsThreeTerm - entry");

		Map<String, Term> terms = sesClient.getTermDetails(new String[] {"OMITERMO760", "OMITERMO439", "OMITERMO616" });
		assertEquals("Terms count", 3, terms.size());

		assertEquals("Term Name", "Government, politics and public administration", terms.get("OMITERMO760").getName().getValue());
		assertEquals("Term Name", "Education and skills", terms.get("OMITERMO439").getName().getValue());
		assertEquals("Term Name", "Leisure and culture", terms.get("OMITERMO616").getName().getValue());

//		print(terms);
		logger.info("testTermDetailsThreeTerm - exit");
	}

	public void testTermDetailsManyTerm() throws SESException {
		logger.info("testTermDetailsManyTerm - entry");

		String[] termsIds = new String[] {"637fa56b-0826-57c0-a4fc-d345d4d9ea86", "d2240271-183a-51f1-b4fb-203985df0d7a", "51020e82-f5ac-5e43-9eb5-4a54b1e882ca"};

		Map<String, Term> terms  = sesClient.getTermDetails(termsIds);

		for (String key: terms.keySet()) {
			assertNotNull("Term shouldn't be null: " + key, terms.get(key));
		}
		assertEquals("Terms count", 3, terms.size());
		logger.info("testTermDetailsManyTerm - exit");
	}

	public void testConceptMapping() throws SESException {
		logger.info("testConceptMapping - entry");

		Map<String, Term> terms = sesClient.getMappedConcepts("education");
		assertEquals("Terms count", 34, terms.size());

		assertEquals("Term Name", "Health education", terms.get("a4f7d15e-d820-502b-85a6-2df34b048d59").getName().getValue());

//		print(terms);
		logger.info("testConceptMapping - exit");
	}


	public void testConceptMappingWithSpaces() throws SESException {
		logger.info("testConceptMapping - entry");

		Map<String, Term> terms = sesClient.getMappedConcepts("Health education");
		// We now return "Health and Education" and "Health" from this request
		assertEquals("Terms count", 2, terms.size());

		assertEquals("Term Name", "Health education", terms.get("a4f7d15e-d820-502b-85a6-2df34b048d59").getName().getValue());

//		print(terms);
		logger.info("testConceptMapping - exit");
	}

	public void testBrowse() throws SESException {
		logger.info("testBrowse - entry");
		Map<String, Term> terms = sesClient.browse();
		assertEquals("Terms count", 16, terms.size());

		assertEquals("Term Name", "International affairs and defence", terms.get("OMITERMO911").getName().getValue());
		logger.info("testBrowse - exit");
	}

	public void testBrowseOneTerm() throws SESException {
		logger.info("testBrowseOneTerm - entry");
		Map<String, Term> terms = sesClient.browse("c91344b4-f3a9-5a20-9b96-2b6043046302");
		assertEquals("Terms count", 5, terms.size());

		assertEquals("Term Name", "Farmers markets", terms.get("f35054e2-abd1-54eb-a3f6-a273f05c00b8").getName().getValue());
		assertEquals("Term Name", "Livestock markets", terms.get("fa99b82b-a642-54e0-be7d-25d7b26ae53c").getName().getValue());
		logger.info("testBrowseOneTerm - exit");
	}

	public void testGetTermDetailsByName() throws SESException {
		logger.info("testGetTermDetailsByName - entry");
		Map<String, Term> terms = sesClient.getTermDetailsByName("Livestock markets");
		assertEquals("Terms count", 1, terms.size());
		assertEquals("Term Name", "Livestock markets", terms.get("fa99b82b-a642-54e0-be7d-25d7b26ae53c").getName().getValue());
		logger.info("testGetTermDetailsByName - exit");
	}

	public void testGetTermDetailsByNameNPT() throws SESException {
		logger.info("testGetTermDetailsByNameNPT - entry");
		Map<String, Term> terms = sesClient.getTermDetailsByName("Employment relations");
		assertEquals("Terms count", 1, terms.size());
		assertEquals("Term Name", "Employment relations", terms.get("8ef29eb3-3a6d-520b-ad0a-e9bb4a24e3b4").getName().getValue());
		logger.info("testGetTermDetailsByNameNPT - exit");
	}

	public void testGetTermDetailsByNameNoSuchTerm() throws SESException {
		logger.info("testGetTermDetailsByNameNoSuchTerm - entry");
		Map<String, Term> terms = sesClient.getTermDetailsByName("Non-existent term");
		assertEquals("Terms count", 0, terms.size());
		logger.info("testGetTermDetailsByNameNoSuchTerm - exit");
	}

	public void testGetAtoZTerms() throws SESException {
		logger.info("testGetAtoZTerms - entry");
		Map<String, Term> terms = sesClient.getAtoZTerms("A");
		assertEquals("Terms count", 0, terms.size());
		logger.info("testGetAtoZTerms - exit");
	}

	public void testGetSortedAtoZTerms() throws SESException {
		logger.info("testGetSortedAtoZTerms - entry");
		Collection<Term> terms = sesClient.getSortedAtoZTerms("A");
		assertEquals("Terms count", 0, terms.size());
		logger.info("testGetSortedAtoZTerms - exit");
	}

	public void testSearch() throws SESException {
		logger.info("testSearch - entry");
		Map<String, Term> terms = sesClient.search("faeces");
		assertEquals("Terms count", 1, terms.size());
		assertEquals("Term Name", "Animal fouling", terms.get("ddc4132c-f811-5d91-9b51-3ae170d62eec").getName().getValue());
		logger.info("testSearch - exit");
	}



	public void testSortedSearch() throws SESException {
		logger.info("testSortedSearch - entry");
		Collection<Term> terms = sesClient.sortedSearch("faeces");
		assertEquals("Terms count", 1, terms.size());
		logger.info("testSortedSearch - exit");
	}

	public void testError() {
		logger.info("testError - entry");
		String oldOntology = sesClient.getOntology();
		sesClient.setOntology("RandomString");
		try {
			sesClient.getSortedAtoZTerms("A");
			fail("Exception not raised");
		} catch (SESException e) {
		}
		sesClient.setOntology(oldOntology);
		logger.info("testError - exit");
	}

	public void testGetVersionInfo() throws SESException {
		logger.info("testGetVersionInfo - entry");
		VersionInfo versionInfo = sesClient.getVersion();
		assertFalse("API", versionInfo.getApi() == null);
		assertFalse("Build", versionInfo.getBuild() == null);
		assertFalse("Index", versionInfo.getIndexStructure() == null);

		logger.info("testGetVersionInfo - exit");
	}

//	public void testGetStatisticsInfo() throws SESException {
//		logger.info("testGetStatisticsInfo - entry");
//		StatisticsInfo statisticsInfo1 = sesClient.getStatistics();
//		int requestCount1 = statisticsInfo1.getTotalNumOfRequests();
//
//		StatisticsInfo statisticsInfo2 = sesClient.getStatistics();
//		int requestCount2 = statisticsInfo2.getTotalNumOfRequests();
//		assertTrue("Request count increases", requestCount2 > requestCount1);
//		assertEquals("Term count", 19642, statisticsInfo2.getTermCount("disp_taxonomy"));
//		logger.info("testGetStatisticsInfo - exit");
//	}

	public void testPrefix() throws SESException {
		Map<String, TermHint> termHints = sesClient.getTermHints("appo");

		TermHint termHint2 = termHints.get("346a90a2-c8e9-523f-8a32-58fe02ba6efe");
		assertEquals("TH2", "346a90a2-c8e9-523f-8a32-58fe02ba6efe", termHint2.getId());
		assertEquals("TH2", "disp_taxonomy", termHint2.getIndex());
		assertEquals("TH2", "Appointments procedure", termHint2.getName());
		assertEquals("TH2", 1, termHints.size());
	}


	public void testListModels() throws SESException {
		logger.info("testListModels - entry");
		Collection<Model> models = sesClient.listModels();
		assertEquals("Models count", 1, models.size());
		assertTrue("First model", models.contains(new Model("disp_taxonomy")));
		logger.info("testListModels - exit");
	}



}
