package com.smartlogic.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Created by stevenbiondi on 6/21/17.
 *
 * Diff two models at specified URLs.
 */
public class TTLDiff {

  static Logger logger = LoggerFactory.getLogger(TTLDiff.class);

  public static void main(String[] args) {


    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(getCmdOptions(), args);
      if (!FileUtils.isFile(cmd.getOptionValue("lf"))) {
        logger.error("Invalid left file: {}", cmd.getOptionValue("lf"));
        System.exit(-1);
      }
      if (!FileUtils.isFile(cmd.getOptionValue("rf"))) {
        logger.error("Invalid right file: {}", cmd.getOptionValue("rf"));
        System.exit(-1);
      }

      try (OutputStreamWriter osw = new OutputStreamWriter(
              cmd.hasOption("o") ?
                      new BufferedOutputStream(new FileOutputStream(cmd.getOptionValue("o"), false))
                      :
                      new BufferedOutputStream(System.out),
              StandardCharsets.UTF_8
              )) {

        Model model_left = RDFDataMgr.loadModel(cmd.getOptionValue("lf"));
        Model model_right = RDFDataMgr.loadModel(cmd.getOptionValue("rf"));

        Consumer<Statement> out;
        if (cmd.hasOption("sparql"))
          out = s -> JenaUtil.printSPARQLStatement(s, osw);
        else {
          out = s -> JenaUtil.printStatement(s, osw);
        }

        if (cmd.hasOption("sparql")) {
          osw.append("DELETE {\n");
        } else {
          osw.append("<\n");
        }

        model_left.listStatements().toList()
                .stream()
                .filter(stmt -> !model_right.contains(stmt))
                .forEach(out);
        if (cmd.hasOption("sparql"))
          osw.append("} WHERE {}\n");

        if (cmd.hasOption("sparql")) {
          osw.append("INSERT {\n");
        } else {
          osw.append("<");
        }
        model_right.listStatements().toList()
                .stream()
                .filter(stmt -> !model_left.contains(stmt))
                .forEach(out);
        if (cmd.hasOption("sparql"))
          osw.append("} WHERE {}\b");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Options getCmdOptions() {
    Options o = new Options();
    o.addOption("lf", true, "left file");
    o.addOption("rf", true, "right file");
    o.addOption("o", true, "out file");
    o.addOption("sparql", false, "output SPARQL insert/delete");
    return o;
  }
}
