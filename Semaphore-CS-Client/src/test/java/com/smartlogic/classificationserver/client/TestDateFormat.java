package com.smartlogic.classificationserver.client;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import com.ibm.icu.text.SimpleDateFormat;

public class TestDateFormat extends TestCase {

	public void testDateFormat() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		
		Date date = new Date();
		String formattedDate = simpleDateFormat.format(date);
		System.out.println(formattedDate);
		
		Date newDate = simpleDateFormat.parse(formattedDate);
		System.out.println(newDate);
	}
}
