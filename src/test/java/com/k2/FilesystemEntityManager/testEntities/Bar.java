package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "BARS")
public class Bar {
	
	@EmbeddedId
	public BarId getKey() {
		return new BarId(alias, sequence);
	}
	
	@Column(name = "ALIAS")
	@Expose private String alias;
	
	@Column(name = "SEQUENCE")
	@Expose private Integer sequence;
	
	@Column(name = "NAME")
	@Expose private String name;
	
	protected Bar() {}
	
	public Bar(String alias, Integer sequence) {
		this.alias = alias;
		this.sequence = sequence;
	}

	public String getAlias() {
		return alias;
	}

	public Bar setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public Integer getSequence() {
		return sequence;
	}

	public Bar setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getName() {
		return name;
	}

	public Bar setName(String name) {
		this.name = name;
		return this;
	}
	
	
}
