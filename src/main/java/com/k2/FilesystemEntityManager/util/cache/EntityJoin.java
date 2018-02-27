package com.k2.FilesystemEntityManager.util.cache;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityJoin {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	final EntityField<?,?> fromField;
	final EntityField<?,?> toField;
	EntityJoin(EntityField<?,?> fromField, EntityField<?,?> toField) {
		
		logger.trace("Joining {}.{} to {}.{}", fromField.onClass.getName(), fromField.alias, toField.onClass.getName(), toField.alias);
		
		this.fromField = fromField;
		this.toField = toField;
	}
	
	public EntityField<?,?> getFromField() { return fromField; }
	public EntityField<?,?> getToField() { return toField; }
	
}
	
