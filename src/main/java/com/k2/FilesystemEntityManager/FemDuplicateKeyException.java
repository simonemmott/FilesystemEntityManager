package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

/**
 * This checked exception is thrown when an attempt is made to save an Object instance that already exists but has not been
 * fetched into the entity manager saving the object instance.
 * 
 * @author simon
 *
 */
public class FemDuplicateKeyException extends FemException {

	private static final long serialVersionUID = 3893110315990960001L;
	/**
	 * Create a duplicate key exception for the given class, key and repository identifying the cause that gave rise to the exception	
	 * @param cls	The class of the duplicate key exception
	 * @param key	The id of the object instance that is duplicated
	 * @param repo	The repository in which the duplicate key was detected
	 */
	public FemDuplicateKeyException(Class<?> cls, Serializable key, File repo) {
		super("An instance of '{}({}) already exists in the repo '{}'", cls.getCanonicalName(), key, repo.getAbsolutePath());
	}

}
