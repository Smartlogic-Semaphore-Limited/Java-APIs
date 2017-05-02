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

@XmlRootElement(name = "frequency")
@XmlAccessorType(XmlAccessType.FIELD)
public class Frequency extends AbstractSimpleNode implements Serializable
{
	private static final long serialVersionUID = -5240378626576474900L;
	
	// This is required by the XML Marshalling/Unmarshalling
	public Frequency() {}

	protected Frequency(Element element) {
		super(element);
	}

}
