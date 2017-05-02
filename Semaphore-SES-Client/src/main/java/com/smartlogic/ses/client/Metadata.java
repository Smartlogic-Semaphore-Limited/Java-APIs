//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Element;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metadata extends AbstractFieldMapElement implements Serializable
{
	private static final long serialVersionUID = -7641444801249874468L;
	// This is required by the XML Marshalling/Unmarshalling
	public Metadata() {}

	protected Metadata(Element element) {
		super(element);
	}

	@Override
	@XmlTransient
	IdentifierField getIdentifierField() {
		return IdentifierField.NAME;
	}
}
