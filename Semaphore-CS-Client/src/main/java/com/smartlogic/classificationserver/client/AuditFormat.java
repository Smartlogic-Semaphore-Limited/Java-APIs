package com.smartlogic.classificationserver.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Audit Format that will be used to return the data from the Classification History method.
 *
 * Note that any fields that are not configured (within the Classification Server configuration) to be stored
 * in the logs will not be returned by the Audit Format, in this case null values will be returned when requested.
 * @author Smartlogic Semaphore
 *
 */
public class AuditFormat {
	protected static final Logger logger = LoggerFactory.getLogger(AuditFormat.class);

	public final static String defaultDateFormat = "EEE LLL dd hh:mm:ss yyyy";

	public final static int UNDEFINED_INT = -1;
	public final static float UNDEFINED_FLOAT = -1.0f;
	public final static boolean UNDEFINED_BOOLEAN = false;
	public final static Date UNDEFINED_DATE = new Date(0);

	private enum DataField {
		STARTTIME, 				// The time that the request started processing
        FINISHTIME,				// The time that the request finished processing
        SOURCEIP,				// The ip address of the client sending the request
        PROTOCOLUSED, 			// The protocol used by the client to send the request
        SCORES,					// A comma separated list of document classification scores in format Class.Name:Score
        ERRORNUM,				// The error number for result (is 0 for success)
        ERRORCOMPONENT,			// The component reporting an error (if any)
        ERRORMESSAGE,			// The error message reported (if any)
        OPERATION,				// The operation type for the request
        FILENAME,				// The name of the file (if specified)
        THRESHOLD,				// The threshold used for the classification
        TITLE,					// The title of the document if specified in the request
        SERVERID,				// The id of the server performing the operation
        SINGLEARTICLEMODE,		// SingleArticle or MultiArticle as set in the request
        LEGACYMODE,				// Legacy or Normal as set in request
        CLUSTERINGTYPE,			// Type of clustering to use
        CLUSTERINGTHRESHOLD,	// Clustering threshold
        DOCUMENTSCORELIMIT,		// Limit on number of scores at document level
        DIAGNOSTICSMODE,		// Diagnostics or No Diagnostics as appropriate
        FEEDBACKMODE,			// Feedback of No Feedback as appropriate
        TIMETAKEN,				// Time taken (in milliseconds) for request
        DOCUMENTHASH,			// The MD5 hash of the document text (including META data from request)
        AUDITTAG,				// The optional Audit Tag specified in request
        URL,					// The url that data retrieved from (if specified)
        META_ORIGINALURI		// The meta supplied while classfication (the real file name)
	}

	private static final Map<String, DataField> lookup = new HashMap<String, DataField>();
	static {
		for (DataField dataField: EnumSet.allOf(DataField.class)){
			lookup.put(dataField.name(), dataField);
		}
	}

	private final Map<DataField, Integer> fieldPositions = new HashMap<DataField, Integer>();
	private final Map<Integer, String> fieldFormats = new HashMap<Integer, String>(); // Cannot map from field name, 'cos the same field might be in twice
	private final Map<Integer, String> fixedFields = new HashMap<Integer, String>();

	/**
	 * Create the Audit Format from the string returned within the history request
	 * @param formatStringArray String array containing format
	 * @throws CSDataFormatException Data format exception
	 */
	public AuditFormat(String[] formatStringArray) throws CSDataFormatException {
		for (int fieldIndex = 0; fieldIndex < formatStringArray.length; fieldIndex++) {
			String dataFieldName = formatStringArray[fieldIndex].trim();
			if (dataFieldName.startsWith("$(") && dataFieldName.endsWith(")")) {
				dataFieldName = dataFieldName.substring(2, dataFieldName.length()-1);
				// Is there a : format clause to this column?
				int colonPos;
				if ((colonPos = dataFieldName.indexOf("::")) != -1) {
					String fieldFormat = dataFieldName.substring(colonPos + 2);
					if (fieldFormat.startsWith("\"")) fieldFormat = fieldFormat.substring(1);
					if (fieldFormat.endsWith("\"")) fieldFormat = fieldFormat.substring(0, fieldFormat.length()-1);
					fieldFormats.put(fieldIndex, fieldFormat);

					dataFieldName = dataFieldName.substring(0, colonPos);
				}
				DataField dataField = lookup.get(dataFieldName);

				if (logger.isDebugEnabled())
					logger.debug("Data field name: " + dataFieldName + " Data field: " + dataField + " fieldIndex: " + fieldIndex);

				if (dataField == null){
					logger.warn("Unrecognized data field \"" + dataFieldName + "\" returned in CS format string");
				} else {
					fieldPositions.put(dataField, fieldIndex);
				}
			} else {
				// Keep a record of the fixed fields expected by this format
				fixedFields.put(fieldIndex, dataFieldName);

			}
		}
	}

