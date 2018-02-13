package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.Type;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.ClassUtil;

public class FemSetAttribute<X, T> implements SetAttribute<X, T> {
	
	private Field field = null;
	private ManagedType<X> managedType;
	private String name;
	private Type<T> elementType;
	
	public FemSetAttribute(Class<X> classType, Class<T> elementClass, String name) {
		if (classType == null || elementClass == null || name == null) throw new FemError("Null arguemnts permitted for FemSetAttribute({}, {}, {})",
				classType, elementClass, name);
		managedType = new FemManagedType<X>(classType);
		elementType = new FemType<T>(elementClass);
		this.name = name;
		for (Field f : ClassUtil.getDeclaredFields(classType)) {
			if (		f.getName().equals(name) && 
					f.getType().isAssignableFrom(Set.class) &&
					((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0].equals(elementClass)) {
				
				field = f;
				
				if (!field.isAccessible()) field.setAccessible(true);
				break;

			}
		}
		if (field == null) throw new FemError("Unable to identify suitable field for SetAttribute <{}>{} on class '{}'", 
				elementClass.getSimpleName(),
				name,
				classType.getCanonicalName());
	}

	@Override
	public CollectionType getCollectionType() {
		// TODO Auto-generated method stub
		return CollectionType.SET;
	}

	@Override
	public Type<T> getElementType() {
		// TODO Auto-generated method stub
		return elementType;
	}

	@Override
	public ManagedType<X> getDeclaringType() {
		// TODO Auto-generated method stub
		return managedType;
	}

	@Override
	public Member getJavaMember() {
		return field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Set<T>> getJavaType() {
		return (Class<Set<T>>) field.getType();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAssociation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public Class<T> getBindableJavaType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindableType getBindableType() {
		// TODO Auto-generated method stub
		return null;
	}


}
