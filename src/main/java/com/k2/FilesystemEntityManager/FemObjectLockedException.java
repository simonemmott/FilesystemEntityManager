package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

public class FemObjectLockedException extends FemException {

	private static final long serialVersionUID = -3665966755067584586L;

	public FemObjectLockedException(Class<?> cls, Serializable key, File resource, Throwable cause) {
		super("Unable to lock resource '"+resource.getAbsolutePath()+"' for class '"+cls.getCanonicalName()+"' and key '"+key+"'", cause);
	}

	public FemObjectLockedException(Class<?> cls, Serializable key, File resource) {
		super("Unable to lock resource '"+resource.getAbsolutePath()+"' for class '"+cls.getCanonicalName()+"' and key '"+key+"'");
	}

}
