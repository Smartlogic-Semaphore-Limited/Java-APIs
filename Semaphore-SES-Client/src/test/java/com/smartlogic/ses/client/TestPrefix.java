package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestPrefix extends SESServerMockTestCase {
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

	public void testPrefixSea() throws SESException {

		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=sea"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(hintSeaResponseXml)));
		Map<String, TermHint> termHints = sesClient.getTermHints("sea");

		TermHint termHintSea = termHints.get("8bac881f-21a9-533e-86c9-7cd22d0d8971");
		assertEquals("THS", "8bac881f-21a9-533e-86c9-7cd22d0d8971", termHintSea.getId());
//		assertEquals("THS", "disp_taxonomy", termHintSea.getIndex());
		assertEquals("THS", "Air Sea Rescue Service", termHintSea.getName());
		assertEquals("THS", "Public order, justice and rights", termHintSea.getFacets().getFacets().get(0).getName());
		assertEquals("THS", "Air ", termHintSea.getValues().getValues().get(0).getPreEm());
		assertEquals("THS", "Sea", termHintSea.getValues().getValues().get(0).getEm());
		assertEquals("THS", " Rescue Service", termHintSea.getValues().getValues().get(0).getPostEm());
//		assertTrue("THS",Math.abs(termHintSea.getWeight()-16.3078) < 0.1);
		assertEquals("THS", 10, termHints.size());
	}

	public void testPrefix1() throws SESException {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=chi"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(hintChiResponseXml)));

		Map<String, TermHint> termHints = sesClient.getTermHints("chi");

		TermHint termHint1 = termHints.get("9e933b80-b6b1-5d31-9f77-8d25c3dc5816");
		assertEquals("TH1", "Environment", termHint1.getFacets().getFacets().get(0).getName());
		assertEquals("TH1", 10, termHints.size()); //NOTE: this is the default we ship with.
	}

	public void testPrefix2() throws SESException {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=apt"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(hintAptResponseXml)));

		Map<String, TermHint> termHints = sesClient.getTermHints("apt");

		assertEquals("TH2", 0, termHints.size());
	}

	public void testMultiple() throws SESException {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=apt"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(hintAptResponseXml)));
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=chi"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(hintChiResponseXml)));

		for (int i = 0; i < 10; i++) {
			sesClient.getTermHints(i % 2 == 0 ? "chi" : "apt");
		}
	}

	private static final String hintSeaResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE><PARAMETERS><PARAMETER NAME=\"template\">service.xml</PARAMETER><PARAMETER NAME=\"fl\">id, name_en_pl, name_en, class_name_en, [child parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:facet]</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"fq\">content_type:concept_scheme content_type:concept</PARAMETER><PARAMETER NAME=\"sort\">score desc, name_en_pl asc</PARAMETER><PARAMETER NAME=\"rows\">10</PARAMETER><PARAMETER NAME=\"version\">1</PARAMETER><PARAMETER NAME=\"raw_query\">sea</PARAMETER><PARAMETER NAME=\"structure\">XML</PARAMETER><PARAMETER NAME=\"q\">autocomplete_en_plf:sea* autocomplete_en_f:sea* autocomplete_en_pl:sea* autocomplete_en:sea*</PARAMETER><PARAMETER NAME=\"defType\">edismax</PARAMETER><PARAMETER NAME=\"qf\">autocomplete_en_plf^100.0 autocomplete_en_f^20.0 autocomplete_en_pl^50.0 autocomplete_en^1.0</PARAMETER><PARAMETER NAME=\"service\">PREFIX</PARAMETER><PARAMETER NAME=\"tbdb\">IPSV</PARAMETER><PARAMETER NAME=\"wt\">sesHintsXML</PARAMETER></PARAMETERS><TERM_HINTS total=\"10\"><TERM_HINT ID=\"68a31523-a599-58e3-86ed-ef6bdc000208\" NAME=\"Sea angling\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"68a31523-a599-58e3-86ed-ef6bdc000208\" NATURE=\"PT\"><EM>Sea</EM> angling</HINT><FACET ID=\"715a57f8-d84e-472d-9c02-ef2bbce0b9ef\" NAME=\"Leisure and culture\"/></TERM_HINT><TERM_HINT ID=\"13c6fbb0-1312-5814-98a1-e3324384d9c7\" NAME=\"Sea Cadet Corps\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"13c6fbb0-1312-5814-98a1-e3324384d9c7\" NATURE=\"PT\"><EM>Sea</EM> Cadet Corps</HINT><FACET ID=\"bc565469-cdd8-4c66-8c33-ee6d986f0328\" NAME=\"People and organisations\"/><FACET ID=\"776f8bac-c573-4d2e-9bf0-903326fc6eb5\" NAME=\"International affairs and defence\"/></TERM_HINT><TERM_HINT ID=\"b33c4227-c6ff-5564-8859-cbc0ac0c3ecd\" NAME=\"Search engines\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"b33c4227-c6ff-5564-8859-cbc0ac0c3ecd\" NATURE=\"PT\"><EM>Sea</EM>rch engines</HINT><FACET ID=\"e41d67e2-48bb-42bf-ba46-df7065ed0285\" NAME=\"Science, technology and innovation\"/><FACET ID=\"ca792b9a-f0ca-4fd9-ba45-a6fd65392a81\" NAME=\"Information and communication\"/></TERM_HINT><TERM_HINT ID=\"32943daf-4ce8-54a6-bdf0-be18d478dcef\" NAME=\"Season tickets (parking)\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"32943daf-4ce8-54a6-bdf0-be18d478dcef\" NATURE=\"PT\"><EM>Sea</EM>son tickets (parking)</HINT><FACET ID=\"6acfdac2-7486-4e6b-83c8-b91908d6f45c\" NAME=\"Transport and infrastructure\"/></TERM_HINT><TERM_HINT ID=\"ec20fffd-cdc6-575a-852b-726df3cabd0c\" NAME=\"Seasonal affective disorder\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"ec20fffd-cdc6-575a-852b-726df3cabd0c\" NATURE=\"PT\"><EM>Sea</EM>sonal affective disorder</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"d2c4d8c4-a3fa-535c-bcd3-0d5e50e86a71\" NAME=\"Seat belts (cars)\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"d2c4d8c4-a3fa-535c-bcd3-0d5e50e86a71\" NATURE=\"PT\"><EM>Sea</EM>t belts (cars)</HINT><FACET ID=\"6acfdac2-7486-4e6b-83c8-b91908d6f45c\" NAME=\"Transport and infrastructure\"/></TERM_HINT><TERM_HINT ID=\"dc8efe85-9084-527f-b88c-b1c1dfe45e7b\" NAME=\"Public seating\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"dc8efe85-9084-527f-b88c-b1c1dfe45e7b\" NATURE=\"PT\">Public <EM>sea</EM>ting</HINT><FACET ID=\"6acfdac2-7486-4e6b-83c8-b91908d6f45c\" NAME=\"Transport and infrastructure\"/></TERM_HINT><TERM_HINT ID=\"8bac881f-21a9-533e-86c9-7cd22d0d8971\" NAME=\"Air Sea Rescue Service\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"8bac881f-21a9-533e-86c9-7cd22d0d8971\" NATURE=\"PT\">Air <EM>Sea</EM> Rescue Service</HINT><FACET ID=\"6b7db970-af6c-4a2e-9256-ae06211edc4b\" NAME=\"Public order, justice and rights\"/><FACET ID=\"776f8bac-c573-4d2e-9bf0-903326fc6eb5\" NAME=\"International affairs and defence\"/></TERM_HINT><TERM_HINT ID=\"3b223008-04e2-5f92-861d-d90039501b26\" NAME=\"Children's seats in cars\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"3b223008-04e2-5f92-861d-d90039501b26\" NATURE=\"PT\">Children's <EM>sea</EM>ts in cars</HINT><FACET ID=\"6acfdac2-7486-4e6b-83c8-b91908d6f45c\" NAME=\"Transport and infrastructure\"/></TERM_HINT><TERM_HINT ID=\"89c43370-de59-55a4-baf1-e9ead053c0ee\" NAME=\"Coastal erosion and protection\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"89c43370-de59-55a4-baf1-e9ead053c0ee\" NATURE=\"NPT\"><EM>Sea</EM> Walls</HINT><FACET ID=\"d761553a-d378-4e99-9712-c3d888465e29\" NAME=\"Environment\"/></TERM_HINT></TERM_HINTS></SEMAPHORE>";
	private static final String hintChiResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE><PARAMETERS><PARAMETER NAME=\"template\">service.xml</PARAMETER><PARAMETER NAME=\"fl\">id, name_en_pl, name_en, class_name_en, [child parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:facet]</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"fq\">content_type:concept_scheme content_type:concept</PARAMETER><PARAMETER NAME=\"sort\">score desc, name_en_pl asc</PARAMETER><PARAMETER NAME=\"rows\">10</PARAMETER><PARAMETER NAME=\"version\">1</PARAMETER><PARAMETER NAME=\"raw_query\">chi</PARAMETER><PARAMETER NAME=\"structure\">XML</PARAMETER><PARAMETER NAME=\"q\">autocomplete_en_plf:chi* autocomplete_en_f:chi* autocomplete_en_pl:chi* autocomplete_en:chi*</PARAMETER><PARAMETER NAME=\"defType\">edismax</PARAMETER><PARAMETER NAME=\"qf\">autocomplete_en_plf^100.0 autocomplete_en_f^20.0 autocomplete_en_pl^50.0 autocomplete_en^1.0</PARAMETER><PARAMETER NAME=\"service\">PREFIX</PARAMETER><PARAMETER NAME=\"tbdb\">IPSV</PARAMETER><PARAMETER NAME=\"wt\">sesHintsXML</PARAMETER></PARAMETERS><TERM_HINTS total=\"10\"><TERM_HINT ID=\"9e933b80-b6b1-5d31-9f77-8d25c3dc5816\" NAME=\"Chickens\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"9e933b80-b6b1-5d31-9f77-8d25c3dc5816\" NATURE=\"PT\"><EM>Chi</EM>ckens</HINT><FACET ID=\"d761553a-d378-4e99-9712-c3d888465e29\" NAME=\"Environment\"/></TERM_HINT><TERM_HINT ID=\"747792ba-1537-5cf6-93b2-90aadc188591\" NAME=\"Child abuse\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"747792ba-1537-5cf6-93b2-90aadc188591\" NATURE=\"PT\"><EM>Chi</EM>ld abuse</HINT><FACET ID=\"6d13260c-642e-4ec2-9294-e5cbcefc3369\" NAME=\"Life in the community\"/></TERM_HINT><TERM_HINT ID=\"9de6fe1e-1206-5532-a48b-808809a7d0fe\" NAME=\"Child benefit\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"9de6fe1e-1206-5532-a48b-808809a7d0fe\" NATURE=\"PT\"><EM>Chi</EM>ld benefit</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"cc44d542-12d0-507c-9652-9752c8c6e0c6\" NAME=\"Child care\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"cc44d542-12d0-507c-9652-9752c8c6e0c6\" NATURE=\"PT\"><EM>Chi</EM>ld care</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"f80c16ca-0a62-5901-8cfb-32c549d5dfbd\" NAME=\"Child custody\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"f80c16ca-0a62-5901-8cfb-32c549d5dfbd\" NATURE=\"PT\"><EM>Chi</EM>ld custody</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"b84cdc47-839b-5682-9000-b6cc75447e90\" NAME=\"Child employment\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"b84cdc47-839b-5682-9000-b6cc75447e90\" NATURE=\"PT\"><EM>Chi</EM>ld employment</HINT><FACET ID=\"30b6deb0-bd17-4dec-ae5e-9935a16a84a3\" NAME=\"Employment, jobs and careers\"/></TERM_HINT><TERM_HINT ID=\"331dee57-0d4a-5cbd-b69c-e5f83ebc5a0a\" NAME=\"Child health services\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"331dee57-0d4a-5cbd-b69c-e5f83ebc5a0a\" NATURE=\"PT\"><EM>Chi</EM>ld health services</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"2df53e74-e598-53d4-af26-f6ae5afedda5\" NAME=\"Child protection\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"2df53e74-e598-53d4-af26-f6ae5afedda5\" NATURE=\"PT\"><EM>Chi</EM>ld protection</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"92eb5545-b3a0-5c3e-ab5d-b29d77292407\" NAME=\"Child safety\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"92eb5545-b3a0-5c3e-ab5d-b29d77292407\" NATURE=\"PT\"><EM>Chi</EM>ld safety</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT><TERM_HINT ID=\"d527162e-6853-5690-8c51-8e96b9cc78eb\" NAME=\"Child support\"><CLASSES><CLASS>Concept</CLASS></CLASSES><HINT ID=\"d527162e-6853-5690-8c51-8e96b9cc78eb\" NATURE=\"PT\"><EM>Chi</EM>ld support</HINT><FACET ID=\"675b8390-72ad-45d1-bfab-77b6c2ac7420\" NAME=\"Health, well-being and care\"/></TERM_HINT></TERM_HINTS></SEMAPHORE>";
	private static final String hintAptResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE><PARAMETERS><PARAMETER NAME=\"template\">service.xml</PARAMETER><PARAMETER NAME=\"fl\">id, name_en_pl, name_en, class_name_en, [child parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:facet]</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"fq\">content_type:concept_scheme content_type:concept</PARAMETER><PARAMETER NAME=\"sort\">score desc, name_en_pl asc</PARAMETER><PARAMETER NAME=\"rows\">10</PARAMETER><PARAMETER NAME=\"version\">1</PARAMETER><PARAMETER NAME=\"raw_query\">apt</PARAMETER><PARAMETER NAME=\"structure\">XML</PARAMETER><PARAMETER NAME=\"q\">autocomplete_en_plf:apt* autocomplete_en_f:apt* autocomplete_en_pl:apt* autocomplete_en:apt*</PARAMETER><PARAMETER NAME=\"defType\">edismax</PARAMETER><PARAMETER NAME=\"qf\">autocomplete_en_plf^100.0 autocomplete_en_f^20.0 autocomplete_en_pl^50.0 autocomplete_en^1.0</PARAMETER><PARAMETER NAME=\"service\">PREFIX</PARAMETER><PARAMETER NAME=\"tbdb\">IPSV</PARAMETER><PARAMETER NAME=\"wt\">sesHintsXML</PARAMETER></PARAMETERS><TERM_HINTS total=\"0\"/></SEMAPHORE>";
}
