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


@XmlRootElement(name = "parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter extends AbstractNamedElement implements Serializable
{
	private static final long serialVersionUID = -2914236686984178696L;
	
	// This is required by the XML Marshalling/Unmarshalling
	public Parameter() {}

	protected Parameter(Element element) {
		super(element);
	}
}
