package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

/**
 * This checked exception is thrown when an attempt is made to save or delete an object that is locked.
 * 
 * An object is locked as soon as an attempt is made to save or delete it and is released when the connection 
 * that is locking it is committed or rolled back.All locks managed by the file system entity manager are 
 * effectively optimistic write locks with mutated objects identified through the object change number 
 * or the calculation of the MD5 hash of the file persisting the objects instance data.
 * @author simon
 *
 */
public class FemObjectLockedException extends FemException {

	private static final long serialVersionUID = -3665966755067584586L;

	/**
	 * Create a new mutated object exception for the object instance identified by the class and key stored in the given file and with the
	 * given throwable cause giving rise to the exception
	 * @param cls		The class of the object being locked
	 * @param key		The id of the object instance being locked
	 * @param resource	The file holding the the objects instance data
	 */
	public FemObjectLockedException(Class<?> cls, Serializable key, File resource) {
		super("Unable to lock resource '"+resource.getAbsolutePath()+"' for class '"+cls.getCanonicalName()+"' and key '"+key+"'");
	}

}
