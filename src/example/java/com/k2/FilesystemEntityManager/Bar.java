package com.k2.FilesystemEntityManager;

import com.google.gson.annotations.Expose;
import com.k2.Util.Identity.Id;

public class Bar implements Id<Bar, Integer> {

	@Expose Integer id;
	@Expose String name;
	@Expose String description;
	Foo foo;
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
