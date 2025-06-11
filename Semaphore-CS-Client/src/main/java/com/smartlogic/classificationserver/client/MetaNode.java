package com.smartlogic.classificationserver.client;

import java.util.Collection;
import java.util.Map.Entry;

public class MetaNode extends MetadataHoldingObject implements Comparable<MetaNode> {

	private final String name;
	private final String value;
	private final String score;
	private final String id;

	public MetaNode(String name, String value, String score, String id) {
		this.name = name;
		this.value = value;
		this.score = score;
		this.id = id;
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

	public String getId() { return id; }
		
	@Override
	public String toString() {
		StringBuilder stringBuffer = new StringBuilder(String.format(" Name: '%s'  Value: '%s' Score: '%s' Id: '%s'", name, value, score, id));
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

		if (this.id == null && other.id == null) return 0;
		// in these cases we have one or both ids not null
		if (this.id == null) return 1;
		if (other.id == null) return -1;
		// both ids are not null, how do they compare?
		return this.id.compareTo(other.id);
    }
}
