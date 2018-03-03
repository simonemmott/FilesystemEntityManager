package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.persistence.ManyToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

public class MethodAttribute<X,Y> implements Attribute<X, Y> {
	
	protected final Method method;
	protected final FemManagedType<X> managedType;
	private PersistentAttributeType persistentAttributeType = PersistentAttributeType.BASIC;
	
	MethodAttribute(FemManagedType<X> managedType, Method method) {
		this.managedType = managedType;
		this.method = method;
		if (method.isAnnotationPresent(ManyToOne.class))
			persistentAttributeType = PersistentAttributeType.MANY_TO_ONE;
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return persistentAttributeType;
	}

	@Override
	public ManagedType<X> getDeclaringType() {
		return managedType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Y> getJavaType() {
		return (Class<Y>) method.getReturnType();
	}

	@Override
	public Member getJavaMember() {
		return method;
	}

	@Override
	public boolean isAssociation() {
		return method.isAnnotationPresent(ManyToOne.class);
	}

	@Override
	public boolean isCollection() {
		return (Collection.class.isAssignableFrom(method.getReturnType()));
	}

}
