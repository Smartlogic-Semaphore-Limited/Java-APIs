package com.smartlogic.ontologyeditor.examples;


import java.io.IOException;
import java.util.Collection;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Task;

public class GetAllTasks extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetAllTasks());
	}
	
	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		Collection<Task> tasks = oeClient.getAllTasks();
		for (Task task: tasks) {
			System.err.println(task);
		}

		System.err.println(String.format("%d tasks returned", tasks.size()));
	}

}
