package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class  AbstractTestRunner {
    protected static ConceptsClient getConceptsClient() {
        Properties properties;

        String fileName = "Semaphore-Concepts-Client/src/test/resources/concepts.properties";
        try (FileInputStream propertiesInputStream = new FileInputStream(fileName)) {
            properties = new Properties();
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            File file = new File(fileName);
            System.err.println("Error attempting to read properties from file " + file.getAbsolutePath());
            throw new RuntimeException(e);
        }

        ConceptsClient conceptsClient= new ConceptsClient();
        conceptsClient.setUrl(properties.getProperty("concepts.url"));
        conceptsClient.setOntology(properties.getProperty("model.name"));

//        if (properties.getProperty("token.apiKey") != null) {
//            TokenFetcher tokenFetcher = new TokenFetcher(properties.getProperty("token.url"),properties.getProperty("token.apiKey"));
//
//            try {
//                Token token = tokenFetcher.getAccessToken();
//                conceptsClient.setApiToken(token.getAccess_token());
//            } catch (CloudException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return conceptsClient;
    }
}