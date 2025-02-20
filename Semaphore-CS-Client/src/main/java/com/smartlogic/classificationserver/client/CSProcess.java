package com.smartlogic.classificationserver.client;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class CSProcess {
	protected static final Logger logger = LoggerFactory.getLogger(CSProcess.class);

	
	public CSProcess(Element element) {
		
		NamedNodeMap namedNodeMap = element.getAttributes();
		if (namedNodeMap != null) {
			for (int a = 0; a < namedNodeMap.getLength(); a++) {
				Attr attributeNode = (Attr) namedNodeMap.item(a);
				if ("id".equals(attributeNode.getName())) {
					setId(Long.parseLong(attributeNode.getValue()));
				} else if ("pid".equals(attributeNode.getName())) {
					setProcessId(Long.parseLong(attributeNode.getValue()));
				} else if ("generation".equals(attributeNode.getName())) {
					setGeneration(Integer.parseInt(attributeNode.getValue()));
				} else if ("status".equals(attributeNode.getName())) {
					setStatus(attributeNode.getValue());
				} else if ("access_count".equals(attributeNode.getName())) {
					setAccessCount(Integer.parseInt(attributeNode.getValue()));
				} else if ("exception_count".equals(attributeNode.getName())) {
					setExceptionCount(Integer.parseInt(attributeNode.getValue()));
				} else if ("bytes".equals(attributeNode.getName())) {
					setBytes(Long.parseLong(attributeNode.getValue()));
				} else if ("port".equals(attributeNode.getName())) {
					setPort(Integer.parseInt(attributeNode.getValue()));
				} else if ("start_time".equals(attributeNode.getName())) {
					setStartTime(new Date((long) Double.parseDouble(attributeNode.getValue())));
				} else if ("stop_time".equals(attributeNode.getName())) {
					double dTime = Double.parseDouble(attributeNode.getValue());
					setStopTime(new Date((long)dTime));
				} else if ("last_used".equals(attributeNode.getName())) {
					setLastUsed(new Date(1000 * Long.parseLong(attributeNode.getValue())));
				} else {
					logger.warn("Unrecognized attribute: '" + attributeNode.getName() + "' (" + this.getClass().getName() + ")");
				}
			}
		}
	}
	
	private int exceptionCount;
	public int getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}


	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long processId;
	public long getProcessId() {
		return processId;
	}
	public void setProcessId(long processId) {
		this.processId = processId;
	}

	private int generation;
	public int getGeneration() {
		return generation;
	}
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	private Date lastUsed;
	private Long bytes;
	public Long getBytes() {
		return bytes;
	}
	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	private Date startTime;
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	private int accessCount;
	public int getAccessCount() {
		return accessCount;
	}
	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	private Date stopTime;
	public Date getStopTime() {
		return stopTime;
	}
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	public Date getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}
	
	private int port = -1;
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Process: ");
		sb.append(" ID: " + id);
		sb.append(" Port: " + port);
		sb.append(" Status : " + status);
		sb.append(" Process ID: " + processId);
		sb.append(" Start Time: " + startTime);
		sb.append(" Stop Time: " + stopTime);
		sb.append(" Last Used: " + lastUsed);
		sb.append(" Generation: " + generation);
		sb.append(" Access Count: "  + accessCount);
		sb.append(" Exception Count: " + exceptionCount);
		return sb.toString();
		
	}
	

}
