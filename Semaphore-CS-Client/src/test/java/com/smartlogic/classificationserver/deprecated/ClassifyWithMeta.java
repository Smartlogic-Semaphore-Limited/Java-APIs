package com.smartlogic.classificationserver.deprecated;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.smartlogic.classificationserver.client.Body;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Title;

public class ClassifyWithMeta extends ClassificationTestCase {

	@SuppressWarnings("deprecation")
	public void testClassifyWithMeta() {
		
		try {
			String title = "This is the document title";
			String body = "This is the document body";
			
			Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
			Collection<String> urls = new Vector<String>();
			urls.add("http://www.bbc.co.uk");
			urls.add("http://www.telegraph.co.uk");
			metadata.put("URL", urls);
			Collection<String> other = new Vector<String>();
			other.add("widgets");
			other.add("poiseiden");
			metadata.put("other", other);
			
			classificationClient.classifyDocument(new Body(body), new Title(title), metadata);
			
			
		} catch (Exception e) {
			System.err.println("Exception encountered: " + e.getMessage());
		}
	}


}
