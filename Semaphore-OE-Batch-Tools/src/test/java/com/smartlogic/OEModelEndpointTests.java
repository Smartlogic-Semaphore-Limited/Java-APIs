package com.smartlogic;

import com.smartlogic.cloud.Token;
import com.sun.net.httpserver.HttpServer;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class OEModelEndpointTests {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testUrlEndpoints() throws Exception {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setModelIRI(URI.create("model:ModelID").toString());
    ep.setAccessToken("ACCESSTOKEN");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));

    ep.setBaseUrl("http://localhost:5080/");
    ep.setModelIRI("model:TestAnotherModelID");
    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql", ep.buildSPARQLUrl(null));
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql?async=true&runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl());
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.setAcceptWarnings(true);
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql?async=true&warningsAccepted=true&runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl(options));

    ep.setBaseUrl("http://myserver.mydomain.com:9999/");
    ep.setModelIRI("model:TestID");
    assertEquals("http://myserver.mydomain.com:9999/kmm/api/", ep.buildApiUrl().toString());
    assertEquals(
            "http://myserver.mydomain.com:9999/kmm/api/model:TestID/sparql", ep.buildSPARQLUrl(null));

    ep.setConnectTimeout(Duration.ofMinutes(11));
    assertEquals(ep.getConnectTimeout(), Duration.ofMinutes(11));
    ep.setConnectTimeout(Duration.ofMinutes(7));
    assertEquals(ep.getConnectTimeout(), Duration.ofMinutes(7));

    ep.setRequestTimeout(Duration.ofMinutes(33));
    assertEquals(ep.getRequestTimeout(), Duration.ofMinutes(33));
    ep.setRequestTimeout(Duration.ofMinutes(7));
    assertEquals(ep.getRequestTimeout(), Duration.ofMinutes(7));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildApiUrlRejectsUnsupportedScheme() {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setBaseUrl("file:///tmp");

    ep.buildApiUrl();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetJobStatusRejectsCallbackFromDifferentOrigin() {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setBaseUrl("http://localhost:5080");

    ep.getJobStatus("http://127.0.0.1:5080/kmm/api/async/jobs/job-1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetJobStatusRejectsCallbackOutsideJobEndpoint() {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setBaseUrl("http://localhost:5080");

    ep.getJobStatus("http://localhost:5080/kmm/api/admin");
  }

  /**
   * Evidence for proposed false-positive triage of Polaris SSRF issues:
   * 9525EE0978929E10274EA552BEAB6E54 (initiateExportAsyncDownload),
   * 90EB68617051B52F051D3A54110E9C54 (getJobStatus),
   * 94AB9EF7931401D074619B9014ABBEFF (fetchData),
   * 727567720093618173B2E9BF17DC18CF (getJobResult).
   * Their traces taint the request builder via Authorization, not the URI.
   */
  @Test
  public void testCloudAuthorizationCannotChangeAsyncRequestDestination() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    List<String> paths = new CopyOnWriteArrayList<>();
    List<String> authorizationHeaders = new CopyOnWriteArrayList<>();
    server.createContext("/", exchange -> {
      String path = exchange.getRequestURI().getPath();
      paths.add(path);
      authorizationHeaders.add(exchange.getRequestHeaders().getFirst("Authorization"));
      String body;
      int status;
      if ("/kmm/api/".equals(path)) {
        status = 202;
        body = "{\"status\":\"ACCEPTED\",\"jobId\":\"job-1\"}";
      } else if ("/kmm/api/async/jobs/job-1".equals(path)) {
        status = 200;
        body = "{\"status\":\"FINISHED\"}";
      } else {
        status = 200;
        body = "<urn:subject> <urn:predicate> <urn:object> .";
      }
      byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
      try {
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
      } finally {
        exchange.close();
      }
    });
    server.start();
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      // A URL-looking token is still header data, not a request destination.
      String tokenValue = "http://127.0.0.1:1/unintended";
      OEModelEndpoint ep = cloudEndpoint(baseUrl, tokenValue);
      assertEquals("job-1", ep.initiateExportAsyncDownload());
      assertEquals("FINISHED", ep.getJobStatus(ep.getJobCallbackUrl("job-1")));
      org.apache.jena.rdf.model.Model model = ep.fetchData(null);
      try {
        assertEquals(1, model.size());
      } finally {
        model.close();
      }
      assertEquals(Integer.valueOf(200), ep.getJobResult("job-1").httpStatusCode());

      assertEquals(List.of("/kmm/api/", "/kmm/api/async/jobs/job-1",
          "/kmm/api/", "/kmm/api/async/jobs/job-1",
          "/kmm/api/async/jobs/job-1/result", "/kmm/api/async/jobs/job-1/result"), paths);
      assertEquals(6, authorizationHeaders.size());
      for (String header : authorizationHeaders) {
        assertEquals("bearer " + tokenValue, header);
      }
    } finally {
      server.stop(0);
    }
  }

  /**
   * Covers the same four Polaris IDs above: CRLF in Authorization is rejected
   * before any request reaches the server.
   */
  @Test
  public void testCloudAuthorizationRejectsHeaderInjectionBeforeSending() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    List<URI> requests = new CopyOnWriteArrayList<>();
    server.createContext("/", exchange -> {
      requests.add(exchange.getRequestURI());
      exchange.sendResponseHeaders(500, -1);
      exchange.close();
    });
    server.start();
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      OEModelEndpoint ep = cloudEndpoint(baseUrl, "token\r\nHost: unintended.example");
      assertThrows(IllegalArgumentException.class, ep::initiateExportAsyncDownload);
      RuntimeException statusError = assertThrows(RuntimeException.class,
          () -> ep.getJobStatus(ep.getJobCallbackUrl("job-1")));
      assertTrue(statusError.getCause() instanceof IllegalArgumentException);
      assertThrows(IllegalArgumentException.class, () -> ep.fetchData(null));
      RuntimeException resultError = assertThrows(RuntimeException.class, () -> ep.getJobResult("job-1"));
      assertTrue(resultError.getCause() instanceof IllegalArgumentException);
      assertTrue(requests.isEmpty());
    } finally {
      server.stop(0);
    }
  }

  private OEModelEndpoint cloudEndpoint(String baseUrl, String tokenValue) {
    Token token = new Token();
    token.setAccess_token(tokenValue);
    token.setExpires_in(7200);
    OEModelEndpoint ep = new OEModelEndpoint() {
      @Override
      public Token getCloudToken() {
        return token;
      }
    };
    ep.setBaseUrl(baseUrl);
    ep.setModelIRI("model:TestModel");
    ep.setCloudAPIKey("test-api-key");
    return ep;
  }

  @Test
  public void testBadSparql() {
    String sparql = "SELECT { AA";
    try {
      QueryFactory.create(sparql);
      Assert.fail("Failed to detect bad SPARQL query");
    } catch (Exception e) {}

    try {
      UpdateAction.parseExecute(sparql, ModelFactory.createDefaultModel());
      Assert.fail("Failed to detect bad SPARQL update");
    } catch (Exception e) {}
  }

  @Test
  public void testCloudSparql() {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setModelIRI(URI.create("model:ModelID").toString());
    ep.setCloudTokenFetchUrl("https://cloud.smartlogic.com/token");
    ep.setCloudAPIKey("my-api-key");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("my-api-key", ep.getCloudAPIKey());
    assertEquals("https://cloud.smartlogic.com/token", ep.getCloudTokenFetchUrl());
    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&runEditRules=true&checkConstraints=true", ep.buildSPARQLUrl());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.acceptWarnings = false;
    options.runCheckConstraints = true;
    options.runEditRules = false;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));
    options.acceptWarnings = true;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&warningsAccepted=true&runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));

  }
}
