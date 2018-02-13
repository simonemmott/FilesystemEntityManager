package com.k2.FilesystemEntityManager.criteria;


import java.util.Map.Entry;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;

public class FemMapJoin<R,K,T> extends FemJoin<R,T> implements MapJoin<R,K,T> {

	@SuppressWarnings("unchecked")
	public FemMapJoin(FemPath<R> parentPath, MapAttribute<? super R,K,T> attribute) {
		super(parentPath, (Attribute<? super R, T>)attribute);
	}

	@SuppressWarnings("unchecked")
	public FemMapJoin(FemPath<R> parentPath, MapAttribute<? super R,K,T> attribute, JoinType joinType) {
		super(parentPath, (Attribute<? super R, T>)attribute, joinType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MapAttribute<? super R,K,T> getModel() {
		return (MapAttribute<? super R,K,T>) super.getAttribute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapJoin<R,K,T> on(Expression<Boolean> restriction) {
		return (MapJoin<R,K,T>) super.on(restriction);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MapJoin<R,K,T> on(Predicate... restrictions) {
		return (MapJoin<R,K,T>) super.on(restrictions);
	}

	@Override
	public Path<K> key() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path<T> value() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<Entry<K, T>> entry() {
		// TODO Auto-generated method stub
		return null;
	}

}
