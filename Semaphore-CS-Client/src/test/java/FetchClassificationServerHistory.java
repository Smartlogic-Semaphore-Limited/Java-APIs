import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.smartlogic.classificationserver.client.ClassificationClient;
import com.smartlogic.classificationserver.client.ClassificationConfiguration;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationRecord;
import com.smartlogic.classificationserver.client.ObjectPrinter;

public class FetchClassificationServerHistory {

	public static void main(String[] args) throws ParseException, ClassificationException {
		try (ClassificationClient classificationClient = new ClassificationClient()) {
//		classificationClient.setProxyHost("localhost");
//		classificationClient.setProxyPort(8888);

			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setProtocol("http");
			classificationConfiguration.setHostName("jelly.smartlogic.com");
			classificationConfiguration.setHostPort(5058);
			classificationConfiguration.setHostPath("/index.html");
			classificationConfiguration.setSingleArticle(false);
			classificationConfiguration.setMultiArticle(true);

			Map<String, String> additionalParameters = new HashMap<String, String>();
			additionalParameters.put("threshold", "1");
			additionalParameters.put("language", "en1");
			classificationConfiguration.setAdditionalParameters(additionalParameters);
			classificationClient.setClassificationConfiguration(classificationConfiguration);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date startTime = sdf.parse("2012-05-16 13:25:00");
			Date endTime = sdf.parse("2012-05-16 13:26:00");
			System.out.println(startTime);
			System.out.println(endTime);

			Collection<ClassificationRecord> records = classificationClient.getClassificationHistory(startTime,
					endTime);
			for (ClassificationRecord record : records) {
				System.out.println(ObjectPrinter.toString(record));
			}
		}
	}
}
