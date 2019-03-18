package com.smartlogic.ses.client;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class TestHintsXML extends TestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	public void setUp() {
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
		}
	}


	private final static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<termHint>\n" +
			"    <name>Appointments procedure</name>\n" +
			"    <id>346a90a2-c8e9-523f-8a32-58fe02ba6efe</id>\n" +
			"    <weight>0.0</weight>\n" +
			"    <facet>\n" +
			"        <name>Government, politics and public administration</name>\n" +
			"        <id>fa08303d-4ad5-4d4a-abec-a2a1e3f1aded</id>\n" +
			"    </facet>\n" +
			"    <facets>\n" +
			"        <facetList>\n" +
			"            <name>Business and industry</name>\n" +
			"            <id>2fdca937-c0bf-41bb-9469-9e9f7ab16258</id>\n" +
			"        </facetList>\n" +
			"        <facetList>\n" +
			"            <name>Government, politics and public administration</name>\n" +
			"            <id>fa08303d-4ad5-4d4a-abec-a2a1e3f1aded</id>\n" +
			"        </facetList>\n" +
			"    </facets>\n" +
			"    <termClasses>\n" +
			"        <termClassList>\n" +
			"            <value>Concept</value>\n" +
			"        </termClassList>\n" +
			"    </termClasses>\n" +
			"    <hint>Appointments procedure</hint>\n" +
			"    <values>\n" +
			"        <valueList>\n" +
			"            <nature>PT</nature>\n" +
			"            <id>346a90a2-c8e9-523f-8a32-58fe02ba6efe</id>\n" +
			"            <em>Appo</em>\n" +
			"            <preEm></preEm>\n" +
			"            <postEm>intments procedure</postEm>\n" +
			"        </valueList>\n" +
			"    </values>\n" +
			"</termHint>\n" +
			"";
	public void testMarshal() throws Exception {
		Map<String, TermHint> termHints = sesClient.getTermHints("appo");

		XMLifier<TermHint> xmlifier = new XMLifier<TermHint>(TermHint.class);
		String savedXml = xmlifier.objectAsXML(termHints.values().toArray(new TermHint[0])[0]);
		assertEquals("Term hint serialized", xml, savedXml);
	}

	public void testUnmarshall() throws Exception {
		XMLifier<TermHint> xmlifier = new XMLifier<TermHint>(TermHint.class);
		TermHint termHint = xmlifier.objectFromXML(xml);

		assertEquals("TermHint values", "intments procedure", termHint.getValues().getValues().get(0).getPostEm());
	}

}
