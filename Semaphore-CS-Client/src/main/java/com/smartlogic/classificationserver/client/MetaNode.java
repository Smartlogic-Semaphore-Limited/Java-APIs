package com.smartlogic.classificationserver.client;

import java.util.Collection;
import java.util.Map.Entry;

public class MetaNode extends MetadataHoldingObject implements Comparable<MetaNode> {

	private final String name;
	private final String value;
	private final String score;

	public MetaNode(String name, String value, String score) {
		this.name = name;
		this.value = value;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getScore() {
		return score;
	}
		
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(String.format(" Name: '%s'  Value: '%s' Score: '%s'", name, value, score));
		for (Entry<String, Collection<MetaNode>> entry: this.getMetaNodes().entrySet()) {
			stringBuffer.append(String.format("[ KEY: %s : ", entry.getKey()));
			for (MetaNode metaNode: entry.getValue()) {
				stringBuffer.append(metaNode.toString());
			}
			stringBuffer.append("]");
		}
		return stringBuffer.toString();
	}

	@Override
	public int compareTo(MetaNode other) {
		int nameResult = this.name.compareTo(other.name);
		if (nameResult != 0) return nameResult;

		int valueResult = this.value.compareTo(other.value);
		if (valueResult != 0) return valueResult;

		int scoreResult = this.score.compareTo(other.score);
		if (scoreResult != 0) return scoreResult;

		return 0;
	}
}
