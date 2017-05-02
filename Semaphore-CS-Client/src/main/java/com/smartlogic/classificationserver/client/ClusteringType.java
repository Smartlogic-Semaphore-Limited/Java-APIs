package com.smartlogic.classificationserver.client;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ClusteringType implements Serializable{
	private static final long serialVersionUID = 7687985249449614521L;
	private String name;
	private String display;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getDisplay() {
		return display;
	}
	public ClusteringType(String name, String display){
		this.name = name;
		this.display = display;
	}
	public final static List<ClusteringType> listClusteringTypes(){
		List<ClusteringType> ret = new LinkedList<ClusteringType>();
		ret.add(new ClusteringType("DEFAULT", "Default"));
		ret.add(new ClusteringType("ALL", "All"));
		ret.add(new ClusteringType("AVERAGE", "Average (Scored articles only)"));
		ret.add(new ClusteringType("COMMON", "Common (Scored articles only)"));
		ret.add(new ClusteringType("NONE","None"));
		ret.add(new ClusteringType("RMS","RMS (Scored articles only)"));
		ret.add(new ClusteringType("RMS_INCLUDING_EMPTY", "RMS (All articles)"));
		ret.add(new ClusteringType("AVERAGE_INCLUDING_EMPTY", "Average (All articles)"));
		ret.add(new ClusteringType("COMMON_INCLUDING_EMPTY", "Common (All articles)"));
		return ret;
	}
}
