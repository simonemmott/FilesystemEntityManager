package com.k2.FilesystemEntityManager.metamodel;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;

public class FieldSingularAttribute<X, T> extends FieldAttribute<X,T> implements SingularAttribute<X, T> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private PersistentAttributeType persistentAttributeType = PersistentAttributeType.BASIC;
	
	public FieldSingularAttribute(FemManagedType<X> managedType, Field field) {
		super(managedType, field);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getBindableJavaType() {
		return (Class<T>) field.getType();
	}

	@Override
	public BindableType getBindableType() {
		return BindableType.SINGULAR_ATTRIBUTE;
	}

	private Type<T> type;
	@SuppressWarnings("unchecked")
	@Override
	public Type<T> getType() {
		if (type != null) return type;
		type = managedType.metamodel.managedType((Class<T>)field.getType());
		if (type == null) {
			type =  new FemType<T>((Class<T>) field.getType());
		}
		return type;
	}

	@Override
	public boolean isId() {
		if (field.isAnnotationPresent(Id.class))
			return true;
		if (field.isAnnotationPresent(EmbeddedId.class))
			return true;
		return false;
	}

	@Override
	public boolean isOptional() {
		if (field.isAnnotationPresent(Column.class))
			if (field.getAnnotation(Column.class).nullable())
				return true;
		if (field.isAnnotationPresent(ManyToOne.class))
			if (field.getAnnotation(ManyToOne.class).optional())
				return true;
		return false;
	}

	@Override
	public boolean isVersion() {
		if (field.isAnnotationPresent(Version.class))
			return true;
		return false;
	}

}
