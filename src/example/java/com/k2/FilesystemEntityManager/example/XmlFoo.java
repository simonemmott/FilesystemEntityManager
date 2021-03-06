package com.k2.FilesystemEntityManager.example;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.k2.Util.Identity.Id;

@Entity()
@Table(name="XmlFoos")
@XmlRootElement(name = "foo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "sequence", "description", "bars" })
public class XmlFoo implements Id<XmlFoo, String> {
	
	public String id;
	public Integer sequence;
	public String description;
	@XmlElementWrapper(name="bars")
	@XmlElement(name="bar")
	public Set<XmlBar> bars;
	
	public String getId() { return id; }
	public XmlFoo setId(String id) {
		this.id = id;
		return this;
	}
	public XmlFoo setId(Serializable key) {
		id = (String) key;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public XmlFoo setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public XmlFoo setDescription(String description) {
		this.description = description;
		return this;
	}
	public Set<XmlBar> getBars() {
		return bars;
	}
	public void setBars(Set<XmlBar> bars) {
		this.bars = bars;
	}
		

}
