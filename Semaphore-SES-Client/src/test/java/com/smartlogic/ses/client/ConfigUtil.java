package com.smartlogic.ses.client;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static SESClient sesClient = getSESClient();

    public static SESClient getSESClient() {
        if (sesClient != null)
            return sesClient;

        SESClient client = new SESClient();
        Properties prop = new Properties();

        try (InputStream in = ConfigUtil.class.getClass().getResourceAsStream("/testconfig.properties")) {
            prop.load(in);

            client.setOntology(prop.getProperty("ontology"));
            client.setHost(prop.getProperty("host"));
            client.setPort(prop.getProperty("port"));
            client.setPath(prop.getProperty("path"));

            if (prop.containsKey("protocol"))
                client.setProtocol(prop.getProperty("protocol"));
            if (prop.containsKey("language"))
               client.setLanguage(prop.getProperty("language"));
            if (prop.containsKey("connectionTimeoutMs"))
                client.setConnectionTimeoutMS(Integer.parseInt(prop.getProperty("connectionTimeoutMs")));
            if (prop.containsKey("socketTimeoutMs"))
                client.setConnectionTimeoutMS(Integer.parseInt(prop.getProperty("socketTimeoutMs")));

            if (prop.containsKey("proxyHost"))
                client.setProxyHost(prop.getProperty("proxyHost"));
            if (prop.containsKey("proxyPort"))
                client.setProxyHost(prop.getProperty("proxyPort"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }
}
