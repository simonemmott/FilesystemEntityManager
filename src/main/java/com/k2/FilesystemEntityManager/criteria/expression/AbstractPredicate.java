package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

public abstract class AbstractPredicate implements Predicate, FemPredicate {
	
	private String alias;
	private boolean negated = false;
	BooleanOperator operator = null;
	

	AbstractPredicate() {
	}
	
	boolean isNegatedRVal(boolean rVal) {
		return (isNegated()) ? !rVal: rVal;
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
	public Predicate isNotNull() {
		return new PredicateNotNull(this);
	}

	@Override
	public Predicate isNull() {
		return new PredicateNull(this);
	}

	@Override
	public List<Expression<Boolean>> getExpressions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> Expression<X> as(Class<X> cls) {
		return new GenericExpression<X>(this, cls);
	}

	AbstractPredicate(String alias) {
		this.alias = alias;
	}

	AbstractPredicate(BooleanOperator operator) {
		this.operator = operator;
	}

	AbstractPredicate(String alias, BooleanOperator operator) {
		this.alias = alias;
		this.operator = operator;
	}
	
	public static FemPredicate getTrue() { return new PredicateTrue(); }
	public static FemPredicate getFalse() { return new PredicateFalse(); }

	@Override
	public Selection<Boolean> alias(String alias) {
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
	public Class<? extends Boolean> getJavaType() {
		return Boolean.class;
	}

	@Override
	public boolean isNegated() {
		return negated;
	}

	@Override
	public FemPredicate not() {
		negated = true;
		return this;
	}

	@Override
	public BooleanOperator getOperator() {
		return operator;
	}

	@Override
	public Boolean getValue(Object obj) {
		return evaluate(obj);
	}


}
