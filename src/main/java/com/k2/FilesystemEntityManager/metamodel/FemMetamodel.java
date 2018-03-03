package com.k2.FilesystemEntityManager.metamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;
import com.k2.Util.classes.ClassUtil.AnnotationCheck;

public class FemMetamodel implements Metamodel {
	
	Map<Class<?>, FemManagedType<?>> managedTypes = new HashMap<Class<?>, FemManagedType<?>>();
	Map<Class<?>, FemEntityType<?>> entityTypes = new HashMap<Class<?>, FemEntityType<?>>();
	Map<Class<?>, FemEmbeddableType<?>> embeddableTypes = new HashMap<Class<?>, FemEmbeddableType<?>>();
	
	public FemMetamodel() {}
	
	public FemMetamodel(Class<?> ...classes) {
		for (Class<?> cls : classes)
			manage(cls);
	}
	
	public FemMetamodel(String ...packageNames) {
		for (String packageName : packageNames) 
			for (Class<?> cls : ClassUtil.getClasses(packageName, AnnotationCheck.ANY, Entity.class, Embeddable.class))
				manage(cls);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public <X> ManagedType<X> manage(Class<X> cls) {
		FemManagedType<X> mt = (FemManagedType<X>) managedTypes.get(cls);
		if (mt != null) return mt;
		if (cls.getSuperclass() != null && cls.getSuperclass() != Object.class)
			manage(cls.getSuperclass());
		if (cls.isAnnotationPresent(Entity.class)) {
			mt = new FemEntityType<X>(this, cls);
			add(mt);
			return mt;
		}
		if (cls.isAnnotationPresent(Embeddable.class)) {
			mt = new FemEmbeddableType<X>(this, cls);
			add(mt);
			return mt;
		}
		throw new FemError("The class {} is not a manageable type", cls.getName()); 
	}
	
	<X> void add(FemManagedType<X> managedType) {
		managedTypes.put(managedType.getJavaType(), managedType);
		switch (managedType.getPersistenceType()) {
		case BASIC:
			if (managedType instanceof FemEmbeddableType)
				embeddableTypes.put(managedType.getJavaType(), (FemEmbeddableType<X>)managedType);
			
			if (managedType instanceof FemEntityType) 
				entityTypes.put(managedType.getJavaType(), (FemEntityType<X>)managedType);

			break;
		case EMBEDDABLE:
			if (managedType instanceof FemEmbeddableType) {
				embeddableTypes.put(managedType.getJavaType(), (FemEmbeddableType<X>)managedType);
			} else {
				throw new FemError("Managed types with persistence type EMBEDDABLE must implement EmbeddableType");
			}
			break;
		case ENTITY:
			if (managedType instanceof FemEntityType) {
				entityTypes.put(managedType.getJavaType(), (FemEntityType<X>)managedType);
			} else {
				throw new FemError("Managed types with persistence type ENTITY must implement EntityType");
			}
			break;
		case MAPPED_SUPERCLASS:
			break;
		default:
			break;
		
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> EntityType<X> entity(Class<X> cls) {
		if ( ! cls.isAnnotationPresent(Entity.class) )
			throw new FemError("The class {} is not an entity class", cls.getName());
		EntityType<X> et = (EntityType<X>) entityTypes.get(cls);
		if (et == null) 
			throw new FemError("This entity manager factory is not managing instances of the entity class {}", cls.getName());
		return et;
	}
	
	@SuppressWarnings("unchecked")
	public <X> FemManagedType<? super X> managedSuperType(Class<X> cls) {
		if (cls == null) return null;
		Class<? super X> superX = cls;
		FemManagedType<? super X> mt = null;
		while (superX != null && superX.getSuperclass() != Object.class) {
			superX = cls.getSuperclass();
			mt = (FemManagedType<? super X>) managedTypes.get(superX);
			if (mt != null) return mt;
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public <X> FemManagedType<X> managedType(Class<X> cls) {
		return (FemManagedType<X>) managedTypes.get(cls);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
		if ( ! cls.isAnnotationPresent(Embeddable.class) )
			throw new FemError("The class {} is not an embeddable class", cls.getName());
		EmbeddableType<X> et = (EmbeddableType<X>) embeddableTypes.get(cls);
		if (et == null) 
			throw new FemError("This entity manager factory is not managing instances of the embeddable class {}", cls.getName());
		return et;
	}

	@Override
	public Set<ManagedType<?>> getManagedTypes() {
		return new HashSet<ManagedType<?>>(managedTypes.values());
	}

	@Override
	public Set<EntityType<?>> getEntities() {
		return new HashSet<EntityType<?>>(entityTypes.values());
	}

	@Override
	public Set<EmbeddableType<?>> getEmbeddables() {
		return new HashSet<EmbeddableType<?>>(embeddableTypes.values());
	}

}
