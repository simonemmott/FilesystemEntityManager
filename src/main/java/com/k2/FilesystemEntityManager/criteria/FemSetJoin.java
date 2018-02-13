package com.k2.FilesystemEntityManager.criteria;


import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SetAttribute;

public class FemSetJoin<R,T> extends FemJoin<R,T> implements SetJoin<R, T> {

	@SuppressWarnings("unchecked")
	public FemSetJoin(FemPath<R> parentPath, SetAttribute<? super R, T> attribute) {
		super(parentPath, (Attribute<? super R, T>)attribute);
	}

	@SuppressWarnings("unchecked")
	public FemSetJoin(FemPath<R> parentPath, SetAttribute<? super R, T> attribute, JoinType joinType) {
		super(parentPath, (Attribute<? super R, T>)attribute, joinType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SetAttribute<? super R,T> getModel() {
		return (SetAttribute<? super R, T>) super.getAttribute();
	}

	@Override
	public SetJoin<R,T> on(Expression<Boolean> restriction) {
		return (SetJoin<R, T>) super.on(restriction);
	}
	
	@Override
	public SetJoin<R,T> on(Predicate... restrictions) {
		return (SetJoin<R, T>) super.on(restrictions);
	}

}
