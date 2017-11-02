package com.smartlogic.ontologyeditor.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadWrite;
import com.smartlogic.ontologyeditor.beans.ChangeRecord;

public class GetChangesSince extends ModelManipulation {
	public static void main(String args[]) throws URISyntaxException, OEClientException, ParseException, IOException {
		OEClientReadWrite oeClient = getOEClient(false);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = simpleDateFormat.parse("2017-06-01");
		
		Collection<ChangeRecord> changeRecords = oeClient.getChangesSince(date);

		for (ChangeRecord changeRecord: changeRecords) {
			System.out.println(changeRecord);
		}
	}

}
