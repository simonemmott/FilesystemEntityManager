package com.k2.FilesystemEntityManager.example;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.annotations.Expose;
import com.k2.Util.Identity.Id;

@Embeddable
@Table(name="XmlBars")
@XmlRootElement(name = "bar")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "name", "description" })
public class XmlBar implements Id<XmlBar, Integer> {

	@Expose Integer id;
	@Expose String name;
	@Expose String description;

	public Integer getId() { return id; }
	public XmlBar setId(Integer key) {
		id = key;
		return this;
	}
	public String getName() {
		return name;
	}
	public XmlBar setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public XmlBar setDescription(String description) {
		this.description = description;
		return this;
	}
	
	
}
