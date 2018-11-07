package com.smartlogic;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {

  public static Properties getConfig() {

    Properties props = new Properties();
    try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("testconfig.properties")) {
      props.load(input);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return props;
  }
}
