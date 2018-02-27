package com.k2.FilesystemEntityManager.example;

import javax.persistence.Embeddable;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.Util.Identity.Id;

@Embeddable
@Table(name="Bars")
public class Bar implements Id<Bar, Integer> {

	@Expose Integer id;
	@Expose String name;
	@Expose String description;

	public Integer getId() { return id; }
	public Bar setId(Integer key) {
		id = key;
		return this;
	}
	public String getName() {
		return name;
	}
	public Bar setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Bar setDescription(String description) {
		this.description = description;
		return this;
	}
}
