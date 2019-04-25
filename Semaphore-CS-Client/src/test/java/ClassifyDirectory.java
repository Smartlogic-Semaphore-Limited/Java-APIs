import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationConfiguration;
import com.smartlogic.classificationserver.client.Title;

public class ClassifyDirectory {

	public static void main(String[] args) {
		File directory = new File(args[0]);

		try (ClassificationClient classificationClient = new ClassificationClient()) {
			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setHostName("localhost");
			classificationConfiguration.setHostPort(5058);
			classificationConfiguration.setSingleArticle(true);
			classificationClient.setClassificationConfiguration(classificationConfiguration);
			Map<String, String> additionalParameters = new HashMap<String, String>();
			additionalParameters.put("debug", "Timings");
			classificationConfiguration.setAdditionalParameters(additionalParameters);
			classificationConfiguration.setMultiArticle(true);

			UUID uuid = UUID.fromString("0000013d-34cb-7bf7-1145-7e371ef38400");
			classificationClient.setAuditUUID(uuid);
			File[] filesToClassify = directory.listFiles();
			if (filesToClassify == null)
				return;
			for (File file : filesToClassify) {
				try {
					System.out.println("Treating file: " + file.getAbsolutePath());

					String filePath = file.getAbsolutePath();
					filePath = filePath.substring(49);
					filePath = filePath.replace('\\', '/');
					System.out.println(filePath);
					Map<String, Collection<String>> metas = new HashMap<String, Collection<String>>();
					List<String> linkedList = new LinkedList<String>();
					linkedList.add(filePath);
					metas.put("ORIGINALURI", linkedList);

					classificationClient.getClassifiedDocument(file, null, new Title(file.getName()), metas);
				} catch (Exception e) {
					System.out.println("Exception occurred: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
