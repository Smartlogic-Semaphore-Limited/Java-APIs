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

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message extends AbstractSimpleNode implements Serializable
{
	private static final long serialVersionUID = -6191569176985601460L;

	// This is required by the XML Marshalling/Unmarshalling
	public Message() {}

	protected Message(Element element) {
		super(element);
	}

	public Message(String nodeValue) {
		super(nodeValue);
	}
}
