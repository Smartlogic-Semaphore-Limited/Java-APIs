package com.smartlogic.semaphoremodel;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class AbstractTest {

	public void dumpModel(SemaphoreModel semaphoreModel) throws FileNotFoundException {
		File targetDirectory = new File("target");
		targetDirectory.mkdirs();
		semaphoreModel.write(new File(targetDirectory, "ConceptModelDump.ttl"));
	}

}
