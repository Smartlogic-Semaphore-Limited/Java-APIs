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

@XmlRootElement(name = "hierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hierarchy extends AbstractFieldMapElement implements Serializable
{
	private static final long serialVersionUID = -8724379355263247647L;
	
	// This is required by the XML Marshalling/Unmarshalling
	public Hierarchy() {}

	protected Hierarchy(Element element) {
		super(element);
	}

	@Override
	IdentifierField getIdentifierField() {
		return IdentifierField.ID;
	}

}
