package com.smartlogic.ses.examples;

import java.util.Map;

import com.smartlogic.ses.client.ConfigUtil;
import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.SESFilter;
import com.smartlogic.ses.client.TermHint;
import com.smartlogic.ses.client.exceptions.SESException;

public class HintsFilteredByType {

  public static void main(String[] args) throws SESException {
    try (SESClient sesClient = ConfigUtil.getSESClient()) {

      Map<String, TermHint> unfilteredHints = sesClient.getTermHints("neil");
      for (TermHint termHint : unfilteredHints.values()) {
        System.out.println("Unfiltered: " +
            termHint.getName() +
            " (" +
            termHint.getValues().getValues().get(0).getValue() +
            ")");
      }

      SESFilter sesFilter = new SESFilter();
      sesFilter.setLabelTypes(new String[] { "COSPAR ID" });
      Map<String, TermHint> filteredHints1 = sesClient.getTermHints("1964", sesFilter);
      for (TermHint termHint : filteredHints1.values()) {
        System.out.println("Filtered 1: " +
            termHint.getName() +
            " (" +
            termHint.getValues().getValues().get(0).getValue() +
            ")");
      }

      sesFilter.setLabelTypes(new String[] { "Preferred Label" });
      Map<String, TermHint> filteredHints2 = sesClient.getTermHints("arms", sesFilter);
      for (TermHint termHint : filteredHints2.values()) {
        System.out.println("Filtered 2: " +
            termHint.getName() +
            " (" +
            termHint.getValues().getValues().get(0).getValue() +
            ")");
      }

      sesFilter.setLabelTypes(new String[] { "alternative label" });
      Map<String, TermHint> filteredHints3 = sesClient.getTermHints("neil al", sesFilter);
      for (TermHint termHint : filteredHints3.values()) {
        System.out.println("Filtered 3: " +
            termHint.getName() +
            " (" +
            termHint.getValues().getValues().get(0).getValue() +
            ")");
      }

    }
  }

}
