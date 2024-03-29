package com.smartlogic.cloud;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenFetcher {
  protected final Log logger = LogFactory.getLog(getClass());

  private final String tokenUrl;
  private final String key;

  public TokenFetcher(String tokenUrl, String key) {
    this.tokenUrl = tokenUrl;
    this.key = key;
  }

  private String proxyHost;

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  private int proxyPort;

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  private String proxyProtocol = "http";

  public String getProxyProtocol() {
    return proxyProtocol;
  }

  public void setProxyProtocol(String proxyProtocol) {
    this.proxyProtocol = proxyProtocol;
  }

  private int socketTimeoutMS = 10000;

  public int getSocketTimeoutMS() {
    return socketTimeoutMS;
  }

  public void setSocketTimeoutMS(int socketTimeoutMS) {
    this.socketTimeoutMS = socketTimeoutMS;
  }

  private int connectionTimeoutMS = 10000;

  public int getConnectionTimeoutMS() {
    return connectionTimeoutMS;
  }

  public void setConnectionTimeoutMS(int connectionTimeoutMS) {
    this.connectionTimeoutMS = connectionTimeoutMS;
  }

  private int connectionRequestTimeoutMS = 10000;

  public int getConnectionRequestTimeoutMS() {
    return connectionRequestTimeoutMS;
  }

  public void setConnectionRequestTimeoutMS(int connectionRequestTimeoutMS) {
    this.connectionRequestTimeoutMS = connectionRequestTimeoutMS;
  }

  /**
   * Get the access token
   *
   * @return
   * @throws CloudException
   */
  public Token getAccessToken() throws CloudException {
    logger.info("getAccessToken: '" + tokenUrl + "'");
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    setProxyHttpHost(clientBuilder);

    try (CloseableHttpClient httpClient = clientBuilder.build()) {
      {

        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("grant_type", "apikey"));
        postParams.add(new BasicNameValuePair("key", key));

        HttpPost postRequest = new HttpPost(tokenUrl);
        postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
        postRequest.setEntity(new UrlEncodedFormEntity(postParams));

        HttpResponse response = httpClient.execute(postRequest);
        if (response == null) {
          throw new CloudException("Null response from http client: " + tokenUrl);
        }
        if (response.getStatusLine() == null) {
          throw new CloudException("Null status line from http client: " + tokenUrl);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("Status: " + statusCode);

        HttpEntity entity = response.getEntity();
        if (entity == null) {
          throw new CloudException("Null response from Cloud Server");
        }

        if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
          throw new CloudException("Internal cloud server error: " + entity.toString());
        } else if (statusCode != HttpStatus.SC_OK) {
          throw new CloudException("HttpStatus: " +
              statusCode +
              " received from cloud server (" +
              entity.toString() +
              ")");
        }

        try (InputStream inputStream = entity.getContent()) {
          byte[] returnedData = IOUtils.toByteArray(inputStream);
          if (logger.isDebugEnabled()) {
            logger.debug("Reponse: " + new String(returnedData, "UTF-8"));
          }

          ObjectMapper mapper = new ObjectMapper();
          Token token = mapper.readValue(returnedData, Token.class);
          return token;
        }
      }
    } catch (Exception e) {
      String message = String.format("%s thrown fetching token: %s", e.getClass().getSimpleName(),
          e.getMessage());
      logger.error(message, e);
      throw new CloudException(message);
    }
  }

  private void setProxyHttpHost(HttpClientBuilder clientBuilder) {
    if (proxyHost != null && proxyPort != 0) {
      HttpHost proxy = new HttpHost(proxyHost, proxyPort);
      clientBuilder.setProxy(proxy);
    }
  }

}
