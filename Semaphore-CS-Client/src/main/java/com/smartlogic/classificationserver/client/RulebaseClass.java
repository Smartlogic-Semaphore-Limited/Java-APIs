package com.smartlogic.classificationserver.client;

public class RulebaseClass {

	private String name;
	private int staticCount;
	private int templatedCount;

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

	/**
	 * We now just support getRuleCount
	 * @return
	 */
	@Deprecated
	public int getStaticCount() {
		return ruleCount;
	}
	public void setStaticCount(int staticCount) {
		this.staticCount = staticCount;
	}

	/**
	 * We now just support getRuleCount
	 * @return
	 */
	@Deprecated
	public int getTemplatedCount() {
		return ruleCount;
	}
	public void setTemplatedCount(int templatedCount) {
		this.templatedCount = templatedCount;
	}

	@Override
	public String toString() {
		return "Name: '" + name + "' Static Count: " + staticCount + "    Templated Count: " + templatedCount;
	}
}

