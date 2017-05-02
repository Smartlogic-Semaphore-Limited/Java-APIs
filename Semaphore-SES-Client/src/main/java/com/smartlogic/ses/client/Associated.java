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

import org.w3c.dom.Element;

@XmlRootElement(name = "associated")
@XmlAccessorType(XmlAccessType.FIELD)
public class Associated extends AbstractFieldMapElement implements Serializable
{
	private static final long serialVersionUID = 8695965166207085230L;

	// This is required by the XML Marshalling/Unmarshalling
	public Associated() {}

	protected Associated(Element element) {
		super(element);
	}

	@Override
	IdentifierField getIdentifierField() {
		return IdentifierField.ID;
	}
}
