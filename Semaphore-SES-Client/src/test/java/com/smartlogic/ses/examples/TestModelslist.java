package com.smartlogic.ses.examples;

import com.smartlogic.ses.client.Model;
import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.exceptions.SESException;

public class TestModelslist {

  public static void main(String[] args) throws SESException {
    try (SESClient sesClient = new SESClient()) {
      sesClient.setUrl("http://localhost:8983/ses");

      for (Model model : sesClient.listModels()) {
        System.out.println(model);
      }

    }
  }

}
