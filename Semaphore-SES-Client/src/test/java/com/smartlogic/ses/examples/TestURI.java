package com.smartlogic.ses.examples;

import java.util.Map;

import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.SESFilter;
import com.smartlogic.ses.client.Term;
import com.smartlogic.ses.client.TermHint;
import com.smartlogic.ses.client.builders.SESFilterBuilder;
import com.smartlogic.ses.client.exceptions.SESException;

public class TestURI {

  public static void main(String[] args) throws SESException {
    SESClient sesClient = new SESClient();
    sesClient.setUrl("http://localhost:8983/ses");
    sesClient.setOntology("GeographicLocations");

    Map<String, TermHint> termHints = sesClient.getTermHints("liv");
    for (Map.Entry<String, TermHint> entry : termHints.entrySet()) {
      System.out.println(entry.getValue().getName() + "   " + entry.getValue().getUri());
    }

    Term term = sesClient.getTermDetails("22590f48-7a33-3696-8ca6-34401e11e674");
    System.out.println(term.getName() + "   " + term.getURI());

    Term liverpool =
        sesClient.getTermDetailsByURI("http://models.smartlogic.com/GeographicLocations/2644210");
    System.out.println(liverpool.getName() + "   " + liverpool.getURI());

    SESFilter sesFilter = new SESFilterBuilder()
        .uris(new String[] { "http://models.smartlogic.com/GeographicLocations/2644210" }).build();
    Map<String, Term> nextLiverpools = sesClient.getAllTerms(sesFilter);
    for (Term nextLiverpool : nextLiverpools.values()) {
      System.out.println(nextLiverpool.getName() + "   " + nextLiverpool.getURI());
    }
  }

}
