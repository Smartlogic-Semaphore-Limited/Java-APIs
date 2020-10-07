package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;

public class GetAllConceptSchemes extends ModelManipulation {
  public static void main(String args[]) throws IOException, CloudException, OEClientException {
    runTests(new GetAllConceptSchemes());
  }

  @Override
  protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

    Collection<ConceptScheme> returnedConceptSchemes = oeClient.getAllConceptSchemes();
    for (ConceptScheme conceptScheme : returnedConceptSchemes) {
      System.out.println(conceptScheme);
    }
  }
}
