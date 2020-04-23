package com.smartlogic.ses.client;

public class ConfigUtil {

	private static SESClient sesClient = getSESClient();

	public static SESClient getSESClient() {
		if (sesClient != null)
			return sesClient;

		SESClient client = new SESClient();

		client.setOntology("IPSV");
		client.setHost("svrka02");
		client.setPort(8983);
		client.setPath("/ses");


//		if (prop.containsKey("proxyHost"))
//			client.setProxyHost(prop.getProperty("proxyHost"));
//		if (prop.containsKey("proxyPort"))
//			client.setProxyHost(prop.getProperty("proxyPort"));
		return client;
	}
}
