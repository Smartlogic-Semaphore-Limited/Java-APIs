package com.smartlogic.ontologyeditor.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.smartlogic.ontologyeditor.OEClientReadWrite;

public abstract class ModelManipulation {

	private static Properties properties = null;
	public static synchronized String get(String name) throws IOException {
		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(new FileInputStream("config.properties"));
			} catch (IOException e) {
				System.err.println("Error attempting to read properties from file \"config.properties\"");
				throw e;
			}
		}
		return properties.getProperty(name);
	}

	protected static OEClientReadWrite getOEClient(boolean proxy) throws IOException {
		OEClientReadWrite oeClient = new OEClientReadWrite();
		if (proxy) oeClient.setProxyAddress(get("proxy.address"));
		oeClient.setBaseURL(get("base.url"));
		oeClient.setModelUri(get("model.uri"));
		oeClient.setToken(get("token"));
		return oeClient;
	}
}

