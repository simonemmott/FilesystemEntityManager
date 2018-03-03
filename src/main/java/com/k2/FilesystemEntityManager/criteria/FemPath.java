package com.k2.FilesystemEntityManager.criteria;

import java.lang.invoke.MethodHandles;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Expressions.Evaluator;
import com.k2.Expressions.expression.*;
import com.k2.FilesystemEntityManager.FemError;
import com.k2.Proxy.AProxy;
import com.k2.Util.StringUtil;
import com.k2.Util.Identity.IdentityUtil;

public class FemPath<T> extends AbstractExpression<T> implements Path<T>, K2Expression<T> {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected Attribute<?, T> attr = null;
	
	private FemPath<?> parentPath = null;
	
	public FemPath(FemPath<?> parentPath, Attribute<?, T> attr) {
		super(attr.getName(), attr.getJavaType());
		this.parentPath = parentPath;
		this.attr = attr;
	}
	public FemPath(Class<T> cls) {
		super(cls.getSimpleName(), cls);
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
	public String toString() {
		return "Path: "+this.getAlias()+"("+this.getJavaType().getSimpleName()+")";
	}

	@SuppressWarnings("unchecked")
	T getValueFromRoot(Object rootObj) {
		if (parentPath == null) {
			logger.trace("{} is root, returning root object {}({})", this, rootObj.getClass().getSimpleName(), IdentityUtil.getIdentity(rootObj));
			return (T)rootObj;
		}
		logger.trace("{} is not root, returning attribute value from parent path", this);
		return getValueFromAttribute(parentPath.getValueFromRoot(rootObj));
	}
	
	@SuppressWarnings("unchecked")
	private T getValueFromAttribute(Object obj) {
		if (obj == null) {
			logger.trace("Returning null value from null object");
			return  null;
		}
		Member m = attr.getJavaMember();
		
		try {
			if (m instanceof Field) {
				Field f = (Field)m;
				if (!f.isAccessible()) f.setAccessible(true);
				T value = (T) f.get(obj);
				logger.trace("{} returning {} from field {}.{}:{}", this, StringUtil.toString(value), f.getDeclaringClass().getName(), f.getName(), f.getType().getSimpleName());
				return value;
			}
			if (m instanceof Method) {
				Method meth = (Method)m;
				if (!meth.isAccessible()) meth.setAccessible(true);
				T value = (T) meth.invoke(obj);
				logger.trace("{} returning {} from method {}.{}:{}", this, StringUtil.toString(value), meth.getDeclaringClass().getName(), meth.getName(), meth.getReturnType().getSimpleName());
				return value;
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new FemError("Unable to get value from member {} of class {}", e, m.getName(), obj.getClass().getName());
		}
		
		throw new FemError("The member {} of class {} is neither a Field or a Method", m.getName(), obj.getClass().getName());
	}


	@Override
	public T evaluate(Evaluator eval) {
		if (eval instanceof PathEvaluator) {
			PathEvaluator pEval = (PathEvaluator)eval;
			T value = pEval.valueOf(this);
			logger.trace("{} evaluates to {}", this, StringUtil.toString(value));
			return value;
		}
		return null;
	}


}
