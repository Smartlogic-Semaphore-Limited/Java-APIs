package com.smartlogic.rdfdiff;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DiffToSparqlInsertUpdateBuilderTests {

  Model model;

  Resource newConceptRes;
  Resource foobarFooRes;

  Property prop1;
  Property prop2;
  Property prop3;
  Property prop4;
  Property prop5;
  Property prop6;

  SimpleDateFormat dtfmt = new SimpleDateFormat("yyyy-MM-dd");

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    model = ModelFactory.createDefaultModel();
    model.read("Playpen2.ttl", "TTL");

    prop1 = model.createProperty("http://foo.com/foo");
    prop2 = model.createProperty("http://foo.com/bar");
    prop3 = model.createProperty("http://foo.com/fasel");
    prop4 = model.createProperty("http://foo.com/fase2");
    prop5 = model.createProperty("http://foo.com/thisUri");
    prop6 = model.createProperty("http://foo.com/stringy");

    newConceptRes = model.createResource("http://example.com/Playpen2#newConcept");
    foobarFooRes = model.createResource("http://example/com/Playpen2#foobarFoo");

  }

  @After
  public void tearDown() throws Exception {
    model.close();
  }

  @Test
  public void testSimpleDiff() throws Exception {

    Model model2 = hydrateAndModifySecondModel();
    String sparql = DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(
        RDFDifferenceBuilder.buildDifference(model, model2));
    System.out.println(sparql);
    assertTrue(sparql.startsWith("DELETE"));

    Resource res2 = model2.createResource("http://example/com/Playpen2#foobarFoo");
    StmtIterator it = model2.listStatements(res2, prop1, (RDFNode) null);
    Statement stmt = it.nextStatement();
    assertTrue(stmt.getObject().asLiteral().getBoolean() == false);

    it = model2.listStatements(res2, prop2, (RDFNode) null);
    stmt = it.nextStatement();
    assertTrue(stmt.getObject().asLiteral().getLong() == 1);

    it = model2.listStatements(res2, prop3, (RDFNode) null);
    stmt = it.nextStatement();
    XSDDateTime date = (XSDDateTime) stmt.getObject().asLiteral().getValue();

    it = model2.listStatements(res2, prop4, (RDFNode) null);
    stmt = it.nextStatement();
    Literal lit = stmt.getObject().asLiteral();
    String val = lit.getLexicalForm();
    String dataType = lit.getDatatype().getURI();
    assertTrue(val.equals("2017-01-01"));
    assertTrue(dataType.equals(XSDDatatype.XSDdate.getURI()));

    it = model2.listStatements(res2, prop5, (RDFNode) null);
    stmt = it.nextStatement();
    assertTrue(stmt.getObject().asLiteral().getString().equals("http://www.google.com"));
    assertTrue(stmt.getObject().asLiteral().getDatatype().getURI().equals(XSDDatatype.XSDanyURI.getURI()));

  }

  @Test
  public void testConceptGroupedDiff() throws IOException {

    Model model2 = hydrateAndModifySecondModel();

    List<Property> chaseProperties = Lists.newArrayList();
    chaseProperties.add(SKOSXL.prefLabel);
    chaseProperties.add(SKOSXL.altLabel);
    Collection<RDFSubjectDifference> diffs = RDFDifferenceBuilder.buildSubjectBatches(
        RDFDifferenceBuilder.buildDifference(model, model2), chaseProperties);

    for (RDFSubjectDifference subjDiff : diffs) {
      String sparql = DiffToSparqlInsertUpdateBuilder.buildSparqlInsertUpdate(subjDiff);
      System.out.println("Batch for subject: " + subjDiff.subject.toString());
      System.out.println(sparql);
    }
  }

  public Model hydrateAndModifySecondModel() {
    Model model2 = ModelFactory.createDefaultModel();
    model2.add(model);

    StmtIterator it = model2.listStatements(newConceptRes, null, (RDFNode) null);
    List<Statement> toDelete = Lists.newArrayList();
    while (it.hasNext()) {
      Statement stmt = it.nextStatement();
      toDelete.add(stmt);
    }
    model2.remove(toDelete);

    Resource foobarFooRes = model2.createResource("http://example/com/Playpen2#foobarFoo");
    foobarFooRes.addProperty(RDF.type, SKOS.Concept);

    foobarFooRes.addLiteral(prop1, false);
    foobarFooRes.addLiteral(prop2, 1);
    Calendar c = Calendar.getInstance();
    c.set(Calendar.YEAR, 2016);
    c.set(Calendar.DAY_OF_MONTH, 3);
    c.set(Calendar.MONTH, 10);
    foobarFooRes.addLiteral(prop3, model.createTypedLiteral(dtfmt.format(c.getTime()), XSDDatatype.XSDdate));
    foobarFooRes.addProperty(prop4, "2017-01-01", XSDDatatype.XSDdate);
    foobarFooRes.addProperty(prop5, "http://www.google.com", XSDDatatype.XSDanyURI);

    model2.addLiteral(foobarFooRes, prop6, model.createLiteral("2017-02-22", "en"));

    return model2;
  }
}