package com.smartlogic.cloud;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;

import com.smartlogic.classificationserver.client.Body;
import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationConfiguration;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationScore;
import com.smartlogic.classificationserver.client.Result;
import com.smartlogic.classificationserver.client.Title;

/**
 * Example class showing how to use the Cloud API and the Classification API in the same context
 * 
 * The Cloud Configuration will need updating with the cloud settings available from the "Basic API Interface" settings page 
 * of your cloud installation
 * 
 * @author keith.atkins@smartlogic.com
 *
 */
public class CloudClassification {

	public static void main(String[] args) throws ClientProtocolException, IOException, CloudException, ClassificationException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		// Create the Cloud Access API Token from the supplied key
		TokenFetcher tokenFetcher = new TokenFetcher(CloudConfiguration.get("tokenRequestURL"), CloudConfiguration.get("apiKey"));
		Token token = tokenFetcher.getAccessToken();

		// Create the Classification Server client 
		ClassificationClient classificationClient = new ClassificationClient();
		ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
		classificationConfiguration.setUrl(CloudConfiguration.get("csUrl"));
		classificationConfiguration.setApiToken(token.getAccess_token());
		classificationClient.setClassificationConfiguration(classificationConfiguration);
		
//		classificationClient.setProxyHost("localhost");
//		classificationClient.setProxyPort(8888);
		
		// Classify a document stored as a file
		Result result = classificationClient.getClassifiedDocument(new File("./data/SampleData.txt"), null);
		for (Entry<String, Collection<ClassificationScore>> entry: result.getAllClassifications().entrySet()) {
			System.out.println(entry.getKey() + ":");
			for (ClassificationScore classificationScore: entry.getValue()) {
				System.out.println(String.format(" %s %s %s %f",
						classificationScore.getRulebaseClass(),
						classificationScore.getId(),
						classificationScore.getName(),
						classificationScore.getScore()
						));
			}
		}
		System.out.println();

		// Classify a document created from a body and a title string
		Result result2 = classificationClient.getClassifiedDocument(new Body("This is a document about Renault"), new Title("This is a front doucment about cars and Money"));
		for (Entry<String, Collection<ClassificationScore>> entry: result2.getAllClassifications().entrySet()) {
			System.out.println(entry.getKey() + ":");
			for (ClassificationScore classificationScore: entry.getValue()) {
				System.out.println(String.format(" %s %s %s %f",
						classificationScore.getRulebaseClass(),
						classificationScore.getId(),
						classificationScore.getName(),
						classificationScore.getScore()
						));
			}
		}
	}
}
