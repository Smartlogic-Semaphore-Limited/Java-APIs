package com.smartlogic.ses.examples;

import com.smartlogic.ses.client.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoadTightLoopExample {

    private static final int NUM_THREADS = 30;
    private static final int MAX_LOOPS = 10;

    public static void main(String[] args) {

        try (SESClient client = ConfigUtil.getSESClient()) {

            Thread[] threads = new Thread[NUM_THREADS];
            {
                for (int i = 0; i < NUM_THREADS; i++) {
                    threads[i] = new Thread(() -> {
                        for ( int j = 0; j <= MAX_LOOPS; j++)
                            performWork(client);
                    });
                    threads[i].start();
                }
            }

            for (int i = 0; i < NUM_THREADS; i++) {
                threads[i].join();
            }

            System.out.println("Test completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void performWork(SESClient client) {

        client.setMaxConnections(NUM_THREADS + 2);

        Set<Term> allTerms = new HashSet<>();
        try {
            VersionInfo vInfo = client.getVersion();
            System.out.println("Version: " + vInfo.getVersion());

            for (Model m : client.listModels()) {
                System.out.println("Model: " + m.getName());
            }

            Map<String, TermHint> hints = client.getTermHints("bus");
            System.out.println("Hints: (bus)");
            for (TermHint hint : hints.values())
                System.out.println(hint.toString());

            System.out.println("Mapped concept for 'Business':");
            Map<String, Term> concepts = client.getMappedConcepts("Business");
            for (Term t : concepts.values()) {
                System.out.println("  Mapped concept   : " + t.getName().getValue());
                System.out.println("  Mapped concept id: " + t.getId().getValue());
            }

            Map<String, Term> rootTerms = client.browse(null);
            for (Term t : rootTerms.values()) {
                allTerms.add(t);
                System.out.println("Top concept: " + t.getName().getValue());
                Map<String, Term> childTerms = client.browse(t.getId().getValue());
            }

            int loopCounter = 0;
            for (Term t : allTerms) {
                if (loopCounter++ > 10)
                    break;
                Term tempTerm = client.getTermDetails(t.getId().getValue(), SESClient.DetailLevel.FULL);
                System.out.println("Term details: " + t.getId().getValue());
                System.out.println(tempTerm.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
