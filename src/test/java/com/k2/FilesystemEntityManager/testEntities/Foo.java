package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "FOOS")
public class Foo {
	@Id
	@Column(name="ID")
	@Expose private Long id;
	
	@Column(name="NAME")
	@Expose private String name;
	
	protected Foo() {}
	
	public Foo(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Foo setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Foo setName(String name) {
		this.name = name;
		return this;
	}
	
	
}
