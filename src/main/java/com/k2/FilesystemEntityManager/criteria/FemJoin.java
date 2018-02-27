package com.k2.FilesystemEntityManager.criteria;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;

import com.k2.Expressions.expression.K2Expression;
import com.k2.Expressions.predicate.*;

public class FemJoin<R,T> extends FemFrom<R,T> implements Join<R,T> {
	
	private JoinType joinType;
	private Predicate predicate;
	
	FemJoin(FemPath<R> parentPath, Attribute<? super R, T> attribute) {
		super(parentPath, attribute);
		this.joinType = JoinType.LEFT;
	}

	FemJoin(FemPath<R> parentPath, Attribute<? super R, T> attribute, JoinType joinType) {
		super(parentPath, attribute);
		this.joinType = joinType;
	}

	@Override
	public Join<R, T> on(Expression<Boolean> restriction) {
		this.predicate = new PredicateIsTrue((K2Expression<Boolean>) restriction);
		return this;
	}

	@Override
	public Join<R, T> on(Predicate... restrictions) {
		K2Predicate[] femPredicates = new K2Predicate[restrictions.length];
		for (int i=0; i<restrictions.length; i++) {
			femPredicates[i] = (K2Predicate)restrictions[i];
		}
		this.predicate = new PredicateAnd(femPredicates);
		return this;
	}

	@Override
	public Predicate getOn() {
		if (predicate == null) return new PredicateTrue();
		return predicate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Attribute<? super R, ?> getAttribute() {
		return (Attribute<? super R, ?>) attr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public From<?, R> getParent() {
		Path<?> pPath = getParentPath();
		if (pPath instanceof From) {
			return (From<?, R>) pPath;
		}
		return null;
	}

	@Override
	public JoinType getJoinType() {
		return joinType;
	}

}
