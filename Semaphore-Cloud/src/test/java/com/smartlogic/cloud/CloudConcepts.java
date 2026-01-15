package com.smartlogic.cloud;

import java.io.IOException;
import java.util.Map;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Concept;

/**
 * Example class showing how to use the Cloud API and the Semantic Enhancement Server API in the
 * same context
 *
 * The Cloud Configuration will need updating with the cloud settings available from the "Basic API
 * Interface" settings page of your cloud installation
 *
 * @author keith.atkins@smartlogic.com
 *
 */
public class CloudConcepts {

  public static void main(String[] args)
          throws ConceptsException, IOException, CloudException {

    // Create the Cloud Access API Token from the supplied key
    TokenFetcher tokenFetcher = new TokenFetcher(CloudConfiguration.get("tokenRequestURL"),
        CloudConfiguration.get("apiKey"));
    Token token = tokenFetcher.getAccessToken();

    // Create the Semantic Enhancement Server client
    try (ConceptsClient conceptsClient = new ConceptsClient()) {
      conceptsClient.setUrl(CloudConfiguration.get("conceptsUrl"));
      conceptsClient.setApiToken(token.getAccess_token());
      conceptsClient.setOntology("ContentIntelligence");


      // Fetch term details for particular term
      Map<String, Concept> concepts = conceptsClient.getAllConcepts();
      System.out.println(concepts.size());

    }
  }
}
