

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationException;

abstract public class DirectoryParser {
	protected final static Log logger = LogFactory.getLog(DirectoryParser.class);
	
	
	protected ClassificationClient classificationClient;
	public DirectoryParser(ClassificationClient classificationClient) {
		this.classificationClient = classificationClient;
	}
	
	public void parseDirectory(String inputDirectoryName, String outputDirectoryName) throws Exception {
		File inputDirectory = new File(inputDirectoryName);
		if (!inputDirectory.exists()) throw new Exception("Input directory '" + inputDirectory.getAbsolutePath() + "' does not exist");
		if (!inputDirectory.isDirectory()) throw new Exception("Input directory '" + inputDirectory.getAbsolutePath() + "' is not a directory");
		
		File outputDirectory = new File(outputDirectoryName);
		if (!outputDirectory.exists()) throw new Exception("Output directory '" + outputDirectory.getAbsolutePath() + "' does not exist");
		if (!outputDirectory.isDirectory()) throw new Exception("Output directory '" + outputDirectory.getAbsolutePath() + "' is not a directory");
		
		parseDirectory(inputDirectory, outputDirectory);
	}

	public void parseDirectory(File inputDirectory, File outputDirectory) throws Exception {
		if ((inputDirectory == null) || (outputDirectory == null)) return;
		
		if (!outputDirectory.exists()) outputDirectory.mkdirs();
		if (!outputDirectory.isDirectory()) throw new Exception("Output directory '" + outputDirectory.getAbsolutePath() + "' is not a directory");
		
		File[] inputFiles = inputDirectory.listFiles();
		if (inputFiles == null) return;
		
		for (File inputFile: inputFiles) {
			File outputFile = new File(outputDirectory, inputFile.getName() + "." + getSuffix());
			if (inputFile.isDirectory()) {
				parseDirectory(inputFile,outputFile);
			} else {
				try {
					parseFile(inputFile, outputFile);
				} catch (Exception e) {
					logger.error("Error with file: '" + inputFile.getAbsolutePath() + "': " + e.getMessage());
				}
			}
		}
	}
	
	public abstract String getSuffix();
	
	public abstract void parseFile(File inputFile, File outputFile) throws ClassificationException, IOException;
}
