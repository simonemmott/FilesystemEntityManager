package com.k2.FilesystemEntityManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name="Toos")
public class Too {

	@Id
	@RootId
	@Expose public String id;
	@Expose public String name;
	@Expose public String description;
	
	public String getId() { return id; }
	public Too setId(String id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Too setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Too setDescription(String description) {
		this.description = description;
		return this;
	}
}
