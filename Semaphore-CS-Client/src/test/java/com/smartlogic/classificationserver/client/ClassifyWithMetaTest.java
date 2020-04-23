package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.testng.annotations.Test;

public class ClassifyWithMetaTest extends ClassificationTestCase {

	@Test
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
			System.out.println(result.getAllClassifications());
			assertEquals(2, result.getAllClassifications().get("IPSV-Health, well-being and care").size(), "run 1 - IPSV-Health, well-being and care");


		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}


}
