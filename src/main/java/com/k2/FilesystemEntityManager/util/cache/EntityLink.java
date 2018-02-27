package com.k2.FilesystemEntityManager.util.cache;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.util.EntityUtil;
import com.k2.FilesystemEntityManager.util.KeyUtil;

public class EntityLink<E,L> extends EntityField<E,L>{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private List<EntityJoin> joins = new ArrayList<EntityJoin>();
	private final FetchType fetchType;
	private final CascadeType[] cascadeTypes;
	
	EntityLink(Class<E> entityClass, Class<L> linkToClass, Field field) {
		super(entityClass, linkToClass, field);
		
		logger.trace("Caching link {} on entity class {} to entity class {}", field.getName(), entityClass.getName(), field.getType().getName());
		
		if (field.isAnnotationPresent(ManyToOne.class)) {
			ManyToOne mto = field.getAnnotation(ManyToOne.class);
			this.fetchType = mto.fetch();
			this.cascadeTypes = mto.cascade();
			
			
		} else {
			throw new FemError("The link field {}.{} to {} is not annotated with an @ManyToOne!", entityClass.getName(), field.getName(), linkToClass.getName());
		}
		
		if (field.isAnnotationPresent(JoinColumn.class)) {
			
			JoinColumn jc = field.getAnnotation(JoinColumn.class);
						
			String sourceColumnName = jc.name();
			String targetColumnName = jc.referencedColumnName();
			
			if (targetColumnName == null || targetColumnName.equals("")) {
				Field[] targetPk = KeyUtil.getKeyFields(linkToClass);
				if (targetPk.length > 1) 
					throw new FemError("The target class {} has a compound key with {} fields but is referenced by the single field {}.{}",
							linkToClass.getName(),
							targetPk.length,
							entityClass.getName(),
							field.getName());
				Column c = targetPk[0].getAnnotation(Column.class);
				targetColumnName = (c.name()==null||c.name().equals(""))?targetPk[0].getName():c.name();
			}
			
			logger.trace("Caching link {} on entity class {} with joining Source.{} to Target.{}", field.getName(), entityClass.getName(), sourceColumnName, targetColumnName);

			EntityField<E,?> joinFromField = EntityUtil.getEntityFieldByName(entityClass, sourceColumnName);
			EntityField<?,?> joinToField = EntityUtil.getEntityFieldByName(linkToClass, targetColumnName);
			
			EntityJoin ej = new EntityJoin(joinFromField, joinToField);
			
			joins.add(ej);
		}
		
		if (field.isAnnotationPresent(JoinColumns.class)) {
			
			JoinColumns jcs = field.getAnnotation(JoinColumns.class);
			
			logger.trace("Caching link {} on entity class {} with multiple joins", field.getName(), entityClass.getName());

			for (JoinColumn jc : jcs.value()) {

				String sourceColumnName = jc.name();
				String targetColumnName = jc.referencedColumnName();
				
				logger.trace("Caching link {} on entity class {} with joining Source.{} to Target.{}", field.getName(), entityClass.getName(), sourceColumnName, targetColumnName);

				EntityField<E,?> joinFromField = EntityUtil.getEntityFieldByName(entityClass, sourceColumnName);
				EntityField<?,?> joinToField = EntityUtil.getEntityFieldByName(linkToClass, targetColumnName);
				
				EntityJoin ej = new EntityJoin(joinFromField, joinToField);
				
				joins.add(ej);

			}
			
			
		}
	}
	
	public List<EntityJoin> getJoins() { return joins; }
	
	public Field[] getLinkFromFields() {
		Field[] linkFromFields = new Field[joins.size()];
		for (int i=0; i<joins.size(); i++) {
			linkFromFields[i] = joins.get(i).getFromField().getField();
		}
		return linkFromFields;
	}
	
	public Field[] getLinkToFields() {
		Field[] linkToFields = new Field[joins.size()];
		for (int i=0; i<joins.size(); i++) {
			linkToFields[i] = joins.get(i).getFromField().getField();
		}
		return linkToFields;
	}
	
	public boolean isLazyFetch() {
		return (fetchType == FetchType.LAZY);
	}
}
