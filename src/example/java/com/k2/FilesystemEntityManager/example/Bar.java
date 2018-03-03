package com.k2.FilesystemEntityManager.example;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.k2.FilesystemEntityManager.example.Bar;

@Embeddable
@Table(name="Bars")
public class Bar implements com.k2.Util.Identity.Id<Bar, Integer> {

	@Id
	@Column
	@Expose Integer id;
	@Column
	@Expose String name;
	@Column
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
