package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "TOOS")
public class Too {
	@Id
	@Column(name = "ID")
	@Expose private long id;
	
	@Column(name="FOO_ID")
	@Expose private Long fooId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOO_ID")
	private Foo foo;
	
	@Column(name = "BAR_ALIAS")
	@Expose private String barAlias;
	
	@Column(name = "BAR_SEQUENCE")
	@Expose private Integer barSequence;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "BAR_ALIAS", referencedColumnName = "ALIAS"),
		@JoinColumn(name = "BAR_SEQUENCE", referencedColumnName = "SEQUENCE")
	})
	private Bar bar;
	
	@Column(name = "NAME")
	@Expose private String name;
	
	protected Too() {}
	
	public Too(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Too setId(long id) {
		this.id = id;
		return this;
	}

	public Long getFooId() {
		return fooId;
	}

	public Too setFooId(Long fooId) {
		this.fooId = fooId;
		this.foo = null;
		return this;
	}

	public Foo getFoo() {
		return foo;
	}

	public Too setFoo(Foo foo) {
		this.foo = foo;
		this.fooId = foo.getId();
		return this;
	}

	public String getBarAlias() {
		return barAlias;
	}

	public Too setBarAlias(String barAlias) {
		this.barAlias = barAlias;
		this.bar = null;
		return this;
	}

	public Integer getBarSequence() {
		return barSequence;
	}

	public Too setBarSequence(Integer barSequence) {
		this.barSequence = barSequence;
		this.bar = null;
		return this;
	}

	public Bar getBar() {
		return bar;
	}

	public Too setBar(Bar bar) {
		this.bar = bar;
		this.barAlias = bar.getAlias();
		this.barSequence = bar.getSequence();
		return this;
	}

	public String getName() {
		return name;
	}

	public Too setName(String name) {
		this.name = name;
		return this;
	}
	
	
}
