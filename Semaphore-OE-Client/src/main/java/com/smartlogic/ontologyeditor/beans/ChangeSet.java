package com.smartlogic.ontologyeditor.beans;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeSet {
	protected final static Logger logger = LoggerFactory.getLogger(ChangeRecord.class);

	private final Collection<Triple> triples = new ArrayList<Triple>();
	public Collection<Triple> getTriples() {
		return triples;
	}

	public ChangeSet(JsonValue jsonValue) {
		logger.debug("ChangeSet - entry: {}", jsonValue);
		if (jsonValue == null) return;
		JsonArray jsonArray = jsonValue.getAsArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			triples.add(new Triple(jsonArray.get(i).getAsObject()));
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Triple triple: triples) stringBuilder.append(triple.toString()).append("\n");
		return stringBuilder.toString();
	}
}
