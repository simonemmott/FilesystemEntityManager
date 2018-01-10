package com.k2.FilesystemEntityManager;

import java.io.Serializable;

/**
 * This unchecked exception is thrown when an attempt is made to delete an Object instance that is not attached to the 
 * entity manager
 * 
 * @author simon
 *
 */
public class FemDetachedObjectError extends FemError {

	private static final long serialVersionUID = -2159376773043440221L;

	/**
	 * Create a new detached object exception for the given class, key and entity manager id
	 * 
	 * @param cls	The class of the object that has mutated
	 * @param key	The id of the instance of the object that has mutated
	 * @param emId	The string id of the entity manager in which the error is thrown
	 */
	public FemDetachedObjectError(Class<?> cls, Serializable key, String emId) {
		super("The instance of '{}({}) is not attached to the entity manager '{}'", cls.getCanonicalName(), key, emId);
	}

}
