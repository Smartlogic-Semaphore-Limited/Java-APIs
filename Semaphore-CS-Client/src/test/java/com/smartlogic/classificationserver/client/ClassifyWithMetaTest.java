package com.smartlogic.classificationserver.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ClassifyWithMetaTest extends ClassificationTestCase {

	public void testClassifyWithMeta() {

		try {
			String title = "This is the document title";
			String body = "abandoned cars";

			Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
			Collection<String> urls = new Vector<String>();
			urls.add("http://www.bbc.co.uk");
			urls.add("http://www.telegraph.co.uk");
			metadata.put("URL", urls);
			Collection<String> other = new Vector<String>();
			other.add("widgets");
			other.add("abandoned cars");
			metadata.put("other", other);

			Result result = classificationClient.getClassifiedDocument(new Body(body), new Title(title), metadata);
			assertEquals("run 1 - IPSV", 1, result.getAllClassifications().get("IPSV").size());
			assertEquals("run 1 - IPSV_ID", 1, result.getAllClassifications().get("IPSV_ID").size());
			assertEquals("run 1 - IPSV_RAW", 1, result.getAllClassifications().get("IPSV_RAW").size());


		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}


}
