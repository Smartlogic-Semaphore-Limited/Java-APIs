package com.smartlogic.ontologyeditor;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import com.smartlogic.ontologyeditor.beans.Task;

public class GetAllTasks extends ModelManipulation {
	public static void main(String args[]) throws URISyntaxException, OEClientException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		Collection<Task> tasks = oeClient.getAllTasks();
		for (Task task: tasks) {
			System.out.println(task);
		}

		System.out.println(String.format("%d tasks returned", tasks.size()));
	}

}
