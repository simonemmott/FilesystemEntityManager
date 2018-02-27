package com.k2.FilesystemEntityManager.util.cache;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.classes.ClassUtil;
import com.k2.Util.classes.Getter;

public class EntityField <E,T> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	final Class<E> onClass;
	final Class<T> type;
	final Field field;
	final Method getterMethod;
	final Method setterMethod;
	final Getter<E,T> getter;
	final String alias;
	final String columnName;
	
	public EntityField(Class<E> entityClass, Class<T> fieldType, Field field) {

		logger.trace("Caching Field '{}' on class {}", field.getName(), entityClass.getName());
		this.onClass = entityClass;
		this.type = fieldType;
		logger.trace("Caching Field '{}' on class {} with type {}", field.getName(), entityClass.getName(), this.type.getName());
		this.field = field;
		Member m = ClassUtil.getGetterMember(entityClass, field.getType(), field.getName());
		if (m instanceof Method) {
			this.getterMethod = (Method)m;
			logger.trace("Caching Field '{}' on class {} with getter method {}()", field.getName(), entityClass.getName(), this.getterMethod.getName());
		} else {
			this.getterMethod = null;
			logger.trace("Caching Field '{}' on class {} with no getter method", field.getName(), entityClass.getName());
		}
		m = ClassUtil.getSetterMember(entityClass, field.getType(), field.getName());
		if (m instanceof Method) {
			this.setterMethod = (Method)m;
			logger.trace("Caching Field '{}' on class {} with setter method {}({})", 
					field.getName(), entityClass.getName(), this.setterMethod.getName(), field.getType().getSimpleName());
		} else {
			this.setterMethod = null;
			logger.trace("Caching Field '{}' on class {} with no setter method", field.getName(), entityClass.getName());
		}
		getter = ClassUtil.getGetter(entityClass, fieldType, field.getName());
		this.alias = field.getName();
		if (field.isAnnotationPresent(Column.class)) {
			this.columnName = field.getAnnotation(Column.class).name();
			logger.trace("Caching Field '{}' on class {} with column name '{}'", field.getName(), entityClass.getName(), this.columnName);
		} else {
			this.columnName = null;
			logger.trace("Caching Field '{}' on class {} with no column name", field.getName(), entityClass.getName());
		}
	}
	
	public Class<E> getOnClass() { return onClass; }
	public Class<T> getType() { return type; }
	public Field getField() { return field; }
	public Method getGetterMethod() { return getterMethod; }
	public Method getSetterMethod() { return setterMethod; }
	public Getter<E,T> getGetter() { return getter; }
	public String getAlias() { return alias; }
	public String getColumnName() { return columnName; }

	
}
	
