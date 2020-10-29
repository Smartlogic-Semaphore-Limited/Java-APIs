package com.smartlogic.ses.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestTermsXML extends SESServerMockTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	private static String xml;

	public void setUp() throws Exception {
		wireMockRule.start();
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();

			try (BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/TestTerm.xml")))) {
				String data;
				StringBuilder stringBuilder = new StringBuilder();
				while ((data = reader.readLine()) != null) {
					stringBuilder.append(data);
					stringBuilder.append("\n");
				}
				xml = stringBuilder.toString();
			}
		}
	}

	public void tearDown() {
		wireMockRule.stop();
	}

	public void testMarshal() throws Exception {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=term&term=Livestock+markets"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(termsLivestockResponseXml)));

		Map<String, Term> terms = sesClient.getTermDetailsByName("Livestock markets");

		Term term = terms.get("fa99b82b-a642-54e0-be7d-25d7b26ae53c");

		XMLifier<Term> xmlifier = new XMLifier<Term>(Term.class);

		String xmlResult = xmlifier.objectAsXML(term);
		System.out.println(xmlResult);
		assertTrue("Marshalling of term", xmlResult.startsWith("<?xml"));
	}

	@SuppressWarnings("deprecation")
	public void testUnmarshall() throws Exception {
		XMLifier<Term> xmlifier = new XMLifier<Term>(Term.class);
		Term term = xmlifier.objectFromXML(xml);

		assertEquals("Term values", "animal markets", term.getSynonyms().getSynonyms().get(0).getValue());
		assertEquals("Term values", "animal markets", term.getSynonymsList().get(0).getSynonyms().get(0).getValue());
		assertEquals("Term values", "Cattle Markets", term.getSynonymsList().get(0).getSynonyms().get(1).getValue());
	}

	public void testStructure() throws Exception {
		wireMockRule.stubFor(get(urlEqualTo("/ses/IPSV"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(structureResponseXml)));

		OMStructure structure = sesClient.getStructure();

		XMLifier<OMStructure> xmlifier = new XMLifier<OMStructure>(OMStructure.class);
		System.out.println(xmlifier.objectAsXML(structure));

	}

	private static final String termsLivestockResponseXml = "" +
			"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE><PARAMETERS><PARAMETER NAME=\"template\">service.xml</PARAMETER><PARAMETER NAME=\"fl\">* score [child limit=10000 parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:related_concept] [child limit=10000 parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:ordered_related_concept] [child limit=10000 parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:related_concept_scheme] [child limit=10000 parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:facet] [child limit=10000 parentFilter=\"(content_type:concept OR content_type:concept_scheme)\" childFilter=content_type:path_element]</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"fq\">content_type:concept_scheme content_type:concept</PARAMETER><PARAMETER NAME=\"rows\">1000</PARAMETER><PARAMETER NAME=\"version\">1</PARAMETER><PARAMETER NAME=\"structure\">XML</PARAMETER><PARAMETER NAME=\"command\">term</PARAMETER><PARAMETER NAME=\"q\">name_en:Livestock\\ markets</PARAMETER><PARAMETER NAME=\"service\">term</PARAMETER><PARAMETER NAME=\"term\">Livestock markets</PARAMETER><PARAMETER NAME=\"tbdb\">IPSV</PARAMETER><PARAMETER NAME=\"wt\">sesConceptsXML</PARAMETER></PARAMETERS><TERMS count=\"1\"><TERM SCORE=\"4.928024\" URI=\"http://smartlogic.com/term#OMITERMO6908\"><NAME>Livestock markets</NAME><ID>fa99b82b-a642-54e0-be7d-25d7b26ae53c</ID><DISPLAY_NAME>Livestock markets</DISPLAY_NAME><FREQUENCY>0</FREQUENCY><CLASSES><CLASS>Concept</CLASS></CLASSES><SYNONYMS ABBR=\"UF\" TYPE=\"Use For\"><SYNONYM>Cattle Markets</SYNONYM><SYNONYM>animal markets</SYNONYM></SYNONYMS><FACETS><FACET ID=\"e4f90f0a-0f8c-4238-a746-3d6ad03f2791\" NAME=\"Business and industry\"/></FACETS><HIERARCHY ABBR=\"BT\" QTY=\"1\" TYPE=\"Broader Term\"><FIELD FREQ=\"0\" ID=\"c91344b4-f3a9-5a20-9b96-2b6043046302\" NAME=\"term\">Markets</FIELD></HIERARCHY><ASSOCIATED ABBR=\"RT\" QTY=\"1\" TYPE=\"Related To\"><FIELD FREQ=\"0\" ID=\"9368569e-34ab-5cd2-99b3-8a6f48053427\" NAME=\"term\">Cattle</FIELD></ASSOCIATED><PATH ABBR=\"NT\" TYPE=\"Narrower Term\"><FIELD FREQ=\"0\" ID=\"e4f90f0a-0f8c-4238-a746-3d6ad03f2791\" NAME=\"term\">Business and industry</FIELD><FIELD FREQ=\"0\" ID=\"bb9e5c3b-73fa-554c-a325-e694c89a8258\" NAME=\"term\">Business sectors</FIELD><FIELD FREQ=\"0\" ID=\"f00dc09e-3f15-5c56-a6ec-c4e2684cd54c\" NAME=\"term\">Distribution and service industries</FIELD><FIELD FREQ=\"0\" ID=\"d564f5d4-24aa-5489-b083-82022cc6440b\" NAME=\"term\">Retail trade</FIELD><FIELD FREQ=\"0\" ID=\"c91344b4-f3a9-5a20-9b96-2b6043046302\" NAME=\"term\">Markets</FIELD><FIELD FREQ=\"0\" ID=\"fa99b82b-a642-54e0-be7d-25d7b26ae53c\" NAME=\"term\">Livestock markets</FIELD></PATH><METADATA><FIELD NAME=\"URI\">http://smartlogic.com/term#OMITERMO6908</FIELD></METADATA><CREATED_DATE>2020-08-05T17:27:06+0000</CREATED_DATE><MODIFIED_DATE>2020-08-05T17:27:06+0000</MODIFIED_DATE></TERM></TERMS></SEMAPHORE>";
	private static final String structureResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><SEMAPHORE><PARAMETERS><PARAMETER NAME=\"q\">content_type:om_structure_xml</PARAMETER><PARAMETER NAME=\"language\">en</PARAMETER><PARAMETER NAME=\"tbdb\">IPSV</PARAMETER><PARAMETER NAME=\"version\">1</PARAMETER><PARAMETER NAME=\"wt\">sesOmStructureXML</PARAMETER><PARAMETER NAME=\"structure\">XML</PARAMETER><PARAMETER NAME=\"command\">structure</PARAMETER></PARAMETERS><OM_STRUCTURE>\n" +
			"    <TERM_CLASSES>\n" +
			"        <TERM_CLASS ID=\"http://www.w3.org/2004/02/skos/core#Concept\" PARENT_ID=\"0\">Concept</TERM_CLASS>\n" +
			"        <TERM_CLASS ID=\"http://www.w3.org/2004/02/skos/core#ConceptScheme\" PARENT_ID=\"0\">Concept Scheme</TERM_CLASS>\n" +
			"    </TERM_CLASSES>\n" +
			"    <TERM_FACETS>\n" +
			"        <FIELD ID=\"5337ca7e-627b-4c1b-a347-273d832d09e1\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO758\">Information and communication</FIELD>\n" +
			"        <FIELD ID=\"db55547a-fc54-446e-a793-c42208590535\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO642\">Life in the community</FIELD>\n" +
			"        <FIELD ID=\"d8d28569-cb58-4385-9896-3749f0197ea1\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO981\">Employment, jobs and careers</FIELD>\n" +
			"        <FIELD ID=\"884f5a0e-80f9-4522-bc2a-b93b05f5437c\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO439\">Education and skills</FIELD>\n" +
			"        <FIELD ID=\"9dfd3c13-d46b-439b-bf2e-984bf2dd771c\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO564\">Public order, justice and rights</FIELD>\n" +
			"        <FIELD ID=\"30ec8e75-4865-4b80-b40e-249c439e7039\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO652\">Science, technology and innovation</FIELD>\n" +
			"        <FIELD ID=\"192009c2-c3fd-4850-bbf5-2e32d2331218\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO616\">Leisure and culture</FIELD>\n" +
			"        <FIELD ID=\"b36a1403-79ef-4e01-9c22-5cf6d3a5078c\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO760\">Government, politics and public administration</FIELD>\n" +
			"        <FIELD ID=\"d4ab8178-511d-4838-ac7a-5ca5da740ec6\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO557\">Health, well-being and care</FIELD>\n" +
			"        <FIELD ID=\"f9f2c988-bb60-465c-a50a-0860f253301f\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO6999\">People and organisations</FIELD>\n" +
			"        <FIELD ID=\"e4f90f0a-0f8c-4238-a746-3d6ad03f2791\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO692\">Business and industry</FIELD>\n" +
			"        <FIELD ID=\"46478ccc-c17d-4f24-9b98-b76a473c1831\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO460\">Housing</FIELD>\n" +
			"        <FIELD ID=\"fab6a755-c426-47cc-9086-b83b7d235a9d\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO521\">Transport and infrastructure</FIELD>\n" +
			"        <FIELD ID=\"3304f883-e6b8-4da4-815f-507530deecd1\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO499\">Environment</FIELD>\n" +
			"        <FIELD ID=\"3a6f2c90-3505-4159-974e-0636ca64269a\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO911\">International affairs and defence</FIELD>\n" +
			"        <FIELD ID=\"f75667e5-44ed-4c71-af2d-d62da9c0d028\" NAME=\"term\" URI=\"http://smartlogic.com/conceptScheme#OMITERMO726\">Economics and finance</FIELD>\n" +
			"    </TERM_FACETS>\n" +
			"    <TERM_ATTRIBUTES>\n" +
			"        <TERM_ATTRIBUTE ID=\"http://smartlogic.com/attributeType#Internal_no_class\" NAME=\"Internal\"/>\n" +
			"    </TERM_ATTRIBUTES>\n" +
			"    <TERM_NOTES>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#URI\" NAME=\"URI\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#editorialNote\" NAME=\"editorial note\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#historyNote\" NAME=\"history note\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#scopeNote\" NAME=\"scope note\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2000/01/rdf-schema#comment\" NAME=\"comment\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#definition\" NAME=\"definition\"/>\n" +
			"        <TERM_NOTE ID=\"http://www.w3.org/2004/02/skos/core#note\" NAME=\"note\"/>\n" +
			"    </TERM_NOTES>\n" +
			"    <TERM_METADATA/>\n" +
			"    <EQUIVALENCE_RELATIONS>\n" +
			"        <RELATION_DEF ABRV=\"UF\" FWD_DISP_NAME=\"alternative label\" ID=\"http://www.w3.org/2008/05/skos-xl#altLabel\" NAME=\"Use For\" REV_ABRV=\"UF\" REV_DISP_NAME=\"alternative label\" REV_NAME=\"Use For\" SCOPE_NOTE=\"\" SYMMETRIC=\"true\"/>\n" +
			"    </EQUIVALENCE_RELATIONS>\n" +
			"    <HIERARCHICAL_RELATIONS>\n" +
			"        <RELATION_DEF ABRV=\"NT\" FWD_DISP_NAME=\"has narrower\" ID=\"http://www.w3.org/2004/02/skos/core#broader\" NAME=\"Narrower Term\" REV_ABRV=\"BT\" REV_DISP_NAME=\"has broader\" REV_NAME=\"Broader Term\" SCOPE_NOTE=\"\" SYMMETRIC=\"false\"/>\n" +
			"        <RELATION_DEF ABRV=\"HAS NARROWER MATCH\" FWD_DISP_NAME=\"has narrower match\" ID=\"http://www.w3.org/2004/02/skos/core#broadMatch\" NAME=\"has narrower match\" REV_ABRV=\"HAS BROADER MATCH\" REV_DISP_NAME=\"has broader match\" REV_NAME=\"has broader match\" SCOPE_NOTE=\"\" SYMMETRIC=\"false\"/>\n" +
			"    </HIERARCHICAL_RELATIONS>\n" +
			"    <ASSOCIATIVE_RELATIONS>\n" +
			"        <RELATION_DEF ABRV=\"RT\" FWD_DISP_NAME=\"has related\" ID=\"http://www.w3.org/2004/02/skos/core#related\" NAME=\"Related To\" REV_ABRV=\"RT\" REV_DISP_NAME=\"has related\" REV_NAME=\"Related To\" SCOPE_NOTE=\"\" SYMMETRIC=\"true\"/>\n" +
			"        <RELATION_DEF ABRV=\"HAS RELATED MATCH\" FWD_DISP_NAME=\"has related match\" ID=\"http://www.w3.org/2004/02/skos/core#relatedMatch\" NAME=\"has related match\" REV_ABRV=\"HAS RELATED MATCH\" REV_DISP_NAME=\"has related match\" REV_NAME=\"has related match\" SCOPE_NOTE=\"\" SYMMETRIC=\"true\"/>\n" +
			"    </ASSOCIATIVE_RELATIONS>\n" +
			"    <INDEX_METADATA>\n" +
			"        <PUBLISH_DATE>2020-10-29T10:04:17.358-07:00</PUBLISH_DATE>\n" +
			"    </INDEX_METADATA>\n" +
			"</OM_STRUCTURE><MODELS><MODEL><NAME>IPSV</NAME></MODEL></MODELS></SEMAPHORE>";
}
