# The Ontology Editor API #

The Ontology Editor currently consists of two main services classes (in com.smartlogic.ontologyeditor)
- OEClientReadOnly - responsible for getting data out of Ontology Server into the Java Application.
- OEClientReadWrite - responsible for uploading data from the Java Application into Ontology Server.

All manipulation of the data within the Ontology Editor model is done using methods of OEClientReadWrite
(so adding metadata to a concept is done using the method oeClientReadWrite.createMeadata(....) not a method of the concept).

To get data out of the model methods of the form OEClientReadOnly.get.... are used. However, if you have a concept in the Java application and wish to load its metadata then you call the OEClientReadOnly.populate... methods. So oeClientReadOnly.populateMetadata(<metadata uri>, Concept) will populate the metadata of the supplied concept with the values currently in the model.

Examples of how to use all these methods are present in the src/test/java. Notice that all the examples use a subclass of ModelManipulation to create the OE client.

This requires the presence of the file "config.properties" (not supplied) that contains the properties given with example values below. If a proxy is not used, then the proxy address is not required.


proxy.address=http://localhost:8888
base.url=http://localhost:8080/workbench-webapp-4.1.2.rc1

model.uri=model:Playpen2
token=WyJBZG1pbmlzdHJhdG9yIiwxNTAyNDU3Njc5LCJNQ0VDRGkyelpXRjZ6MTdVbmIxcjk5L0RBZzhBMGROOWhjUm5uQWMvMk40RjFSST0iXQ==

 