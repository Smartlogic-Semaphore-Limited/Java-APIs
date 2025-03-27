package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;

public class AddTask extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new AddTask());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Task task = new Task(new Label("en", "KRTTest"));

		// if you specify model:APITest as the model.uri, you can create this task here,
		// but to test KRT related stuff, need to enable a review in the task.
		oeClient.createTask(task);
	}


}
