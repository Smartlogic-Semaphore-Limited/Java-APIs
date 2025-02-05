package com.smartlogic.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration parameters required for the Semaphore Cloud test examples 
 * @author keith.atkins@smartlogic.com
 *
 * The configuration parameters are assumed to be in a file "config.properties"
 * 
 * The expected properties are:
 * 
 * apiKey
 * tokenRequestURL
 * csUrl
 * sesUrl
 * 
 * all of which are available from the Basic API Interface information panel on your Semaphore Cloud instance 
 */
public class CloudConfiguration {
	
	private static Properties properties = null;
	public static synchronized String get(String name) throws IOException {
		File file = new File("config.properties");
		if (properties == null) {
			try (FileInputStream propertiesInputStream = new FileInputStream(file)) {
				properties = new Properties();
				properties.load(propertiesInputStream);
			} catch (IOException e) {
				System.err.println("Error attempting to read properties from file " + file.getAbsolutePath());
				throw e;
			}
		}
		return properties.getProperty(name);
	}
}
