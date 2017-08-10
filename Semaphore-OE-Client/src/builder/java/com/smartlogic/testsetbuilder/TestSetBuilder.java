package com.smartlogic.testsetbuilder;

import java.util.ArrayList;
import java.util.List;

import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;

public class TestSetBuilder {

	public static void main(String[] args) {
		OEClientReadWrite oeClient = new OEClientReadWrite();
		oeClient.setBaseURL("http://localhost:8080/workbench-webapp-4.0.21.rc1/");
		oeClient.setModelUri("model:MultiLingual");
		oeClient.setToken("WyJBZG1pbmlzdHJhdG9yIiwxNDY5MTkzNDYyLCJNQ0VDRHdESzhnV29WeEkycVRjYnpLMkZKQUlPQmJId0NRUnBtZGNwQk5wUGFVYz0iXQ==");

		for (int i = 0; i < 4; i++) {
			String conceptSchemeUri = "http://example.com/MultiLingual#ConceptScheme/CS-" + i;
			List<Label> labelListCS = new ArrayList<Label>();
			labelListCS.add(new Label("en", "EN - CS-" + i));
			labelListCS.add(new Label("es", "ES - CS-" + i));
			labelListCS.add(new Label("fr", "FR - CS-" + i));
			if (i != 3) labelListCS.add(new Label("de", "DE - CS-" + i));
			ConceptScheme conceptScheme = new ConceptScheme(oeClient, conceptSchemeUri, labelListCS);
			oeClient.createConceptScheme(conceptScheme);

			for (int j = 0; j < 4; j++) {
				String conceptUri = "http://example.com/MultiLingual#Concept/C-" + i + "-" + j;
				List<Label> labelListC = new ArrayList<Label>();
				labelListC.add(new Label("en", "EN - C-" + i + "-" + j));
				labelListC.add(new Label("es", "ES - C-" + i + "-" + j));
				labelListC.add(new Label("fr", "FR - C-" + i + "-" + j));
				labelListC.add(new Label("de", "DE - C-" + i + "-" + j));
				Concept concept = new Concept(oeClient, conceptUri, labelListC);
				oeClient.createConcept(conceptSchemeUri, concept);
			}
		}

	}
}
