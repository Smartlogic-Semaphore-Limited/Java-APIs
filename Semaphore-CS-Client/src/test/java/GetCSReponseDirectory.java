
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationConfiguration;
import com.smartlogic.classificationserver.client.ClassificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCSReponseDirectory extends DirectoryParser {
	protected static final Logger logger = LoggerFactory.getLogger(GetCSReponseDirectory.class);

	public GetCSReponseDirectory(ClassificationClient classificationClient) {
		super(classificationClient);
	}

	public static void main(String[] args) {

		try (ClassificationClient classificationClient = new ClassificationClient()) {
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

			DirectoryParser directoryParser = new GetCSReponseDirectory(classificationClient);
			try {
				directoryParser.parseDirectory(args[1], args[2]);
			} catch (Exception e) {
				System.err.println("Exception encountered: " + e.getMessage());
			}
		}
	}

	public String getSuffix() {
		return "xml";
	}

	public void parseFile(File inputFile, File outputFile) throws ClassificationException, IOException {
		logger.debug("Treating file: '" + inputFile + "'");

		byte[] returnedData = classificationClient.getClassificationServerResponse(inputFile, null);
		if (returnedData != null) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
				fileOutputStream.write(returnedData);
				fileOutputStream.close();
			}
		}
	}
}
