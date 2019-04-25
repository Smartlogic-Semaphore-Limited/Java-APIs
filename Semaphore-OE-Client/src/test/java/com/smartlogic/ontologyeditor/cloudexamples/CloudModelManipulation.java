package com.smartlogic.ontologyeditor.cloudexamples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.TokenFetcher;
import com.smartlogic.ontologyeditor.OEClientReadWrite;

public abstract class CloudModelManipulation {

	private static Properties properties = null;

	public static synchronized String get(String name) throws IOException {
		if (properties == null) {
			try (FileInputStream propertiesInputStream = new FileInputStream("cloud.properties")) {
				properties = new Properties();
				properties.load(propertiesInputStream);
			} catch (IOException e) {
				System.err.println("Error attempting to read properties from file \"config.properties\"");
				throw e;
			}
		}
		return properties.getProperty(name);
	}

	protected static OEClientReadWrite getCloudOEClient(boolean proxy) throws IOException, CloudException {
		OEClientReadWrite oeClient = new OEClientReadWrite();
		if (proxy)
			oeClient.setProxyAddress(get("proxy.address"));

		TokenFetcher tokenFetcher = new TokenFetcher(get("token.url"), get("token.key"));
		oeClient.setCloudToken(tokenFetcher.getAccessToken());
		oeClient.setBaseURL(get("base.url"));
		oeClient.setModelUri(get("model.uri"));
		oeClient.setToken(get("token"));
		return oeClient;
	}
}
