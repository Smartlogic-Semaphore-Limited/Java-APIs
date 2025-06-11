package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.*;

public class TestMetaNode extends ClassificationTestCase {

    @Test
    public void testMetaNodeCreation() {
         MetaNode metaNode = new MetaNode("name", "value", "score", "id");
         assertNotNull(metaNode);
         assertEquals("name", metaNode.getName());
         assertEquals("value", metaNode.getValue());
         assertEquals("score", metaNode.getScore());
         assertEquals("id", metaNode.getId());
    }

    @Test
    public void testMetaNodeEquals() {
        MetaNode metaNodeA = new MetaNode("name", "value", "score", "id");
        MetaNode metaNodeB = new MetaNode("name", "value", "score", "id");
        assertEquals(0, metaNodeA.compareTo(metaNodeB));
        assertEquals(0, metaNodeB.compareTo(metaNodeA));
        MetaNode metaNodeC = new MetaNode("name", "value", "score", null);
        assertNotEquals(metaNodeC.compareTo(metaNodeA), 0);
        assertNotEquals(metaNodeC.compareTo(metaNodeB), 0);
        assertNotEquals(metaNodeA.compareTo(metaNodeC), 0);
        assertNotEquals(metaNodeB.compareTo(metaNodeC), 0);
    }

    @Test
    public void testMetaNodeAddMetaNode() {
        MetaNode m = new MetaNode("name", "value", "score", "id");

        try {
            String xmlString = readFileToString("src/test/resources/responses/csResponseSampleData.xml");
            var doc = XMLReader.getDocument(xmlString.getBytes(StandardCharsets.UTF_8));
            var nodeList = doc.getDocumentElement().getElementsByTagName("META");
            final Set<String> metaIds = new HashSet<>();
            final Set<String> returnedIds = new HashSet<>();
            final List<MetaNode> metaNodesList = new ArrayList<>();
            for (int i = 1; i < nodeList.getLength(); i++) {
                var elem = (Element)nodeList.item(i);
                if (elem.hasAttribute("id")) {
                    m.addMetaNode(elem);
                    metaIds.add(elem.getAttribute("id"));
                }
            }

            m.getMetaNodes().values().forEach( metaNodesCollection -> {
                metaNodesCollection.forEach(metaNode -> {
                    returnedIds.add(metaNode.getId());
                    metaNodesList.add(metaNode);
                });
            });

            assertTrue(metaIds.containsAll(returnedIds));
            assertTrue(returnedIds.containsAll(metaIds));
            Collections.sort(metaNodesList);
            metaNodesList.forEach(metaNode -> {
               assertTrue(returnedIds.contains(metaNode.getId()));
            });

        } catch (Exception e) {
            fail("Exception encountered: " + e.getMessage());
        }
    }
}
