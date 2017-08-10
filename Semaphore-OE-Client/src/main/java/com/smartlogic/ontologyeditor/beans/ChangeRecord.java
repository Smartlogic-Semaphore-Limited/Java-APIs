package com.smartlogic.ontologyeditor.beans;

import java.time.Instant;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class ChangeRecord  extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(ChangeRecord.class);

	private final ChangeSet added;
	public ChangeSet getAdded() {
		return added;
	}

	private final ChangeSet deleted;
	public ChangeSet getDeleted() {
		return deleted;
	}
	
	private final Instant committed;
	public Instant getCommitted() {
		return committed;
	}


	public ChangeRecord(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("ChangeRecord - entry: {}", jsonObject);
		this.oeClient = oeClient;
		this.uri = getAsString(jsonObject, "@id");
		
		this.added = new ChangeSet(jsonObject.get("teamwork:added"));
		this.deleted = new ChangeSet(jsonObject.get("teamwork:deleted"));
		
		JsonValue jsonCommitted = jsonObject.get("sem:committed");
		if (jsonCommitted != null) {
			logger.debug("jsonCommitted: {}", jsonCommitted);
			this.committed = Instant.parse(jsonCommitted.getAsArray().get(0).getAsObject().get("@value").getAsString().value());
		}
		else this.committed = null;
	}


	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Change: ").append(this.getUri()).append("\nDate: ").append(this.getCommitted());
		stringBuilder.append("\nDeleted:\n").append(this.deleted.toString());
		stringBuilder.append("Added:\n").append(this.added.toString());
		return stringBuilder.toString();
	}
}
