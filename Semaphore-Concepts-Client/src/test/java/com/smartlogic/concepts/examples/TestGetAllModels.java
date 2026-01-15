package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Concept;
import com.smartlogic.concepts.client.beans.Model;

import java.util.List;
import java.util.Map;

public class TestGetAllModels extends AbstractTestRunner {

    public static void main(String[] args) throws ConceptsException {

        List<Model> models = getConceptsClient().getAllModels();

        System.out.println(models.get(0));
        System.out.println(models.get(1));

    }

}
