package com.k2.FilesystemEntityManager.metamodel;

import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public class FemManagedType<X> implements ManagedType<X> {
	
	Class<X> classType;
	FemManagedType(Class<X> classType) {
		this.classType = classType;
	}

	@Override
	public Class<X> getJavaType() {
		return classType;
	}

	@Override
	public PersistenceType getPersistenceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute<? super X, ?> getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Attribute<? super X, ?>> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionAttribute<? super X, ?> getCollection(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> CollectionAttribute<? super X, E> getCollection(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute<X, ?> getDeclaredAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Attribute<X, ?>> getDeclaredAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionAttribute<X, ?> getDeclaredCollection(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> CollectionAttribute<X, E> getDeclaredCollection(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListAttribute<X, ?> getDeclaredList(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> ListAttribute<X, E> getDeclaredList(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapAttribute<X, ?, ?> getDeclaredMap(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> MapAttribute<X, K, V> getDeclaredMap(String arg0, Class<K> arg1, Class<V> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetAttribute<X, ?> getDeclaredSet(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> SetAttribute<X, E> getDeclaredSet(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingularAttribute<X, ?> getDeclaredSingularAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String arg0, Class<Y> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListAttribute<? super X, ?> getList(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> ListAttribute<? super X, E> getList(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapAttribute<? super X, ?, ?> getMap(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> MapAttribute<? super X, K, V> getMap(String arg0, Class<K> arg1, Class<V> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetAttribute<? super X, ?> getSet(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> SetAttribute<? super X, E> getSet(String arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingularAttribute<? super X, ?> getSingularAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String arg0, Class<Y> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
