
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationConfiguration;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryClassifier extends DirectoryParser {
	protected static final Logger logger = LoggerFactory.getLogger(DirectoryClassifier.class);

	public DirectoryClassifier(ClassificationClient classificationClient) {
		super(classificationClient);
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage \"Directory Classifier inputDirectory outputDirectory\" ");
			System.exit(0);
		}

		ClassificationClient classificationClient = new ClassificationClient();
//		classificationClient.setProxyHost("localhost");
//		classificationClient.setProxyPort(8888);

		ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
		classificationConfiguration.setProtocol("http");
		classificationConfiguration.setHostName("127.0.0.1");
		classificationConfiguration.setHostPort(5058);
		classificationConfiguration.setHostPath("/index.html");
		classificationConfiguration.setSingleArticle(true);
		classificationConfiguration.setMultiArticle(false);

		Map<String, String> additionalParameters = new HashMap<String, String>();
		additionalParameters.put("threshold", "1");
		additionalParameters.put("language", "en1");
		classificationConfiguration.setAdditionalParameters(additionalParameters);
		classificationClient.setClassificationConfiguration(classificationConfiguration);

		UUID uuid = new UUID((new Date()).getTime(), 14L);
		classificationClient.setAuditUUID(uuid);
		System.out.println(uuid.toString());

		DirectoryParser directoryParser = new DirectoryClassifier(classificationClient);
		try {
			directoryParser.parseDirectory(args[0], args[1]);
		} catch (Exception e) {
			System.err.println("Exception encountered: " + e.getMessage());
		}
	}

	public String getSuffix() {
		return "txt";
	}

	public void parseFile(File inputFile, File outputFile) throws ClassificationException, IOException {
		logger.debug("Treating file: '" + inputFile + "'");

		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient
				.getClassifiedDocument(inputFile, null).getAllClassifications();

		try (FileWriter fileWriter = new FileWriter(outputFile)) {
			for (String rulebaseClass : classificationScores.keySet()) {
				fileWriter.write(rulebaseClass + "\r\n");
				for (ClassificationScore classificationScore : classificationScores.get(rulebaseClass)) {
					fileWriter.write(classificationScore.getName() + ":" + classificationScore.getScore() + "\r\n");
				}
			}
		}
	}
}
