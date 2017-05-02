package com.smartlogic.classificationserver.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

/**
 * Holder of information related to an individual classification request
 * @author Smartlogic Semaphore
 *
 */
public abstract class ClassificationRecord {

	private String[] data;
	private AuditFormat format;
	public ClassificationRecord(String[] data, AuditFormat format) {
		this.data = data;
		this.format = format;
	}
	
	/**
	 * Is the data line in the format expected for this AuditFormat. This is tested by checking the fixed format fields to see if they match.
	 * If they match, then it is assume that the data is of this format.
	 * @return True if data matches the AuditFormat
	 */
	public boolean isExample() {
		return format.isExample(data);
	}
	/**
	 * Get the tag attached at to the request at classification time
	 * @return The value from the data line
	 */
	public String getAuditTag() {
		return format.getAuditTag(data);
	}
	/**
	 * Get the clustering threshold used at classification time
	 * @return The value from the data line
	 */
	public float getClusteringThreshold() {
		return format.getClusteringThreshold(data);
	}
	/**
	 * Get the clustering type used at classification time
	 * @return The value from the data line
	 */
	public String getClusteringType() {
		return format.getClusteringType(data);
	}
	/**
	 * Get the diagnostics mode in use at classification time
	 * @return The value from the data line
	 */
	public String getDiagnosticsMode() {
		return format.getDiagnosticsMode(data);
	}
	/**
	 * Get the hash of the document as calculated at classification time. This 
	 * can be used to identify document changes
	 * @return The value from the data line
	 */
	public String getDocumentHash() {
		return format.getDocumentHash(data);
	}
	/**
	 * Get the document score limit (i.e. the maximum number of tags
	 * that can be returned) in place at classification time
	 * @return The value from the data line
	 */
	public Float getDocumentScoreLimit() {
		return format.getDocumentScoreLimit(data);
	}
	/**
	 * For an error record, return the component in error
	 * @return The value from the data line
	 */
	public String getErrorComponent() {
		return format.getErrorComponent(data);
	}
	/**
	 * For an error record, return the message associated with the error
	 * @return The value from the data line
	 */
	public String getErrorMessage() {
		return format.getErrorMessage(data);
	}
	/**
	 * For an error record, return the number of that error
	 * @return The value from the data line
	 */
	public int getErrorNum() {
		return format.getErrorNum(data);
	}
	/**
	 * Return the feedback mode in place at classification time
	 * @return The value from the data line
	 */
	public String getFeedbackMode() {
		return format.getFeedbackMode(data);
	}
	/**
	 * Get the name of the file actually classified
	 * @return The value from the data line
	 */
	public String getFileName() {
		return format.getFileName(data);
	}
	
	/**
	 * Get the original uri, supplied previously as a meta
	 * @return The value from the data line
	 */	
	public String getOriginalURI(){
		return format.getOriginalURI(data);
	}
	
	public String getURIOrName(){
		if (!"".equals(format.getOriginalURI(data)) && format.getOriginalURI(data) != null)
			return format.getOriginalURI(data);
		return format.getFileName(data);
	}
	/**
	 * Get the time at which classification was completed
	 * @return The value from the data line
	 */
	public Date getFinishDateTime() {
		return format.getFinishDateTime(data);
	}
	/**
	 * Get the legacy mode flag in place at classification time
	 * @return The value from the data line
	 */
	public String getLegacyMode() {
		return format.getLegacyMode(data);
	}
	/**
	 * Get the name of operation (this is pretty much redudant for classification requests)
	 * @return The value from the data line
	 */
	public String getOperation() {
		return format.getOperation(data);
	}
	/**
	 * Get the protocol used for the submission of the document
	 * @return The value from the data line
	 */
	public String getProtocolUsed() {
		return format.getProtocolUsed(data);
	}
	/**
	 * Return the identity of the classification server
	 * @return The value from the data line
	 */
	public String getServerId() {
		return format.getServerId(data);
	}
	/**
	 * Get the status of the single article flag at classification time
	 * @return The value from the data line
	 */
	public String getSingleArticleMode() {
		return format.getSingleArticleMode(data);
	}
	/**
	 * Get the IP address of the requestor for the classification 
	 * @return The value from the data line
	 */
	public String getSourceIP() {
		return format.getSourceIP(data);
	}
	/**
	 * Get the time of the start of the classification process
	 * @return The value from the data line
	 */
	public Date getStartDateTime() {
		return format.getStartDateTime(data);
	}
	/**
	 * Get the threshold used for the classification request
	 * @return The value from the data line
	 */
	public float getThreshold() {
		return format.getThreshold(data);
	}
	/**
	 * Get the time take to classify the document
	 * @return The value from the data line
	 */
	public float getTimeTaken() {
		return format.getTimeTaken(data);
	}
	/**
	 * Get the title of the document as extracted by the classification server
	 * @return The value from the data line
	 */
	public String getTitle() {
		return format.getTitle(data);
	}
	/**
	 * Get the URL of the submitted document
	 * @return The value from the data line
	 */
	public String getURL() {
		return format.getURL(data);
	}
	/**
	 * Return the scores extracted from the data line. One ClassificationScore object is returned
	 * for each tag returned by Classification Server
	 * @return The value from the data line
	 */
	public Collection<ClassificationScore> getClassificationScores() {
		return format.getClassificationScores(data);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getCanonicalName() + "\n");
		for (Method method: this.getClass().getMethods()) {
			if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
				try {
					sb.append(method.getName() + ": '" + method.invoke(this, new Object[0]) + "'\n");
				} catch (IllegalAccessException e) {
					sb.append("IllegalAccessException invoking method: " + method.getName() + "\n" + e.getMessage());
				} catch (IllegalArgumentException e) {
					sb.append("IllegalArgumentException invoking method: " + method.getName() + "\n" + e.getMessage());
				} catch (InvocationTargetException e) {
					sb.append("InvocationTargetException invoking method: " + method.getName() + "\n");
				}
			}
		}
		return sb.toString();
	}
	
}
