package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;

public class CommitTask extends ModelManipulation {

	public static void main(String[] args) throws IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Task task = new Task(new Label("en", "Task1"), "Task1", "task:Playpen2:Task1");

		oeClient.commitTask(task);
	}


}
