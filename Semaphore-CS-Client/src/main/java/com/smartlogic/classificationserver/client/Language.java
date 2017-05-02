package com.smartlogic.classificationserver.client;

import java.io.Serializable;

public class Language implements Serializable{
	private static final long serialVersionUID = 5361170688046717715L;
	
	private String display;
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String name;
	
	@Override
	public String toString() {
		return "{name : " + name + ", id:" + id + ", display:" + display + "}";
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getDisplay() {
		return display;
	}
}
