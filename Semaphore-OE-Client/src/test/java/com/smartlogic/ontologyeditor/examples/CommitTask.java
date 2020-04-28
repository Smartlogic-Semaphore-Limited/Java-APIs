package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;

public class CommitTask extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new CommitTask());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Task task = new Task(new Label("en", "My task again again"), "MyTaskAgainAgain", "task:" + getModelName(oeClient) + ":MyTaskAgainAgain");

		oeClient.commitTask(task, new Label("en", "Commit label"), "A comment about the commit");
	}

}
