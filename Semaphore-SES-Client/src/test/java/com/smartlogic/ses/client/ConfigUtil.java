package com.smartlogic.ses.client;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigUtil {

  private static SESClient sesClient = getSESClient();
  protected static Map<String, Object> config;

  @SuppressWarnings("unchecked")
  public static void initConfig() {
    if (config != null)
      return;

    try {
      Yaml yaml = new Yaml();
      try (InputStream inputStream =
          ConfigUtil.class.getClassLoader().getResourceAsStream("default.config.yml")) {
        config = (Map<String, Object>) yaml.load(inputStream);
      }
    } catch (Exception e) {
      System.err.println("Failed to load config");
      System.err.println(e.toString());
    }
  }

  public static SESClient getSESClient() {
    if (sesClient != null)
      return sesClient;

    initConfig();

    SESClient client = new SESClient();

    client.setOntology((String) config.get("ses.ontology"));
    client.setHost((String) config.get("ses.host"));
    client.setPort((int) config.get("ses.port"));
    client.setPath((String) config.get("ses.path"));

    if (config.containsKey("ses.proxyHost")) {
      client.setProxyHost((String) config.get("ses.proxyHost"));
      client.setProxyPort((Integer) config.get("ses.proxyPort"));
    }
    return client;
  }
}
