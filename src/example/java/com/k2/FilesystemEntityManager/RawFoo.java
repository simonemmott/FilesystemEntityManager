package com.k2.FilesystemEntityManager;

import java.io.Serializable;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.k2.Util.Identity.Id;

public class RawFoo implements Id<RawFoo, String> {
	
	@Expose String id;
	@Expose Integer sequence;
	@Expose String description;
	@Expose Set<Bar> bars;
	
	public String getId() { return id; }
	public RawFoo setId(String id) {
		this.id = id;
		return this;
	}
	public RawFoo setId(Serializable key) {
		id = (String) key;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public RawFoo setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public RawFoo setDescription(String description) {
		this.description = description;
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
