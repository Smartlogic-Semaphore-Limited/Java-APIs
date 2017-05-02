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

@XmlRootElement(name = "name")
@XmlAccessorType(XmlAccessType.FIELD)
public class Name extends AbstractSimpleNode implements Serializable
{
	private static final long serialVersionUID = -296644099319099842L;
	// This is required by the XML Marshalling/Unmarshalling
	public Name() {}

	protected Name(String value) {
		super(value);
	}
	
	protected Name(Element element) {
		super(element);
	}
	
}
