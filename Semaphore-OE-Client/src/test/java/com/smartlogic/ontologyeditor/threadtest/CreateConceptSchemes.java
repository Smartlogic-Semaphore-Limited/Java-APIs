package com.smartlogic.ontologyeditor.threadtest;

import java.util.ArrayList;
import java.util.List;

import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class CreateConceptSchemes {

	public static void main(String[] args) {
		OEClientReadWrite oeClient = new OEClientReadWrite();
		oeClient.setBaseURL("http://localhost:8080/workbench-webapp-4.0.21.rc1/");
		oeClient.setModelUri("model:MultiLingual");
		oeClient.setToken("WyJBZG1pbmlzdHJhdG9yIiwxNDYwNTY0MzYxLCJNQ0VDRGp3M2I2Y0xwc0VRTGdzRkM3dE1BZzhBdjZ6T3pGa2JRR2hhNnB2bnhhVT0iXQ==");

		String conceptSchemeUri = "http://example.com/MultiLingual#ConceptScheme/Snazzy-new-concept-scheme";

		List<Label> labelList = new ArrayList<Label>();
		labelList.add(new Label("en", "Fluffy Dice"));

		labelList.add(new Label("es", "Dados esponjosos"));
		labelList.add(new Label("de", "Flauschige Würfel"));
		labelList.add(new Label("fr", "Dés duveteux"));
		ConceptScheme conceptScheme = new ConceptScheme(oeClient, conceptSchemeUri, labelList);

		oeClient.createConceptScheme(conceptScheme);

	}

}
