package com.smartlogic.classificationserver.client.csvparser;

import java.util.ArrayList;
import java.util.List;

public class CSVParser {

	public static String[] parseLine(String dataLine) {
		if ((dataLine == null) || (dataLine.trim().length() == 0)) return new String[0];
		
		List<String> data = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < dataLine.length(); i++) {
			char c = dataLine.charAt(i);
			if (c == '\\') {
				if (i < (dataLine.length()-1)) {
					if (!(dataLine.charAt(i+1) == '"')) {
						sb.append(c);
					}
					i++;
					sb.append(dataLine.charAt(i));
				}
			} else if (c == '"') {
				inQuotes = !inQuotes;
			} else if (c == ',' && !inQuotes) {
				data.add(sb.toString());
				sb = new StringBuilder();
			} else {
				sb.append(c);
			}
		}

		data.add(sb.toString());
		return data.toArray(new String[data.size()]);
	}

}
