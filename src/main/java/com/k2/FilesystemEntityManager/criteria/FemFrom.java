package com.k2.FilesystemEntityManager.criteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public class FemFrom<R,T> extends FemPath<T> implements From<R,T> {
	
	FemFrom(FemPath<?> parentPath, Attribute<? super R, T> attr) {
		super(parentPath, attr);
	}

	public FemFrom(Class<T> cls) {
		super(cls);
	}

	@Override
	public Set<Fetch<T, ?>> getFetches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> Fetch<X, Y> fetch(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Set<Join<T,?>> joins = new HashSet<Join<T,?>>();
	
	private FemJoin<T,?> getCurrentJoin(Attribute<? super T,?> attribute) {
		for (Join<T,?> j : joins) {
			if (j.getAttribute().equals(attribute)) return (FemJoin<T, ?>) j;
		}
		return null;
	}

	@Override
	public Set<Join<T, ?>> getJoins() {
		return joins;
	}

	@Override
	public boolean isCorrelated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public From<R, T> getCorrelationParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemJoin<T, Y> join(SingularAttribute<? super T, Y> attribute) {
		FemJoin<T,Y> j = (FemJoin<T, Y>) getCurrentJoin(attribute);
		if (j == null) {
			j = new FemJoin<T,Y>(this, attribute);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemJoin<T, Y> join(SingularAttribute<? super T, Y> attribute, JoinType jt) {
		FemJoin<T,Y> j = (FemJoin<T, Y>) getCurrentJoin(attribute);
		if (j == null) {
			j = new FemJoin<T,Y>(this, attribute, jt);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemCollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection) {
		FemCollectionJoin<T,Y> j = (FemCollectionJoin<T, Y>) getCurrentJoin(collection);
		if (j == null) {
			j = new FemCollectionJoin<T,Y>(this, collection);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemSetJoin<T, Y> join(SetAttribute<? super T, Y> set) {
		FemSetJoin<T,Y> j = (FemSetJoin<T, Y>) getCurrentJoin(set);
		if (j == null) {
			j = new FemSetJoin<T,Y>(this, set);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemListJoin<T, Y> join(ListAttribute<? super T, Y> list) {
		FemListJoin<T,Y> j = (FemListJoin<T, Y>) getCurrentJoin(list);
		if (j == null) {
			j = new FemListJoin<T,Y>(this, list);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> FemMapJoin<T, K, V> join(MapAttribute<? super T, K, V> map) {
		FemMapJoin<T,K,V> j = (FemMapJoin<T,K,V>) getCurrentJoin(map);
		if (j == null) {
			j = new FemMapJoin<T,K,V>(this, map);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemCollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection, JoinType jt) {
		FemCollectionJoin<T,Y> j = (FemCollectionJoin<T, Y>) getCurrentJoin(collection);
		if (j == null) {
			j = new FemCollectionJoin<T,Y>(this, collection, jt);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemSetJoin<T, Y> join(SetAttribute<? super T, Y> set, JoinType jt) {
		FemSetJoin<T,Y> j = (FemSetJoin<T, Y>) getCurrentJoin(set);
		if (j == null) {
			j = new FemSetJoin<T,Y>(this, set, jt);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> FemListJoin<T, Y> join(ListAttribute<? super T, Y> list, JoinType jt) {
		FemListJoin<T,Y> j = (FemListJoin<T, Y>) getCurrentJoin(list);
		if (j == null) {
			j = new FemListJoin<T,Y>(this, list, jt);
			joins.add(j);
		}
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> MapJoin<T, K, V> join(MapAttribute<? super T, K, V> map, JoinType jt) {
		FemMapJoin<T,K,V> j = (FemMapJoin<T,K,V>) getCurrentJoin(map);
		if (j == null) {
			j = new FemMapJoin<T,K,V>(this, map, jt);
			joins.add(j);
		}
		return j;
	}

	@Override
	public <X, Y> Join<X, Y> join(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

}
