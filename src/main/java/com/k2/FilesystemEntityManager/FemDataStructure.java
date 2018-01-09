package com.k2.FilesystemEntityManager;
/**
 * This enumeration defines the data structure applied to persisted objects to support object change control
 * 
 * @author simon
 *
 */
public enum FemDataStructure {
	/**
	 * No additional structure is applied to the persisted object instance.
	 * Overwriting changed made and committed since the object was read from the file system  are prevented through 
	 * calculation of an MD5 hash of the file persisting the object instance.
	 */
	RAW,
	/**
	 * The persisted object instance is stored in a wrapped with an object change number 'OCN'
	 * Overwriting changes made and committed since the object was read from the file system as prevented through
	 * maintenance and comparison of the OCN for each persisted object. This approach is faster but requires that
	 * all processes that update the persisted instance update increment the OCN when the instance data is changed
	 */
	OCN
}
