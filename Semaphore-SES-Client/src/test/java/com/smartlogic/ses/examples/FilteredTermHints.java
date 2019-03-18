package com.smartlogic.ses.examples;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.smartlogic.ses.client.*;
import com.smartlogic.ses.client.exceptions.SESException;

public class FilteredTermHints {
	
	public static void main(String[] args) throws SESException {
		
		SESClient sesClient = ConfigUtil.getSESClient();
		
		Date startCacheFetch = new Date();
		Map<String, Term> fordTerms = sesClient.getAllDescendants("Manufacturer_36", "Narrower Term");
		Date endCacheFetch = new Date();
		System.out.println(fordTerms.size() + " terms fetched in " + (endCacheFetch.getTime() - startCacheFetch.getTime()) + " ms");
		
		SESFilter sesFilter = new SESFilter();
		sesFilter.setPrefixResultsLimit(1000);
		sesFilter.setClasses(new String[] { "Model" } );
		Date startHintsFetch = new Date();
		Map<String, TermHint> termHints = sesClient.getTermHints("bus ", sesFilter);
		Date endHintsFetch = new Date();
		System.out.println(termHints.size() + " term hints fetched in " + (endHintsFetch.getTime() - startHintsFetch.getTime()) + " ms");
		
		Date startFiltering = new Date();
		Map<String, TermHint> filteredTermHints = new LinkedHashMap<String, TermHint>();
		for (String termHintId: termHints.keySet()) {
			if (fordTerms.containsKey(termHintId)) {
				filteredTermHints.put(termHintId, termHints.get(termHintId));
			}
		}
		Date endFiltering = new Date();
		System.out.println(filteredTermHints.size() + " filtered terms " + (startFiltering.getTime() - endFiltering.getTime()) + " ms");
		
		for (String termHintId: filteredTermHints.keySet()) {
			System.out.println(filteredTermHints.get(termHintId));
		}
		
		
		
	}

}
