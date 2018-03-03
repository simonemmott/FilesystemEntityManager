package com.k2.FilesystemEntityManager.example;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.FilesystemEntityManager.example.Too;

@Entity()
@Table(name="Foos")
public class Foo implements com.k2.Util.Identity.Id<Foo, String> {
	
	@Id
	@Column
	@Expose public String id;
	@Column
	@Expose public Integer sequence;
	@Column
	@Expose public String description;
	@Column
	@Expose public Integer intVal;
	@Column
	@Expose public Long	 longVal;
	@Column
	@Expose public Float floatVal;
	@Column
	@Expose public Double doubleVal;
	@Column
	@Expose public Boolean booleanVal;
	@Column
	@Expose public Date dateVal;
	@ManyToOne
	@Expose public Too too;
	@OneToMany
	@Expose public Set<Bar> bars;
	
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
