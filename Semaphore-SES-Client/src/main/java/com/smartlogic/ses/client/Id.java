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

@XmlRootElement(name = "id")
@XmlAccessorType(XmlAccessType.FIELD)
public class Id extends AbstractSimpleNode implements Serializable
{
	private static final long serialVersionUID = -5922069417223070189L;
	
	// This is required by the XML Marshalling/Unmarshalling
	public Id() {}

	protected Id(String value) {
		super(value);
	}

	protected Id(Element element) {
		super(element);
	}

}
