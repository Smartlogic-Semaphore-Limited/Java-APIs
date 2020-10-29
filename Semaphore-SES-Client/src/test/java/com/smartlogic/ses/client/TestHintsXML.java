package com.smartlogic.ses.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestHintsXML extends SESServerMockTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	public void setUp() {
		wireMockRule.start();
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
		}
	}

	public void tearDown() {
		wireMockRule.stop();
	}

	private final static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<termHint>\n" +
			"    <name>Appointments procedure</name>\n" +
			"    <id>346a90a2-c8e9-523f-8a32-58fe02ba6efe</id>\n" +
			"    <weight>0.0</weight>\n" +
			"    <facet>\n" +
			"        <name>Government, politics and public administration</name>\n" +
			"        <id>ed296c39-8be6-4257-b44b-4bae4ad2d9c9</id>\n" +
			"    </facet>\n" +
			"    <facets>\n" +
			"        <facetList>\n" +
			"            <name>Business and industry</name>\n" +
			"            <id>abb97cf2-3b2c-4ed2-a205-36ad9a7b0085</id>\n" +
			"        </facetList>\n" +
			"        <facetList>\n" +
			"            <name>Government, politics and public administration</name>\n" +
			"            <id>ed296c39-8be6-4257-b44b-4bae4ad2d9c9</id>\n" +
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
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=appo"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE>\n" +
								"<TERM_HINTS total=\"1\">\n" +
								"<TERM_HINT ID=\"346a90a2-c8e9-523f-8a32-58fe02ba6efe\" NAME=\"Appointments procedure\">\n" +
								"<CLASSES>\n" +
								"<CLASS>Concept</CLASS>\n" +
								"</CLASSES>\n" +
								"<HINT ID=\"346a90a2-c8e9-523f-8a32-58fe02ba6efe\" NATURE=\"PT\">\n" +
								"<EM>Appo</EM>\n" +
								"intments procedure\n" +
								"</HINT>\n" +
								"<FACET ID=\"729c064f-166c-4924-b1b7-ffe38484142d\" NAME=\"Business and industry\"/>\n" +
								"<FACET ID=\"10612c03-a3ee-4272-ab88-6e403821700c\" NAME=\"Government, politics and public administration\"/>\n" +
								"</TERM_HINT>\n" +
								"</TERM_HINTS>\n" +
								"</SEMAPHORE>")));

		Map<String, TermHint> termHints = sesClient.getTermHints("appo");

		XMLifier<TermHint> xmlifier = new XMLifier<TermHint>(TermHint.class);
		TermHint startTh = termHints.values().iterator().next();
		String savedXml = xmlifier.objectAsXML(startTh);
		TermHint newTh = xmlifier.objectFromXML(savedXml);

		assertEquals("Values arrays are not the same size", startTh.getValues().getValues().size(),
				newTh.getValues().getValues().size());
		assertEquals("Term IDs do not match", startTh.getId(), newTh.getId());
		assertEquals("Term names do not match", startTh.getName(), newTh.getName());
	}

	public void testUnmarshall() throws Exception {
		XMLifier<TermHint> xmlifier = new XMLifier<TermHint>(TermHint.class);
		TermHint termHint = xmlifier.objectFromXML(xml);

		assertEquals("TermHint values", "intments procedure", termHint.getValues().getValues().get(0).getPostEm());
	}

}