	/**
	 * Is the data line in the format expected for this AuditFormat. This is tested by checking the fixed format fields to see if they match.
	 * If they match, then it is assume that the data is of this format.
	 * @param data The data line containing the audit for this record
	 * @return True if data matches the AuditFormat
	 */
	public boolean isExample(String[] data) {
		for (int fieldIndex: fixedFields.keySet()) {
			if (data.length < fieldIndex+1) return false; // This data line is too short to be of this format

			if (data[fieldIndex] == null) return false; // There's nothing in this column, but there should be

			if (!data[fieldIndex].trim().equals(fixedFields.get(fieldIndex))) return false; // There's the wrong data in this column
		}
		return true;
	}

	/**
	 * Get the tag attached at to the request at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getAuditTag(String[] data) {
		return getStringField(DataField.AUDITTAG, data);
	}
	/**
	 * Get the clustering threshold used at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public float getClusteringThreshold(String data[]){
		return getFloatField(DataField.CLUSTERINGTHRESHOLD, data);
	}
	/**
	 * Get the clustering type used at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getClusteringType(String[] data) {
		return getStringField(DataField.CLUSTERINGTYPE, data);
	}
	/**
	 * Get the diagnostics mode in use at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getDiagnosticsMode(String[] data) {
		return getStringField(DataField.DIAGNOSTICSMODE, data);
	}
	/**
	 * Get the hash of the document as calculated at classification time. This
	 * can be used to identify document changes
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getDocumentHash(String[] data) {
		return getStringField(DataField.DOCUMENTHASH, data);
	}
	/**
	 * Get the document score limit (i.e. the maximum number of tags
	 * that can be returned) in place at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public Float getDocumentScoreLimit(String[] data) {
		return getFloatField(DataField.DOCUMENTSCORELIMIT, data);
	}
	/**
	 * For an error record, return the component in error
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getErrorComponent(String[] data) {
		return getStringField(DataField.ERRORCOMPONENT, data);
	}
	/**
	 * For an error record, return the message associated with the error
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getErrorMessage(String[] data) {
		return getStringField(DataField.ERRORMESSAGE, data);
	}
	/**
	 * For an error record, return the number of that error
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public int getErrorNum(String[] data) {
		return getIntField(DataField.ERRORNUM, data);
	}
	/**
	 * Return the feedback mode in place at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getFeedbackMode(String[] data) {
		return getStringField(DataField.FEEDBACKMODE, data);
	}
	/**
	 * Get the name of the file actually classified
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getFileName(String[] data) {
		return getStringField(DataField.FILENAME, data);
	}
	/**
	 * Get the time at which classification was completed
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public Date getFinishDateTime(String[] data) {
		return getDateField(DataField.FINISHTIME, data);
	}
	/**
	 * Get the legacy mode flag in place at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getLegacyMode(String[] data) {
		return getStringField(DataField.LEGACYMODE, data);
	}
	/**
	 * Get the name of operation (this is pretty much redudant for classification requests)
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getOperation(String[] data) {
		return getStringField(DataField.OPERATION, data);
	}
	/**
	 * Get the protocol used for the submission of the document
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getProtocolUsed(String[] data) {
		return getStringField(DataField.PROTOCOLUSED, data);
	}
	/**
	 * Return the identity of the classification server
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getServerId(String[] data) {
		return getStringField(DataField.SERVERID, data);
	}
	/**
	 * Get the status of the single article flag at classification time
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getSingleArticleMode(String[] data) {
		return getStringField(DataField.SINGLEARTICLEMODE, data);
	}
	/**
	 * Get the IP address of the requestor for the classification
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getSourceIP(String[] data) {
		return getStringField(DataField.SOURCEIP, data);
	}
	/**
	 * Get the time of the start of the classification process
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public Date getStartDateTime(String[] data) {
		return getDateField(DataField.STARTTIME, data);
	}
	/**
	 * Get the threshold used for the classification request
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public float getThreshold(String data[]){
		return getFloatField(DataField.THRESHOLD, data);
	}
	/**
	 * Get the time take to classify the document
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public float getTimeTaken(String data[]){
		return getFloatField(DataField.TIMETAKEN, data);
	}
	/**
	 * Get the title of the document as extracted by the classification server
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getTitle(String[] data) {
		return getStringField(DataField.TITLE, data);
	}
	/**
	 * Get the URL of the submitted document
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getURL(String[] data) {
		return getStringField(DataField.URL, data);
	}
	/**
	 * Get the original uri of the submitted document
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public String getOriginalURI(String[] data) {
		return getStringField(DataField.META_ORIGINALURI, data);
	}

	/**
	 * Return the scores extracted from the data line. One ClassificationScore object is returned
	 * for each tag returned by Classification Server
	 * @param data The data line containing the audit for this record
	 * @return The value from the data line
	 */
	public Collection<ClassificationScore> getClassificationScores(String[] data) {
		Collection<ClassificationScore> classificationScores = new ArrayList<ClassificationScore>();
		Integer startColumn = fieldPositions.get(DataField.SCORES);
		if (startColumn != null) {
			for (int col = startColumn; col < data.length; col++) {
				try {
					classificationScores.add(new ClassificationScore(data[col]));
				} catch (NotAScoreException e) {
					break;
				}

			}
		}
		return classificationScores;
	}

