package com.smartlogic.ses.examples;

import java.util.Map;

import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.Term;
import com.smartlogic.ses.client.exceptions.SESException;

public class TestTermByName {

  public static void main(String[] args) throws SESException {
    SESClient sesClient = new SESClient();
    sesClient.setUrl("http://localhost:8983/ses");
    sesClient.setOntology("SpaceMissions");

    Map<String, Term> terms = sesClient.getTermDetailsByName("Mr. Armstrong");
    for (Map.Entry<String, Term> entry : terms.entrySet()) {
      System.out.println(entry.getValue().getName() + "   " + entry.getValue().getURI());
    }

    sesClient.close();

  }

}
