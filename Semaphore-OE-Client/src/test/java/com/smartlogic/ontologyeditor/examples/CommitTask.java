package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.util.*;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;

public class CommitTask extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new CommitTask());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Task task = new Task(new Label("en", "MyTaskTestCommit"));
		oeClient.createTask(task);
		Collection<Task> tasks = oeClient.getAllTasks();

		Optional<Task> findResult = tasks.stream()
				.filter((task1) -> task1.getLabel().getValue().equals("MyTaskTestCommit")).findFirst();
		if (findResult.isEmpty()) {
			throw new OEClientException("Newly created task not found!");
		}
		task = findResult.get();

		String modelUri = oeClient.getModelUri();
		oeClient.setModelUri(task.getGraphUri());

		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("en", "Task created Concept Scheme"));

		ConceptScheme conceptScheme = new ConceptScheme(oeClient, "http://example.com/APITest#taskConceptScheme" +
				new Random().nextLong(),
				labels);

		oeClient.createConceptScheme(conceptScheme);

		oeClient.setModelUri(modelUri);

		oeClient.commitTask(task, new Label("en", "Commit label"), "A comment about the commit");
	}

}
