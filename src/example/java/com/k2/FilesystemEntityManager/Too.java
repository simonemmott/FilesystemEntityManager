package com.k2.FilesystemEntityManager;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class Too {

	@Expose public String id;
	@Expose public Integer sequence;
	@Expose public String description;
	@Expose public Set<Bar> bars;
	
	public String getId() { return id; }
	public Too setId(String id) {
		this.id = id;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public Too setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Too setDescription(String description) {
		this.description = description;
		return this;
	}
	public Too addBar(Bar bar) {
		if (bars == null) bars = new HashSet<Bar>();
		bars.add(bar);
		return this;
	}
	public Set<Bar> getBars() {
		return bars;
	}
	public Set<Bar> setBars(Set<Bar> bars) {
		this.bars = bars;
		return bars;
	}
	
}
