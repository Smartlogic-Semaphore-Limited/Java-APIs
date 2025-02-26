package com.smartlogic.ontologyeditor.examples;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadOnly;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import com.smartlogic.ontologyeditor.beans.Task;

import java.io.IOException;

public class AddMultipleModelsAndTasks extends ModelManipulation {
    public static void main(String args[]) throws IOException, CloudException, OEClientException {
        runTests(new AddMultipleModelsAndTasks());
    }

    @Override
    protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {
        for (int i = 0; i < 200; i++) {
            Label modelLabel = new Label("", "Multiple Model " + i);
            String comment = "Model created for testing the Java OE Client API";

            String modelUri = "model:MultipleModel" + i;
            Model model = new Model(modelUri, modelLabel, comment);
            model.setDefaultNamespace("http://example.com/APITest#");
            oeClient.createModel(model);

            oeClient.setModelUri(modelUri);
            for (int j = 0; j < 10; j++) {
                Task task = new Task(new Label("en", "My task again " + i + " Task " + j));
                oeClient.createTask(task);
            }
        }
    }

}
