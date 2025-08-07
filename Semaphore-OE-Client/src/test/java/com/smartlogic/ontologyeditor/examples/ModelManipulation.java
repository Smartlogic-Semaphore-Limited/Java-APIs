package com.smartlogic.ontologyeditor.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.TokenFetcher;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadOnly;
import com.smartlogic.ontologyeditor.OEClientReadWrite;

public abstract class ModelManipulation {

  private static Collection<OEClientReadWrite> oeClients = null;

  public static Collection<OEClientReadWrite> getOEClients() throws IOException, CloudException {
    if (oeClients == null) {
      oeClients = new ArrayList<>();
//      oeClients.add(getOEClient("semaphore4.properties"));
      oeClients.add(getOEClient("semaphore5.properties"));
//      oeClients.add(getOEClient("cloud.properties"));
    }
    return oeClients;
  }

  protected static OEClientReadWrite getOEClient(String fileName)
      throws IOException, CloudException {
    Properties properties;
    try (FileInputStream propertiesInputStream = new FileInputStream(fileName)) {
      properties = new Properties();
      properties.load(propertiesInputStream);
    } catch (IOException e) {
      File file = new File(fileName);
      System.err.println("Error attempting to read properties from file " + file.getAbsolutePath());
      throw e;
    }

    OEClientReadWrite oeClient = new OEClientReadWrite();
    oeClient.setProxyAddress(properties.getProperty("proxy.address"));
    oeClient.setBaseURL(properties.getProperty("base.url"));
    oeClient.setModelUri(properties.getProperty("model.uri"));
    oeClient.setToken(properties.getProperty("token"));
    oeClient.setHeaderToken(properties.getProperty("header.token"));

    String tokenUrl = properties.getProperty("token.url");
    String tokenKey = properties.getProperty("token.key");
    if ((tokenUrl != null) && (tokenKey != null)) {
      TokenFetcher tokenFetcher = new TokenFetcher(tokenUrl, tokenKey);
      oeClient.setCloudToken(tokenFetcher.getAccessToken());
    }

    if (properties.getProperty("header.xuser") != null) {
      oeClient.setHeader("X-User", properties.getProperty("header.xuser"));
    }
    if (properties.getProperty("header.xrole") != null) {
      oeClient.setHeader("X-Role", properties.getProperty("header.xrole"));
    }
    return oeClient;
  }

  public static void runTests(ModelManipulation modelManipulation)
      throws IOException, CloudException, OEClientException {
    Collection<OEClientReadWrite> oeClients = getOEClients();
    for (OEClientReadWrite oeClient : oeClients) {
      modelManipulation.alterModel(oeClient);
    }
  }

  public String getModelName(OEClientReadOnly oeClient) {
    return oeClient.getModelUri().substring(oeClient.getModelUri().indexOf(":") + 1);
  }

  protected abstract void alterModel(OEClientReadWrite oeClient) throws OEClientException;

  protected String urlEncode(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.err.println("UnsupportedEncodingException - shouldn't get here");
      return "INVALID LABEL";
    }
  }

}
