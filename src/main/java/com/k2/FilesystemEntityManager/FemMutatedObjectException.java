package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

/**
 * This checked exception is thrown when an attempt is made to save an Object instance that already been updated and committed by a separate connection
 * 
 * @author simon
 *
 */
public class FemMutatedObjectException extends FemException {

	private static final long serialVersionUID = -8320901828413741790L;

	/**
	 * Create a new mutated object exception for the given class, key and repository
	 * 
	 * @param cls	The class of the object that has mutated
	 * @param key	The id of the instance of the object that has mutated
	 * @param repo	The repository in which the mutated object was identified
	 */
	public FemMutatedObjectException(Class<?> cls, Serializable key, File repo) {
		super("The instance of '{}({}) in the repo '{}' has been changed by another process", cls.getCanonicalName(), key, repo.getAbsolutePath());
	}

}
