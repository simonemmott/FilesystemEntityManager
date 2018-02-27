package com.k2.FilesystemEntityManager.metamodel;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;

public class FemSingularAttribute<X, T> implements SingularAttribute<X, T> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Class<T> fieldType;
	private Field field = null;
	private ManagedType<X> managedType;
	private String name;
	
	public FemSingularAttribute(Class<X> classType, Class<T> fieldType, String name) {
		if (classType == null || fieldType == null || name == null) throw new FemError("Null arguemnts permitted for FemSingularAttribute({}, {}, {})",
				classType, fieldType, name);
		managedType = new FemManagedType<X>(classType);
		this.fieldType = fieldType;
		this.name = name;
		logger.trace("Find matching field in class {}", classType.getCanonicalName());
		for (Field f : ClassUtil.getDeclaredFields(classType)) {
			logger.trace("Checking field {} {} matches {} {}", 
					f.getType().getCanonicalName(), 
					f.getName(),
					fieldType.getCanonicalName(),
					name);
			if (f.getName().equals(name) && f.getType().equals(fieldType)) {
				logger.trace("Matched");
				field = f;
				if (!field.isAccessible()) field.setAccessible(true);
				break;
			}
		}
		if (field == null) throw new FemError("Unable to identify suitable field for SingleAttribute <{}>{} on class '{}'", 
				fieldType.getSimpleName(),
				name,
				classType.getCanonicalName());
	}

	@Override
	public ManagedType<X> getDeclaringType() {
		return managedType;
	}

	@Override
	public Member getJavaMember() {
		return field;
	}

	@Override
	public Class<T> getJavaType() {
		return fieldType;
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
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public Type<T> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOptional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVersion() {
		// TODO Auto-generated method stub
		return false;
	}

}
