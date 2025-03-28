package com.smartlogic.cloud;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.time.temporal.ChronoUnit.SECONDS;

public class TokenFetcher {
  private Logger logger = LoggerFactory.getLogger(TokenFetcher.class);

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

  protected HttpClient.Builder httpClientBuilder;

  private synchronized HttpClient.Builder  getHttpClientBuilder() {
    if (httpClientBuilder == null) {
      httpClientBuilder = HttpClient.newBuilder();
      httpClientBuilder.connectTimeout(Duration.of(60, SECONDS));

      if (proxyHost != null && proxyPort != 0) {
        httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)));
      }
    }
    return httpClientBuilder;
  }


  /**
   * Get the access token
   *
   * @return
   * @throws CloudException
   */
  public Token getAccessToken() throws CloudException {
    logger.info("getAccessToken: '" + tokenUrl + "'");

    try {
      HttpClient httpClient = getHttpClientBuilder().build();

      String formData = "grant_type=apikey&key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);

      HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(formData)).uri(URI.create(tokenUrl)).build();

        HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
          throw new CloudException(String.format("Status %d returned by token fetcher", response.statusCode()));
        }

        return (new ObjectMapper()).readValue(response.body(), Token.class);

      } catch (Exception e) {
        String message = String.format("%s thrown fetching token: %s", e.getClass().getSimpleName(),
                e.getMessage());
        logger.error(message, e);
        throw new CloudException(message);
      }
    }

}
