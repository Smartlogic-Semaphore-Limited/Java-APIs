package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Collection;
import com.smartlogic.concepts.client.beans.Concept;
import com.smartlogic.concepts.client.beans.ConceptScheme;

import java.util.Map;

public class TestGetAllConceptSchemes extends AbstractTestRunner {

    public static void main(String[] args) throws ConceptsException {

        Map<String, ConceptScheme> collections = getConceptsClient().getAllConceptSchemes();

        System.out.println(collections.get("bfc33274-197f-4873-a1f4-f421c8ab64aa"));
    }

}
