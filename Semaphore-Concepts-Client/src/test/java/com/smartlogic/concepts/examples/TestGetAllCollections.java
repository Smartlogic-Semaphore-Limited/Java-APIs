package com.smartlogic.concepts.examples;

import com.smartlogic.concepts.client.ConceptsClient;
import com.smartlogic.concepts.client.ConceptsException;
import com.smartlogic.concepts.client.beans.Collection;
import com.smartlogic.concepts.client.beans.Concept;

import java.util.Map;

public class TestGetAllCollections extends AbstractTestRunner{

    public static void main(String[] args) throws ConceptsException {

        Map<String, Collection> collections = getConceptsClient().getAllCollections();

        System.out.println(collections.get("1ae654ea-7b62-4dcd-a727-570415df9299"));

    }

}
