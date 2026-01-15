package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Structure;


public class TestGetStructure extends AbstractTestRunner {

    public static void main(String[] args) throws ConceptsException {

        Structure structure = getConceptsClient().getStructure();

        System.out.println(structure);

    }

}
