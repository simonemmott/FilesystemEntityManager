package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;

public class FemManagedType<X> implements ManagedType<X> {
	
	protected Class<X> managedType;
	protected FemMetamodel metamodel;
	
	FemManagedType(FemMetamodel metamodel, Class<X> managedType) {
		this.metamodel = metamodel;
		this.managedType = managedType;	
		if (managedType.isAnnotationPresent(Entity.class))
			persistenceType = PersistenceType.ENTITY;
		else if (managedType.isAnnotationPresent(Embeddable.class))
			persistenceType = PersistenceType.EMBEDDABLE;
		else if (managedType.isAnnotationPresent(MappedSuperclass.class))
			persistenceType = PersistenceType.MAPPED_SUPERCLASS;
		
		for (Field f : ClassUtil.getDeclaredFields(managedType)) 
			getAttributeFromField(f);
		
		for (Method m : ClassUtil.getDeclaredMethods(managedType)) 
			getAttributeFromMethod(m);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Attribute<X,?>  getAttributeFromField(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Attribute<X,?> attr = new FieldSingularAttribute(this, field);
			attributeMap.put(field.getName(), attr);
			return attr;
		}
		if (field.isAnnotationPresent(ManyToOne.class)) {
			Attribute<X,?> attr = new FieldSingularAttribute(this, field);
			attributeMap.put(field.getName(), attr);
			return attr;
		}
		if (field.isAnnotationPresent(OneToMany.class)) {
			if (Collection.class.isAssignableFrom(field.getType())) {
				if (Set.class.isAssignableFrom(field.getType())) {
					Attribute<X,?> attr = new FieldSetAttribute(this, field);
					attributeMap.put(field.getName(), attr);
					return attr;
				}
				return null;
			} else if (Map.class.isAssignableFrom(field.getType())) {
				return null;
			}
		}
		return null;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Attribute<X,?>  getAttributeFromMethod(Method method) {
		if (method.isAnnotationPresent(Column.class)) {
			Attribute<X,?> attr = new MethodSingularAttribute(this, method);
			attributeMap.put(method.getName(), attr);
			return attr;
		}
		if (method.isAnnotationPresent(ManyToOne.class)) {
			Attribute<X,?> attr = new MethodSingularAttribute(this, method);
			attributeMap.put(method.getName(), attr);
			return attr;
		}
		if (method.isAnnotationPresent(OneToMany.class)) {
			if (Collection.class.isAssignableFrom(method.getReturnType())) {
				if (Set.class.isAssignableFrom(method.getReturnType())) {
					Attribute<X,?> attr = new MethodSetAttribute(this, method);
					attributeMap.put(method.getName(), attr);
					return attr;
				}
				return null;
			} else if (Map.class.isAssignableFrom(method.getReturnType())) {
				return null;
			}
		}
		return null;
	}

	@Override
	public Class<X> getJavaType() {
		return managedType;
	}

	PersistenceType persistenceType = PersistenceType.BASIC;
	@Override
	public PersistenceType getPersistenceType() {
		return persistenceType;
	}
	
	protected Map<String, Attribute<X, ?>> attributeMap = new HashMap<String, Attribute<X, ?>>();

	@Override
	public Attribute<? super X, ?> getAttribute(String name) {
		Attribute<? super X, ?> attr = attributeMap.get(name);
		if (attr != null) return attr;
		ManagedType<? super X> superMt = metamodel.managedSuperType(managedType);
		if (superMt != null)
			return superMt.getAttribute(name);
		throw new FemError("No attribute named {} on the managed type {} or its super types", name, managedType.getName());
	}
	
	public FemManagedType<? super X> getSuperManagedType() {
		return metamodel.managedSuperType(managedType);
	}

	@Override
	public Set<Attribute<? super X, ?>> getAttributes() {
		Set<Attribute<? super X, ?>> set = new HashSet<Attribute<? super X, ?>>();
		set.addAll(getDeclaredAttributes());
		FemManagedType<? super X> superMt = getSuperManagedType();
		while (superMt != null) {
			set.addAll(superMt.getDeclaredAttributes());
			superMt = superMt.getSuperManagedType();
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CollectionAttribute<? super X, ?> getCollection(String name) {
		Attribute<? super X, ?> attr = getAttribute(name);
		if (attr instanceof CollectionAttribute)
			return (CollectionAttribute<? super X, ?>)attr;
		throw new FemError("The attribute named {} is not a collection on the managed type {} or its super types", name, managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
		Attribute<? super X, ?> attr = getAttribute(name);
		if (attr instanceof CollectionAttribute && ((CollectionAttribute)attr).getElementType().getJavaType().equals(elementType))
			return (CollectionAttribute<? super X, E>)attr;
		throw new FemError("The attribute named {} is not a collection of {} on the managed type {} or its super types", name, elementType.getName(), managedType.getName());
	}

	@Override
	public Attribute<X, ?> getDeclaredAttribute(String name) {
		Attribute<X, ?> attr = attributeMap.get(name);
		if (attr == null)
			throw new FemError("No attribute named {} on the managed type {}", name, managedType.getName());
		return attr;
	}

	@Override
	public Set<Attribute<X, ?>> getDeclaredAttributes() {
		Set<Attribute<X, ?>> set = new HashSet<Attribute<X, ?>>();
		for (Attribute<X, ?> a : attributeMap.values())
			set.add(a);
		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof CollectionAttribute)
			return (CollectionAttribute<X, ?>)attr;
		throw new FemError("The attribute named {} is not a collection on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof CollectionAttribute && ((CollectionAttribute)attr).getElementType().getJavaType().equals(elementType))
			return (CollectionAttribute<X, E>)attr;
		throw new FemError("The attribute named {} is not a collection of {} on the managed type {}", name, elementType.getName(), managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ListAttribute<X, ?> getDeclaredList(String name) {
		CollectionAttribute<X, ?> coll = getDeclaredCollection(name);
		if (coll instanceof ListAttribute) {
			return (ListAttribute)coll;
		}
		throw new FemError("The attribute named {} is not a list on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
		CollectionAttribute<X, ?> coll = getDeclaredCollection(name);
		if (coll instanceof ListAttribute  && ((ListAttribute)coll).getElementType().getJavaType().equals(elementType)) {
			return (ListAttribute)coll;
		}
		throw new FemError("The attribute named {} is not a list of {} on the managed type {}", name, elementType.getName(), managedType.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof MapAttribute)
			return (MapAttribute<X,?,?>)attr;
		throw new FemError("The attribute named {} is not a map attribute on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof MapAttribute && ((MapAttribute)attr).getKeyJavaType().equals(keyType) && ((MapAttribute)attr).getJavaType().equals(valueType))
			return (MapAttribute<X,K,V>)attr;
		throw new FemError("The attribute named {} is not a map attribute on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
		Set<PluralAttribute<X, ?, ?>> set = new HashSet<PluralAttribute<X, ?, ?>>();
		for (Attribute<X, ?> attr : attributeMap.values()) {
			if (attr.isCollection()) set.add((PluralAttribute<X, ?, ?>)attr);
		}
		return set;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public SetAttribute<X, ?> getDeclaredSet(String name) {
		CollectionAttribute<X, ?> coll = getDeclaredCollection(name);
		if (coll instanceof SetAttribute) {
			return (SetAttribute)coll;
		}
		throw new FemError("The attribute named {} is not a set on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
		CollectionAttribute<X, ?> coll = getDeclaredCollection(name);
		if (coll instanceof SetAttribute  && ((SetAttribute)coll).getElementType().getJavaType().equals(elementType)) {
			return (SetAttribute)coll;
		}
		throw new FemError("The attribute named {} is not a set of {} on the managed type {}", name, elementType.getName(), managedType.getName());
	}

	@Override
	public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof SingularAttribute)
			return (SingularAttribute<X, ?>)attr;
		throw new FemError("The attribute named {} is not a singular attribute on the managed type {}", name, managedType.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> elementType) {
		Attribute<X, ?> attr = getDeclaredAttribute(name);
		if (attr instanceof SingularAttribute && attr.getJavaType().equals(elementType))
			return (SingularAttribute<X, Y>)attr;
		throw new FemError("The attribute named {} is not a singular attribute of type {} on the managed type {}", name, elementType.getName(), managedType.getName());
	}

	@Override
	public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
		Set<SingularAttribute<X, ?>> set = new HashSet<SingularAttribute<X, ?>>();
		for (Attribute<X, ?> attr : attributeMap.values()) {
			if (attr instanceof SingularAttribute) set.add((SingularAttribute<X, ?>)attr);
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListAttribute<? super X, ?> getList(String name) {
		Attribute<? super X, ?> attr = attributeMap.get(name);
		if (attr != null && attr instanceof ListAttribute) 
			return (ListAttribute<? super X, ?>)attr;
		if (attr != null) 
			throw new FemError("The attribute named {} is not a list attribute on the managed type {}", name, managedType.getName()); 
		FemManagedType<? super X> superMt = this.getSuperManagedType();
		if (superMt == null) 
			throw new FemError("No attribute named {} defined on the managed type {} or its super types", name, managedType.getName());
		return superMt.getList(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> ListAttribute<? super X, E> getList(String name, Class<E> elemenmtType) {
		Attribute<? super X, ?> attr = attributeMap.get(name);
		if (attr != null && attr instanceof ListAttribute && ((ListAttribute)attr).getElementType().getJavaType().equals(elemenmtType)) 
			return (ListAttribute<? super X, E>)attr;
		if (attr != null) 
			throw new FemError("The attribute named {} is not a list attribute on the managed type {} or its super types.", name, managedType.getName()); 
		FemManagedType<? super X> superMt = this.getSuperManagedType();
		if (superMt == null) 
			throw new FemError("No attribute named {} defined on the managed type {} or its super types", name, managedType.getName());
		return superMt.getList(name, elemenmtType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapAttribute<? super X, ?, ?> getMap(String name) {
		Attribute<? super X, ?> attr = attributeMap.get(name);
		if (attr != null && attr instanceof MapAttribute) 
			return (MapAttribute<? super X, ?, ?>)attr;
		if (attr != null) 
			throw new FemError("The attribute named {} is not a map attribute on the managed type {} or its super types.", name, managedType.getName()); 
		FemManagedType<? super X> superMt = this.getSuperManagedType();
		if (superMt == null) 
			throw new FemError("No attribute named {} defined on the managed type {} or its super types", name, managedType.getName());
		return superMt.getMap(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
		Attribute<? super X, ?> attr = attributeMap.get(name);
		if (attr != null && attr instanceof MapAttribute && ((MapAttribute)attr).getKeyJavaType().equals(keyType) && ((MapAttribute)attr).getElementType().getJavaType().equals(valueType)) 
			return (MapAttribute<? super X, K, V>)attr;
		if (attr != null) 
			throw new FemError("The attribute named {} is not a map attribute of {} and {} on the managed type {} or its super types.", name, keyType.getName(), valueType.getName(), managedType.getName()); 
		FemManagedType<? super X> superMt = this.getSuperManagedType();
		if (superMt == null) 
			throw new FemError("No attribute named {} defined on the managed type {} or its super types", name, managedType.getName());
		return superMt.getMap(name, keyType, valueType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
		Set<PluralAttribute<? super X, ?, ?>> set = new HashSet<PluralAttribute<? super X, ?, ?>>();
		for (Attribute<? super X, ?> attr : this.getAttributes()) 
			if (attr instanceof PluralAttribute)
				set.add((PluralAttribute<? super X, ?, ?>)attr);
		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SetAttribute<? super X, ?> getSet(String name) {
		Attribute<? super X, ?> attr = this.getAttribute(name);
		if (attr instanceof SetAttribute)
			return (SetAttribute<? super X, ?>)attr;
		throw new FemError("The attribute named {} is not a set attribute on the managed type {} or its super types", name, managedType.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
		Attribute<? super X, ?> attr = this.getAttribute(name);
		if (attr instanceof SetAttribute && ((SetAttribute)attr).getElementType().getJavaType().equals(elementType))
			return (SetAttribute<? super X, E>)attr;
		throw new FemError("The attribute named {} is not a set attribute of {} on the managed type {} or its super types", name, elementType.getName(), managedType.getName());
	}

	@Override
	public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
		Attribute<? super X, ?> attr = this.getAttribute(name);
		if (attr instanceof SingularAttribute)
			return (SingularAttribute<? super X, ?>)attr;
		throw new FemError("The attribute named {} is not a singular attribute on the managed type {} or its super types", name, managedType.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
		SingularAttribute<? super X, ?> attr = getSingularAttribute(name);
		if (attr.getJavaType().equals(type))
			return (SingularAttribute<? super X, Y>)attr;
		throw new FemError("The attribute named {} is not a singular attribute of type {} on the managed type {} or its super types", name, type.getName(), managedType.getName());
	}

	@Override
	public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
		Set<SingularAttribute<? super X, ?>> set = new HashSet<SingularAttribute<? super X, ?>>();
		for (Attribute<? super X, ?> attr : this.getAttributes()) 
			if (attr instanceof SingularAttribute)
				set.add((SingularAttribute<? super X, ?>)attr);
		return set;
	}

}
