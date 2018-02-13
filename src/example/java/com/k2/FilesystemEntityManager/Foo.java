package com.k2.FilesystemEntityManager;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.k2.Util.Identity.Id;

@Entity()
@Table(name="Foos")
public class Foo implements Id<Foo, String> {
	
	@Expose String id;
	@Expose Integer sequence;
	@Expose String description;
	@Expose Integer intVal;
	@Expose Long	 longVal;
	@Expose Float floatVal;
	@Expose Double doubleVal;
	@Expose Boolean booleanVal;
	@Expose Date dateVal;
	@Expose Too too;
	@Expose Set<Bar> bars;
	
	public String getId() { return id; }
	public Foo setId(String id) {
		this.id = id;
		return this;
	}
	public Foo setId(Serializable key) {
		id = (String) key;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public Foo setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Foo setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getIntVal() {
		return intVal;
	}
	public Foo setIntVal(Integer intVal) {
		this.intVal = intVal;
		return this;
	}
	public Long getLongVal() {
		return longVal;
	}
	public Foo setLongVal(Long longVal) {
		this.longVal = longVal;
		return this;
	}
	public Float getFloatVal() {
		return floatVal;
	}
	public Foo setFloatVal(Float floatVal) {
		this.floatVal = floatVal;
		return this;
	}
	public Double getDoubleVal() {
		return doubleVal;
	}
	public Foo setDoubleVal(Double doubleVal) {
		this.doubleVal = doubleVal;
		return this;
	}
	public Boolean getBooleanVal() {
		return booleanVal;
	}
	public Foo setBooleanVal(Boolean booleanVal) {
		this.booleanVal = booleanVal;
		return this;
	}
	public Date getDateVal() {
		return dateVal;
	}
	public Foo setDateVal(Date dateVal) {
		this.dateVal = dateVal;
		return this;
	}
	public Too getToo() {
		return too;
	}
	public Foo setToo(Too too) {
		this.too = too;
		return this;
	}
	public Foo addBar(Bar bar) {
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
