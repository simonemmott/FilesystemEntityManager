package com.k2.FilesystemEntityManager.testEntities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class BarId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2984701046396520911L;
	
	@Column(name = "ALIAS")
	String alias;
	
	@Column(name = "SEQUENCE")
	Integer sequence;
	
	public BarId(String alias, Integer sequence) {
		this.alias = alias;
		this.sequence = sequence;
	}
	public BarId() {}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarId other = (BarId) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}

	public String getAlias() {
		return alias;
	}
	public Integer getSequence() {
		return sequence;
	}
}
