package com.smartlogic.classificationserver.client;

public class RulebaseClass {

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private int ruleCount;
	public int getRuleCount() {
		return ruleCount;
	}
	public void setRuleCount(int ruleCount) {
		this.ruleCount = ruleCount;
	}

	@Override
	public String toString() {
		return "Name: '" + name + "' Rule Count: " + ruleCount;
	}
}

