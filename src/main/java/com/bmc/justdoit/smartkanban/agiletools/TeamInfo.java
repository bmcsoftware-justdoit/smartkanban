package com.bmc.justdoit.smartkanban.agiletools;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TeamInfo {
	int id; 
	String name;
	List<SprintInfo> sprints;
	
	
	public TeamInfo(int id, String name, List<SprintInfo> sprints) {
		super();
		this.id = id;
		this.name = name;
		this.sprints = sprints;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SprintInfo> getSprints() {
		return sprints;
	}
	public void setSprints(List<SprintInfo> sprints) {
		this.sprints = sprints;
	} 
	
	
}
