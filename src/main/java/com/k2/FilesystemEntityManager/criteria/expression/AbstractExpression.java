package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import com.k2.FilesystemEntityManager.criteria.FemPath;

public abstract class AbstractExpression<T> implements FemExpression<T> {
	
	private String alias;
	
	private Class<? extends T> javaType;
	
	
	protected AbstractExpression(String alias, Class<? extends T> javaType) {
		this.alias = alias;
		this.javaType = javaType;
	}

	protected AbstractExpression(Class<? extends T> javaType) {
		this.javaType = javaType;
	}

	protected AbstractExpression(String alias) {
		this.alias = alias;
	}

	@Override
	public <X> FemExpression<X> as(Class<X> cls) {
		return new GenericExpression<X>(this, cls);
	}

	@Override
	public Selection<T> alias(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	@Override
	public boolean isCompoundSelection() {
		return false;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public Class<? extends T> getJavaType() {
		return javaType;
	}


	public FemPredicate getTrue() { return AbstractPredicate.getTrue(); }
	public FemPredicate getFalse() { return AbstractPredicate.getFalse(); }

	@Override
	public FemPredicate in(Object... objects) {
		return new PredicateIn(this, objects);
	}

	@Override
	public FemPredicate in(Expression<?>... expressions) {
		return new PredicateIn(this, expressions);
	}

	@Override
	public FemPredicate in(Collection<?> collection) {
		return new PredicateIn(this, collection);
	}

	@Override
	public FemPredicate in(Expression<Collection<?>> expr) {
		return new PredicateIn(this, expr);
	}

	@Override
	public FemPredicate isNotNull() {
		return new PredicateNotNull(this);
	}

	@Override
	public FemPredicate isNull() {
		return new PredicateNull(this);
	}



}
