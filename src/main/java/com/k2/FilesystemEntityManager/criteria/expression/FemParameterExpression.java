package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import com.k2.FilesystemEntityManager.criteria.FemCriteriaBuilder;
import com.k2.FilesystemEntityManager.criteria.FemEvaluator;

public class FemParameterExpression<T> implements ParameterExpression<T>, FemExpression<T>{
	
	Class<T> cls;
	String alias;
	Integer pos;
	T value;
	boolean isBound = false;
	TemporalType temporalType = TemporalType.DATE;
	
	public FemParameterExpression(Class<T> cls) {
		this.cls = cls;
	}

	public FemParameterExpression(Class<T> cls, String alias) {
		this.cls = cls;
		this.alias = alias;
	}

	@Override
	public String getName() {
		return alias;
	}

	public void setPosition(Integer pos) {
		if (this.pos == null) this.pos = pos;
	}
	@Override
	public Integer getPosition() {
		return pos;
	}

	@Override
	public Class<T> getParameterType() {
		return cls;
	}

	@Override
	public FemPredicate isNull() {
		return new PredicateNull(this);
	}

	@Override
	public FemPredicate isNotNull() {
		return new PredicateNotNull(this);
	}

	@Override
	public Predicate in(Object... values) {
		return new PredicateIn(this, values);
	}

	@Override
	public Predicate in(Expression<?>... values) {
		return new PredicateIn(this, values);
	}

	@Override
	public Predicate in(Collection<?> values) {
		return new PredicateIn(this, values);
	}

	@Override
	public Predicate in(Expression<Collection<?>> values) {
		return new PredicateIn(this, values);
	}

	@Override
	public <X> FemExpression<X> as(Class<X> type) {
		return new GenericExpression<X>(this, type);
	}

	@Override
	public Selection<T> alias(String name) {
		return this;
	}

	@Override
	public boolean isCompoundSelection() {
		return false;
	}

	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	@Override
	public Class<? extends T> getJavaType() {
		return cls;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public boolean evaluate(Object obj) {
		if (value instanceof Boolean) return (Boolean)value;
		return false;
	}
	
	public FemParameterExpression<T> setValue(T value) {
		this.value = value;
		isBound = true;
		return this;
	}

	@SuppressWarnings("unchecked")
	public FemParameterExpression<T> setObjectValue(Object value) {
		return setValue((T)value);
	}
	
	public FemParameterExpression<T> setTemporalType(TemporalType temporalType) {
		this.temporalType = temporalType;
		return this;
	}

	@Override
	public T getValue(Object obj) {
		return value;
	}

	public boolean isBound() {
		return isBound;
	}


}
