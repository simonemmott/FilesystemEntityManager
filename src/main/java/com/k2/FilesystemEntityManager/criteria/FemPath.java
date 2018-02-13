package com.k2.FilesystemEntityManager.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.criteria.expression.*;
import com.k2.Util.Identity.IdentityUtil;

public class FemPath<T> extends AbstractExpression<T> implements Path<T>, FemExpression<T> {

	protected Attribute<?, T> attr = null;
	
	private FemPath<?> parentPath = null;
	
	public FemPath(FemPath<?> parentPath, Attribute<?, T> attr) {
		super(attr.getName(), attr.getJavaType());
		this.parentPath = parentPath;
		this.attr = attr;
	}
	public FemPath(Class<T> cls) {
		super(null, cls);
	}
	

	@Override
	public <X> FemExpression<X> as(Class<X> cls) {
		return new GenericExpression<X>(this, cls);
	}


	@Override
	public <Y> Path<Y> get(SingularAttribute<? super T, Y> attribute) {
		return new FemPath<Y>(this, attribute);
	}

	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<T, C, E> arg0) {
		return null;
	}

	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<T, K, V> arg0) {
		return null;
	}

	@Override
	public <Y> Path<Y> get(String arg0) {
		return null;
	}

	@Override
	public Bindable<T> getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPath<?> getParentPath() {
		return parentPath;
	}

	@Override
	public Expression<Class<? extends T>> type() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private T getPathObject(Object rootObj) {
		if (parentPath == null) return (T)rootObj;
		return getValueFromAttribute(parentPath.getPathObject(rootObj));
	}
	
	@SuppressWarnings("unchecked")
	private T getValueFromAttribute(Object obj) {
		if (obj == null) return  null;
		Member m = attr.getJavaMember();
		
		try {
			if (m instanceof Field) {
				Field f = (Field)m;
				if (!f.isAccessible()) f.setAccessible(true);
				return (T) f.get(obj);
			}
			if (m instanceof Method) {
				Method meth = (Method)m;
				if (!meth.isAccessible()) meth.setAccessible(true);
				return (T) meth.invoke(obj);
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new FemError("Unable to get value from member {} of class {}", e, m.getName(), obj.getClass().getName());
		}
		
		throw new FemError("The member {} of class {} is neither a Field or a Method", m.getName(), obj.getClass().getName());
	}


	@Override
	public T getValue(Object rootObj) {
		return getValueFromAttribute(parentPath.getPathObject(rootObj));
	}


}
