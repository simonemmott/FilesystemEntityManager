package com.k2.FilesystemEntityManager.criteria;


import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SetAttribute;

public class FemCollectionJoin<R,T> extends FemJoin<R,T> implements CollectionJoin<R, T> {

	@SuppressWarnings("unchecked")
	public FemCollectionJoin(FemPath<R> parentPath, CollectionAttribute<? super R, T> attribute) {
		super(parentPath, (Attribute<? super R, T>)attribute);
	}

	@SuppressWarnings("unchecked")
	public FemCollectionJoin(FemPath<R> parentPath, CollectionAttribute<? super R, T> attribute, JoinType joinType) {
		super(parentPath, (Attribute<? super R, T>)attribute, joinType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CollectionAttribute<? super R,T> getModel() {
		return (CollectionAttribute<? super R, T>) super.getAttribute();
	}

	@Override
	public CollectionJoin<R,T> on(Expression<Boolean> restriction) {
		return (CollectionJoin<R, T>) super.on(restriction);
	}
	
	@Override
	public CollectionJoin<R,T> on(Predicate... restrictions) {
		return (CollectionJoin<R, T>) super.on(restrictions);
	}

}
