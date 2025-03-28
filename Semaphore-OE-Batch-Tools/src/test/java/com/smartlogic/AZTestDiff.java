package com.smartlogic;

import java.io.IOException;

public class AZTestDiff {

    public static void main(String[] args) throws OEConnectionException, IOException, InterruptedException {
        OEModelEndpoint devEndpoint = new OEModelEndpoint();
        devEndpoint.setBaseUrl("http://localhost:5080/");
        devEndpoint.setModelIRI("model:UnitDev");
        devEndpoint.setAccessToken("WyJHb2QiLDE3MzcwODIzMTYsWyJTZW1hcGhvcmVSb290cyIsIlNlbWFwaG9yZUF1dGhlbnRpY2F0ZWRVc2VycyIsIlNlbWFwaG9yZVN1cGVyQWRtaW5pc3RyYXRvcnMiLCJTZW1hcGhvcmVBZG1pbmlzdHJhdG9ycyJdLCJNRVFDSUFXWXU1cy9nQkdXam5ZdGFVSlhzcVdyNWlZa0M4ZFlLb1ZBT2o0TFpFVDlBaUJtdm4yVHlMeXNYV29XQVZydTdBL2xwRng2L1lwMzlDR0ZqditlRlpUeGJRPT0iXQ");

        OEBatchClient devClient = new OEBatchClient(devEndpoint);
        devClient.loadCurrentModelFromOE();
        System.out.println("Dev: " + devClient.getCurrentModel().size());

        OEModelEndpoint prodEndpoint = new OEModelEndpoint();
        prodEndpoint.setBaseUrl("http://localhost:5080/");
        prodEndpoint.setModelIRI("model:UnitProd");
        prodEndpoint.setAccessToken("WyJHb2QiLDE3MzcwODIzMTYsWyJTZW1hcGhvcmVSb290cyIsIlNlbWFwaG9yZUF1dGhlbnRpY2F0ZWRVc2VycyIsIlNlbWFwaG9yZVN1cGVyQWRtaW5pc3RyYXRvcnMiLCJTZW1hcGhvcmVBZG1pbmlzdHJhdG9ycyJdLCJNRVFDSUFXWXU1cy9nQkdXam5ZdGFVSlhzcVdyNWlZa0M4ZFlLb1ZBT2o0TFpFVDlBaUJtdm4yVHlMeXNYV29XQVZydTdBL2xwRng2L1lwMzlDR0ZqditlRlpUeGJRPT0iXQ");

        OEBatchClient prodClient = new OEBatchClient(prodEndpoint);
        prodClient.loadCurrentModelFromOE();
        System.out.println("Prod: " + prodClient.getCurrentModel().size());

        prodClient.getSparqlUpdateOptions().setAcceptWarnings(true);
        //destinationClient.getSparqlUpdateOptions().setRunCheckConstraints(false);
        //destinationClient.getSparqlUpdateOptions().setRunEditRules(false);

        prodClient.setPendingModel(devClient.getCurrentModel());
        prodClient.commit();



    }

}
