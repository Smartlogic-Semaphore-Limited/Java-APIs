package com.smartlogic.ses.examples;

import java.util.Map;

import com.smartlogic.ses.client.ConfigUtil;
import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.SESFilter;
import com.smartlogic.ses.client.Term;
import com.smartlogic.ses.client.exceptions.SESException;

public class TermFromNameFilteredByType {

  public static void main(String[] args) throws SESException {
    try (SESClient sesClient = ConfigUtil.getSESClient()) {

      Map<String, Term> unfilteredTerms = sesClient.getTermDetailsByName("Gemini 1");
      for (Term term : unfilteredTerms.values()) {
        System.out.println("Unfiltered: " + term.getName());
      }

      SESFilter sesFilter = new SESFilter();
      sesFilter.setLabelTypes(new String[] { "COSPAR ID" });
      Map<String, Term> filteredTerms1 = sesClient.getTermDetailsByName("Gemini 1", sesFilter);
      for (Term term : filteredTerms1.values()) {
        System.out.println("Filtered 1: " + term.getName());
      }

      sesFilter.setLabelTypes(new String[] { "Preferred Label" });
      Map<String, Term> filteredTerms2 = sesClient.getTermDetailsByName("Gemini 1", sesFilter);
      for (Term term : filteredTerms2.values()) {
        System.out.println("Filtered 2: " + term.getName());
      }
      sesFilter.setLabelTypes(new String[] { "alternative label" });
      Map<String, Term> filteredTerms3 =
          sesClient.getTermDetailsByName("Acceleration Covenant", sesFilter);
      for (Term term : filteredTerms3.values()) {
        System.out.println("Filtered 3: " + term.getName());
      }

    }
  }

}
