package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ChangeRecord;

public class GetChangesSince extends ModelManipulation {
	public static void main(String args[]) throws IOException, CloudException, OEClientException {
		runTests(new GetChangesSince());
	}

	@Override
	protected void alterModel(OEClientReadWrite oeClient) throws OEClientException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = simpleDateFormat.parse("2017-06-01");

			Collection<ChangeRecord> changeRecords = oeClient.getChangesSince(date);

			for (ChangeRecord changeRecord : changeRecords) {
				System.err.println(changeRecord);
			}
		} catch (ParseException e) {
			System.err.println("Date parse exception - shouldn't happen");
		}
	}

}
