package com.k2.FilesystemEntityManager.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.k2.FilesystemEntityManager.util.cache.*;

public class EntityUtil {
	
	
	private static Map<Class<?>, EntityCache<?>> entities = new HashMap<Class<?>,EntityCache<?>>();
	
	@SuppressWarnings("unchecked")
	public static <E> EntityCache<E> getEntityCache(Class<E> entityClass) {
		EntityCache<E> ec = (EntityCache<E>) entities.get(entityClass);
		if (ec != null) return ec;
		ec = new EntityCache<E>(entityClass);
		entities.put(entityClass, ec);
		ec.populateFields();
		return ec;
	}

	public static <E> Field getColumnByName(Class<E> entityClass, String name) {
		return getEntityCache(entityClass).getColumnByName(name);
	}

	public static <E> EntityField<E,?> getEntityFieldByName(Class<E> entityClass, String name) {
		return getEntityCache(entityClass).getFieldByName(name);
	}

	public static <E> Field getColumnByAlias(Class<E> entityClass, String alias) {
		return getEntityCache(entityClass).getColumnByAlias(alias);
	}

	public static <E> EntityField<E,?> getEnityFieldByAlias(Class<E> entityClass, String alias) {
		return getEntityCache(entityClass).getFieldByAlias(alias);
	}

	public static <E> EntityLink<E,?> getEnityLinkByAlias(Class<E> entityClass, String alias) {
		return (EntityLink<E, ?>) getEntityCache(entityClass).getFieldByAlias(alias);
	}
	
	public static <E> List<EntityLink<E,?>> getLazyLoadLinks(Class<E> entityClass) {
		List<EntityLink<E,?>> lazyLinks = new ArrayList<EntityLink<E,?>>();
		EntityCache<E> ec = getEntityCache(entityClass);
		
		for (EntityLink<E,?> link : ec.getLinks()) 
			if (link.isLazyFetch()) lazyLinks.add(link);
		
		return lazyLinks;
	}

}
