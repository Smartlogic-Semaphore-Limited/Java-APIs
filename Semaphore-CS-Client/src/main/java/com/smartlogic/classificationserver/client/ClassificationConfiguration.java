package com.smartlogic.classificationserver.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Data structure to hold the details of the classification server along with any parameters
 * that are to be supplied to override the defaults in the classification server install.
 * @author Smartlogic Semaphore
 *
 */
public class ClassificationConfiguration {

	private String url;
	public String getUrl() throws MalformedURLException {
		if (url == null) {
			url = new URL(protocol, hostName, hostPort, hostPath).toExternalForm();
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	private String protocol = "http";
	/**
	 * Get the protocol used for submission
	 * @return The protocol in use
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * Set the protocol to be used for submission (default http)
	 * @param protocol The protocol to use
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	private String hostName = "localhost";
	/**
	 * Get the classification server host
	 * @return The name of the host
	 */
	public String getHostName() {
		return hostName;
	}
	/**
	 * Set the name of the classification server host machine (default localhost)
	 * @param hostName The name of the host
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	private int hostPort = 5058;
	/**
	 * Get the port on which classification server is listening
	 * @return The port
	 */
	public int getHostPort() {
		return hostPort;
	}
	/**
	 * Set the port on which classification server is listening (default 5058)
	 * @param hostPort The port
	 */
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}

	private String hostPath = "/index.html";
	/**
	 * Get the path part of the URL used for classification requests
	 * @return The path
	 */
	public String getHostPath() {
		return hostPath;
	}
	/**
	 * Set the path part of the URL used for classification requests (default /index.html)
	 * @param hostPath The path
	 */
	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

	private boolean singleArticle = false;
	/**
	 * Is the request using single article mode
	 * @return The value
	 */
	public boolean isSingleArticle() {
		return singleArticle;
	}
	/**
	 * Set whether the request should use single article mode
	 * @param singleArticle Whether (true) or not (false) to use single article mode
	 */
	public void setSingleArticle(boolean singleArticle) {
		this.multiArticle = !singleArticle;
		this.singleArticle = singleArticle;
	}

	private boolean multiArticle = true;
	/**
	 * Is the request using multi article mode
	 * @return multiarticle The current setting of multi-article
	 */
	public boolean isMultiArticle() {
		return multiArticle;
	}
	/**
	 * Set whether the requets should use multi article mode
	 * @param multiArticle Whether (true) or not (false) to use multi article mode
	 */
	public void setMultiArticle(boolean multiArticle) {
		this.singleArticle = !multiArticle;
		this.multiArticle = multiArticle;
	}


	private boolean feedback;
	/**
	 * Is feedback enabled
	 * @return Is feedback enabled
	 */
	public boolean isFeedback() {
		return feedback;
	}
	/**
	 * Set whether classification server should return feedback information (default false)
	 * @param feedback Whether (true) or not (false) feedback information should be returned
	 */
	public void setFeedback(boolean feedback) {
		this.feedback = feedback;
	}


	private boolean stylesheet;
	/**
	 * Should the CS stylesheet be used to format the response.
	 * @return Whether the style sheet should be used
	 */
	public boolean isStylesheet() {
		return stylesheet;
	}
	public void setStylesheet(boolean stylesheet) {
		this.stylesheet = stylesheet;
	}

	private Map<String, String> additionalParameters = new HashMap<String, String>();
	/**
	 * Get any additional parameters that should be sent to classification server
	 * @return Any additional parameters set
	 */
	public Map<String, String> getAdditionalParameters() {
		return additionalParameters;
	}
	/**
	 * Set any additional parameters that should be sent to classification server
	 * @param additionalParameters Map containing additional parameters to send
	 */
	public void setAdditionalParameters(Map<String, String> additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	private int connectionTimeoutMS = 10000;
	/**
	 * Get the connection time out in milliseconds set on the http client (default 10000)
	 * @return The connection timeout in milliseconds
	 */
	public int getConnectionTimeoutMS() {
		return connectionTimeoutMS;
	}
	/**
	 * Set the connection time out in milliseconds set on the http client (default 10000)
	 * @param connectionTimeoutMS The connection timeout period in milliseconds
	 */
	public void setConnectionTimeoutMS(int connectionTimeoutMS) {
		this.connectionTimeoutMS = connectionTimeoutMS;
	}

	private int socketTimeoutMS = 10000;
	/**
	 * Get the socket time out in milliseconds set on the http client (default 10000)
	 * @return the socket timeout in milliseconds
	 */
	public int getSocketTimeoutMS() {
		return socketTimeoutMS;
	}
	/**
	 * Set the socket time out in milliseconds set on the http client (default 10000)
	 * @param socketTimeoutMS The socket timeout period in milliseconds
	 */
	public void setSocketTimeoutMS(int socketTimeoutMS) {
		this.socketTimeoutMS = socketTimeoutMS;
	}

	private boolean useGeneratedKeys = false;

	/**
	 * Are generated keys enabled
	 * @return  useGeneratedKeys The current value of the "use generated keys" parameter
	 */
	public boolean isUseGeneratedKeys() {
		return this.useGeneratedKeys;
	}

	/**
	 * Set whether the classification server should use generated keys
	 * @param useGeneratedKeys Whether (true) or not (false) "use generated keys" should be enabled
	 */
	public void setUseGeneratedKeys(boolean useGeneratedKeys) {
		this.useGeneratedKeys  = useGeneratedKeys;
	}

	private boolean returnHashCode = false;

	/**
	 * Should the calculated document hash be returned
	 * @return The current document hash
	 */
	public boolean isReturnHashCode() {
		return returnHashCode;
	}

	/**
	 * Set whether the calculated document hash should be returned
	 * @param returnHashCode Whether (true) or not (false) the document hash should be returned
	 */
	public void setReturnHashCode(boolean returnHashCode) {
		this.returnHashCode = returnHashCode;
	}

	private String apiToken;
	/**
	 * Get the token used for authentication within the Smartlogic Cloud
	 * @return
	 */
	public String getApiToken() {
		return apiToken;
	}
	/**
	 * Set the token used for authentication within the Smartlogic Cloud
	 * @param apiToken
	 */
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}


}
