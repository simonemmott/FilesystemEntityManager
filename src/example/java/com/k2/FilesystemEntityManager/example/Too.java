package com.k2.FilesystemEntityManager.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.k2.FilesystemEntityManager.RootId;
import com.k2.FilesystemEntityManager.example.Too;

@Entity
@Table(name="Toos")
public class Too {

	@Id
	@RootId
	@Column
	@Expose public String id;
	@Column
	@Expose public String name;
	@Column
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
