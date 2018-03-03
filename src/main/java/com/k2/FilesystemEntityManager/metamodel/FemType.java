package com.k2.FilesystemEntityManager.metamodel;

import javax.persistence.metamodel.Type;

public class FemType<T> implements Type<T> {
	Class<T> javaType;
	FemType(Class<T> javaType) {
		this.javaType = javaType;
	}

	@Override
	public Class<T> getJavaType() {
		return javaType;
	}

	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.BASIC;
	}

}
