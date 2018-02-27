package com.k2.FilesystemEntityManager.util.cache;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.classes.ClassUtil;

public class EntityCache<E> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	final Class<E> entityClass;
	final String entityName;
	final String tableName;
	Map<String, EntityField<E,?>> columnsByName = new HashMap<String, EntityField<E,?>>();
	Map<String, EntityField<E,?>> fieldsByAlias = new HashMap<String, EntityField<E,?>>();
	Map<String, EntityLink<E,?>> linksByAlias = new HashMap<String, EntityLink<E,?>>();
	public EntityCache(Class<E> entityClass) {
		
		logger.trace("Caching class {}", entityClass.getName());
		this.entityClass = entityClass;
		
		Entity e = entityClass.getAnnotation(Entity.class);
		if (e == null)
			throw new FemError("The class {} is not annotated with the Entity annotation", entityClass.getName());
		this.entityName = (e.name() == null || e.name().equals(""))?entityClass.getSimpleName():e.name();
		logger.trace("Caching entity name {} for class {}", this.entityName, entityClass.getName());
		
		Table t = entityClass.getAnnotation(Table.class);
		if ( t == null) {
			this.tableName = this.entityName;
		} else {
			this.tableName = t.name();
		}
		logger.trace("Caching table name {} for class {}", this.tableName, entityClass.getName());
	}
	
	public void populateFields() {	
		for(Field f : ClassUtil.getAllFields(entityClass)) {
			if (f.isAnnotationPresent(Column.class)) {
				Column c = f.getAnnotation(Column.class);
				logger.trace("Found column name '{}' for class {}", c.name(), entityClass.getName());
				EntityField<E,?> ef = new EntityField(entityClass, f.getType(), f);
				columnsByName.put(c.name(), ef);
				fieldsByAlias.put(f.getName(), ef);
			}
		}
		
		for(Field f : ClassUtil.getAllFields(entityClass)) {			
			if (f.isAnnotationPresent(ManyToOne.class)) {
				logger.trace("Found link alias '{}' for class {}", f.getName(), entityClass.getName());
				@SuppressWarnings("unchecked")
				EntityLink<E,?> el = new EntityLink(entityClass, f.getType(), f);
				linksByAlias.put(f.getName(), el);
				fieldsByAlias.put(f.getName(), el);
				
			}
		}
	}
	
	public Class<E> getEntityClass() { return entityClass; }
	public String getEntityName() { return entityName; }
	public String getTableName() { return tableName; }
	public Collection<EntityField<E,?>> getColumns() { return columnsByName.values(); }
	public Collection<EntityField<E,?>> getFields() { return fieldsByAlias.values(); }
	public Collection<EntityLink<E,?>> getLinks() { return linksByAlias.values(); }
	
	public Field getColumnByName(String name) {
		EntityField<E,?> ef = columnsByName.get(name);
		if (ef == null)
			throw new FemError("The column named {} does not exist on the entity class {}", name, entityClass.getName());
		return ef.field;
	}
	
	public EntityField<E,?> getFieldByName(String name) {
		EntityField<E,?> ef = columnsByName.get(name);
		if (ef == null)
			throw new FemError("The column named {} does not exist on the entity class {}", name, entityClass.getName());
		return ef;
	}
	
	public Field getColumnByAlias(String alias) {
		EntityField<E,?> ef = fieldsByAlias.get(alias);
		if (ef == null || ef.columnName == null)
			throw new FemError("The column wih alias {} does not exist on the entity class {}", alias, entityClass.getName());
		return ef.field;
	}
	
	public EntityField<E,?> getFieldByAlias(String alias) {
		EntityField<E,?> ef = fieldsByAlias.get(alias);
		if (ef == null)
			throw new FemError("The column wih alias {} does not exist on the entity class {}", alias, entityClass.getName());
		return ef;
	}
	
}
