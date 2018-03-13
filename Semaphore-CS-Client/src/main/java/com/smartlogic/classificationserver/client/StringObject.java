package com.smartlogic.classificationserver.client;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.content.StringBody;

public abstract class StringObject {

	protected abstract String getValue();
	protected abstract String getParameterName();
	
	private final static ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
	public FormBodyPart asFormPart() {
		return FormBodyPartBuilder.create(getParameterName(), new StringBody(getValue(), contentType)).build();
	}
}
