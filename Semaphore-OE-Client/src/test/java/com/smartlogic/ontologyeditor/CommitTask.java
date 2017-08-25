package com.smartlogic.ontologyeditor;

import java.io.IOException;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;

public class CommitTask extends ModelManipulation {

	public static void main(String[] args) throws IOException {
		OEClientReadWrite oeClient = getOEClient(true);

		Task task = new Task(new Label("en", "My task again again"), "tag:MyTaskAgainAgain", "task:Playpen2:MyTaskAgainAgain");

		oeClient.commitTask(task);
	}


}
