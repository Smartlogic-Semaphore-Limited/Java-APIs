//----------------------------------------------------------------------
// Product:     Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
//----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "values")
@XmlAccessorType(XmlAccessType.FIELD)
public class Values implements Serializable {
	private static final long serialVersionUID = -4226410042733537586L;
	
	private List<Value> valueList = new ArrayList<Value>();
    public List<Value> getValues() {
        return valueList;
    }
    protected void addValue(Value value) {
    	valueList.add(value);
    }
    public void setValues(List<Value> valueList) {
    	this.valueList = valueList;
    }

}
