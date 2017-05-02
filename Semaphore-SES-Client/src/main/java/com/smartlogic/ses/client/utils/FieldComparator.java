package com.smartlogic.ses.client.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.smartlogic.ses.client.Field;

public class FieldComparator implements Comparator<Field>, Serializable {
	private static final long serialVersionUID = 1820448518030252069L;

	public enum SortField {
		ALPHABETICAL,
		FREQUENCY
	}

	private SortField sortField;
	
	public FieldComparator(SortField sortField) {
		this.sortField = sortField;
	}
	@Override
	public int compare(Field field0, Field field1) {
		if (sortField == SortField.ALPHABETICAL) return compareValue(field0, field1);
		if (sortField == SortField.FREQUENCY) return compareFrequency(field0, field1);
		
		return 0;
	}
	
	private int compareValue(Field field0, Field field1) {
		if (field0.getValue() != null) {
			if (field1.getValue() != null) {
				return field0.getValue().compareTo(field1.getValue());
			} else {
				return 1;
			}
		} else {
			if (field1.getValue() != null) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	private int compareFrequency(Field field0, Field field1) {
		if (field0.getFrequency() < field1.getFrequency()) return 1;
		if (field0.getFrequency() > field1.getFrequency()) return -1;
		
		return compareValue(field0, field1);
	}

}