	private float getFloatField(DataField dataField, String[] data) {
		String stringValue = getStringField(dataField, data);
		try {
			return (stringValue == null) ? UNDEFINED_FLOAT : Float.parseFloat(stringValue);
		} catch (NumberFormatException e) {
			return UNDEFINED_FLOAT;
		}
	}

	private int getIntField(DataField dataField, String[] data) {
		String stringValue = getStringField(dataField, data);
		try {
			return (stringValue == null) ? UNDEFINED_INT : Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			logger.warn("NumberFormatException in Integer field: " + e.getMessage());
			return UNDEFINED_INT;
		}
	}

	private Date getDateField(DataField dataField, String[] data) {
		String stringValue = getStringField(dataField, data);
		if (stringValue == null) return null;

		String dateFormat = fieldFormats.get(fieldPositions.get(dataField));
		if (dateFormat == null) dateFormat = defaultDateFormat;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		try {
			return simpleDateFormat.parse(stringValue);
		} catch (ParseException e) {
			logger.warn("ParseException in Date field (" + dateFormat + "): " + e.getMessage());
			return UNDEFINED_DATE;

		}
	}

	private String getStringField(DataField dataField, String[] data) {
		Integer column = fieldPositions.get(dataField);
		return (column == null) ? null : data[column];
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Field Positions\n");
		for (DataField dataField: fieldPositions.keySet()) {
			stringBuilder.append("   " + dataField.name() +   ":" + fieldPositions.get(dataField) + "\n");
		}
		stringBuilder.append("\nField Formats\n");
		for (Integer column: fieldFormats.keySet()) {
			stringBuilder.append("   " + column +   ":" + fieldFormats.get(column) + "\n");
		}
		stringBuilder.append("\nFixed Fields\n");
		for (Integer column: fixedFields.keySet()) {
			stringBuilder.append("   " + column +   ":" + fixedFields.get(column) + "\n");
		}
		return stringBuilder.toString();
	}
}
