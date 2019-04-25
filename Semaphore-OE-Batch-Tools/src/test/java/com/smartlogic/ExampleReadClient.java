package com.smartlogic;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.Properties;

public class ExampleReadClient {

  public static void main(String[] args) {
    test1();
  }

  public static void test1() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("oeurl"));
      endpoint.setModelIRI("model:myExample");
      endpoint.setAccessToken(config.getProperty("accesstoken"));

      ResultSet rs = endpoint.runSparqlQuery("select ?s ?p ?o where { ?s ?p ?o . } LIMIT 100");
      while (rs.hasNext()) {
        QuerySolution sol = rs.next();
        System.out.println(sol.toString());
      }

      ResultSet rs2 = endpoint.runSparqlQuery("select ?s ?p ?o where { ?s ?p ?o . } LIMIT 100");
      while (rs2.hasNext()) {
        QuerySolution sol = rs2.next();
        System.out.println(sol.toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
