package com.smartlogic.cloud;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.smartlogic.ses.client.SESClient;
import com.smartlogic.ses.client.Term;
import com.smartlogic.ses.client.exceptions.SESException;

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
public class CloudSemanticEnhancement {

  public static void main(String[] args)
      throws SESException, KeyManagementException,
      NoSuchAlgorithmException, KeyStoreException, IOException, CloudException {

    // Create the Cloud Access API Token from the supplied key
    TokenFetcher tokenFetcher = new TokenFetcher(CloudConfiguration.get("tokenRequestURL"),
        CloudConfiguration.get("apiKey"));
    tokenFetcher.setProxyHost("localhost");
    tokenFetcher.setProxyPort(8888);
    Token token = tokenFetcher.getAccessToken();

    // Create the Semantic Enhancement Server client
    try (SESClient sesClient = new SESClient()) {
      sesClient.setUrl(CloudConfiguration.get("sesUrl"));
      sesClient.setApiToken(token.getAccess_token());
      sesClient.setOntology("IPSV");

      sesClient.setProxyHost("localhost");
      sesClient.setProxyPort(8888);

      // Fetch term details for particular term
      Map<String, Term> terms = sesClient.getAllTerms();
      System.out.println(terms.size());

    }
  }
}
