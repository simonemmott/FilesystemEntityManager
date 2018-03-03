package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

public class FieldAttribute<X,Y> implements Attribute<X, Y> {
	
	protected final Field field;
	protected final FemManagedType<X> managedType;
	private PersistentAttributeType persistentAttributeType = PersistentAttributeType.BASIC;
	
	FieldAttribute(FemManagedType<X> managedType, Field field) {
		this.managedType = managedType;
		this.field = field;
		if (field.isAnnotationPresent(ManyToOne.class))
			persistentAttributeType = PersistentAttributeType.MANY_TO_ONE;
	}

	@Override
	public String getName() {
		return field.getName();
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
		return (Class<Y>) field.getType();
	}

	@Override
	public Member getJavaMember() {
		return field;
	}

	@Override
	public boolean isAssociation() {
		return (field.isAnnotationPresent(ManyToOne.class) ||
				field.isAnnotationPresent(OneToMany.class));
	}

	@Override
	public boolean isCollection() {
		return (Collection.class.isAssignableFrom(field.getType()));
	}

}
