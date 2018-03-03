package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.Type;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;

public class MethodSetAttribute<X, T> extends MethodAttribute<X, Set<T>> implements SetAttribute<X, T> {
	
	public MethodSetAttribute(FemManagedType<X> managedType, Method method) {
		super(managedType, method);
	}

	@Override
	public CollectionType getCollectionType() {
		// TODO Auto-generated method stub
		return CollectionType.SET;
	}

	Type<T> elementType;
	@Override
	public Type<T> getElementType() {
		if (elementType != null) return elementType;
		elementType = managedType.metamodel.managedType(getBindableJavaType());
		if (elementType == null) {
			elementType = new FemType<T>(getBindableJavaType());
		}
		return elementType;
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	Class<T> elementClass;
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getBindableJavaType() {
		if  (elementClass != null) return elementClass;
		elementClass = ClassUtil.getMethodGenericTypeClass(method, 0);
		return elementClass;
	}

	@Override
	public BindableType getBindableType() {
		// TODO Auto-generated method stub
		return null;
	}


}
