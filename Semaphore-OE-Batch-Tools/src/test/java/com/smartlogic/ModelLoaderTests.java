package com.smartlogic;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

public class ModelLoaderTests {
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
  public void testLoads() throws Exception {

    // cleanup earlier run. Can't delete dirs on windows after use.
    FileUtils.deleteDirectory(new File("tempTDB"));

    Model m = ModelLoader.loadModelToMem("Playpen2.ttl");
    assertTrue(m != null);
    Model mc = ModelLoader.loadModelToMem(m);
    assertTrue(mc != null);

    Model mtdb = ModelLoader.loadModelToTdb("Playpen2.ttl", "urn:mymodel", "tempTDB");
    assertTrue(mtdb != null);
    StmtIterator it = mtdb.listStatements();
    int nStmts = 0;
    while (it.hasNext()) {
      System.out.println(it.nextStatement().toString());
      nStmts++;
    }
    // this will fail on windows
    FileUtils.deleteDirectory(new File("tempTDB"));
    assertTrue(nStmts > 0);
  }
}
