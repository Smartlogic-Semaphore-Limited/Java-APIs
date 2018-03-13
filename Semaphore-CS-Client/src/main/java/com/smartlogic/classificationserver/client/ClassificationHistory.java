package com.smartlogic.classificationserver.client;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.smartlogic.classificationserver.client.csvparser.CSVParser;

/**
 * The object used to contain the information returned by the classification history request.
 * @author Smartlogic Semaphore
 *
 */
public class ClassificationHistory extends XMLReader {
	protected final Log logger = LogFactory.getLog(getClass());

	public ClassificationHistory(String successFormat, String failFormat, String logData) {
		this.successFormat = successFormat;
		this.failFormat = failFormat;
		this.logData = logData;
	}
	
	protected ClassificationHistory(byte[] data) throws ClassificationException {
		// If there is no data provided, then throw an exception
		if (data == null) throw new ClassificationException("No response from classification server");
		
		Element element = getRootElement(data);
			
		NodeList successFormatNodeList = element.getElementsByTagName("SuccessFormat");
		if ((successFormatNodeList == null) || (successFormatNodeList.getLength() == 0)) {
			throw new ClassificationException("No SuccessFormat element returned by classification server: " + toString(data));
		}
		Element successFormatElement = (Element)successFormatNodeList.item(0);
		this.successFormat = successFormatElement.getTextContent();

		NodeList failFormatNodeList = element.getElementsByTagName("FailFormat");
		if ((failFormatNodeList == null) || (failFormatNodeList.getLength() == 0)) {
			throw new ClassificationException("No FailFormat element returned by classification server: " + toString(data));
		}
		Element failFormatElement = (Element)failFormatNodeList.item(0);
		this.failFormat = failFormatElement.getTextContent();

		NodeList logDataNodeList = element.getElementsByTagName("logData");
		if ((logDataNodeList == null) || (logDataNodeList.getLength() == 0)) {
			throw new ClassificationException("No LogData element returned by classification server: " + toString(data));
		}
		Element logDataElement = (Element)logDataNodeList.item(0);
		this.logData = logDataElement.getTextContent();
			
	}
	
	private String successFormat;
	/** 
	 * Get the format used for a success record
	 * @return the format
	 */
	public String getSuccessFormat() {
		return successFormat;
	}

	private String failFormat;
	/**
	 * Get the format used for a failure record
	 * @return the format
	 */
	public String getFailFormat() {
		return failFormat;
	}

	private String logData;
	/**
	 * Get the original data used to create the classification history
	 * @return the log data
	 */
	public String getLogData() {
		return logData;
	}
	
	/**
	 * Retrieve the complete set of classification records
	 * @return All classification records that were in the original data set
	 * @throws ClassificationException Classification exception
	 */
	public Collection<ClassificationRecord> getClassificationRecords() throws ClassificationException {
		logger.debug("getClassificationRecords - entry");
		AuditFormat successFormat = getAuditFormat(getSuccessFormat());
		if (logger.isDebugEnabled()) logger.debug("getClassificationRecords - success format: " + successFormat);
		AuditFormat errorFormat = getAuditFormat(getFailFormat());
		if (logger.isDebugEnabled()) logger.debug("getClassificationRecords - error format: " + errorFormat);
		
		Collection<ClassificationRecord> returnData = new ArrayList<ClassificationRecord>();
		
		if (logger.isDebugEnabled()) {
			logger.debug("getClassificationRecords - logData");
			logger.debug(getLogData());
		}
		
		String[] logDataArray = getLogData().split("\n");
		
		StringBuilder stringBuilder = new StringBuilder("");
		for (String logDataLine: logDataArray) {
			String trimmedLogDataLine = rtrim(logDataLine);
			if (trimmedLogDataLine.endsWith("\\")) {
				stringBuilder.append(trimmedLogDataLine.substring(0, trimmedLogDataLine.length()-1));
			} else {
				String[] data;
				if (stringBuilder.length() > 0) {
					stringBuilder.append(trimmedLogDataLine);
					data = splitCells(stringBuilder.toString());
					stringBuilder = new StringBuilder("");
				} else {
					data = splitCells(trimmedLogDataLine);
				}
				if (data.length == 0) continue;
				if (successFormat.isExample(data)) {
					returnData.add(new SuccessRecord(data, successFormat));
				} else {
					returnData.add(new ErrorRecord(data, errorFormat));
				}
				
			}
		}
		if (logger.isDebugEnabled()) logger.debug("getClassificationRecords - exit: " + returnData.size());
		return returnData;
	}
	
	private AuditFormat getAuditFormat(String inputFormat) throws ClassificationException {
		try {
			String[] inputData = CSVParser.parseLine(inputFormat);
			return new AuditFormat(inputData);
		} catch (CSDataFormatException e) {
			throw new ClassificationException("CSDataFormatException reading from: " + inputFormat + ": " + e.getMessage());
		}
	}
	
	private String[] splitCells(String inputData) throws ClassificationException {
		return CSVParser.parseLine(inputData);
	}
	
	// Remove any trailing whitespace
	public String rtrim(String input) {
		if (input == null) return input;
		
		int i = input.length()-1;
		while ((i>0) && Character.isWhitespace(input.charAt(i))) { i--; }
		
		return input.substring(0,i+1);
	}

}
