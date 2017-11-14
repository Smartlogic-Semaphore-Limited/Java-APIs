package com.smartlogic.tools;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to load SKOS or SKOS-XL RDF files directing into TDB files used in OE.
 */
public class SKOSTDBLoader {

  static Logger logger = LoggerFactory.getLogger(SKOSTDBLoader.class);

  public static void main(String[] args) {

    if (args.length != 3) {
      System.err.println("Wrong arguments");
      showUsage();
    }
    System.out.println("Model file   : " + args[0]);
    System.out.println("Model URI    : " + args[1]);
    System.out.println("Model tdb dir: " + args[2]);

    Dataset dataset = TDBFactory.createDataset(args[2]);
    try {
      Model model = dataset.getNamedModel(args[1]);
      TDBLoader.loadModel(model, args[0]);
    } finally {
      dataset.close();
    }
  }

  public static void showUsage() {
    System.out.println("java -cp .. com.smartlogic.oelib.tools.SKOSTDBLoader <model file path> <named model name> <dataset directory>");
    System.out.println("example: java -cp .. com.smartlogic.oelib.tools.SKOSTDBLoader /my/models/MySKOS.ttl urn:x-evn-master:MyModel /opt/semaphore/oe_home/workspace/SemaphoreData/MyModel.tdb.data");
  }
}
