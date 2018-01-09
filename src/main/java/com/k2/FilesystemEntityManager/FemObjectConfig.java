package com.k2.FilesystemEntityManager;

import java.io.File;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.k2.Util.ClassUtil;
/**
 * This class defines The storage configuration for a specific class
 * 
 * @author simon
 *
 */
public class FemObjectConfig {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * The file system entity manager configuration using this storage configuration
	 */
	private FemConfig config;
	/**
	 * The class to which this storage configuration applies.
	 */
	private Class<?> cls;
	/**
	 * Create a new instance of the storage configuration for the given file system entity manager configuration and class
	 * @param config		The file system entity manager configuration using this storage configuration
	 * @param cls		The class to which this storage configuration applies
	 */
	FemObjectConfig(FemConfig config, Class<?> cls) {
		this.config = config;
		this.cls = cls;
		logger.trace("Set the resource path to '{}' for class '{}'", ClassUtil.packageNameToPath(cls.getCanonicalName()), cls.getCanonicalName());
		resourcePath = ClassUtil.packageNameToPath(cls.getCanonicalName());
	}
	/**
	 * Set the file system entity manager configuration using this storage configuration
	 * @param config		The file system entity manager configuration using this storage configuration
	 */
	void setConfig(FemConfig config) { this.config = config; }
	/**
	 * Set the class to which this storage configuration applies
	 * @param cls	The class to which this storage configuration applies
	 */
	void setClass(Class<?> cls) { this.cls = cls; }
	/**
	 * The data format in which the instance data will be written to the file system.
	 */
	@Expose private FemDataFormat dataFormat = FemDataFormat.JSON;
	/**
	 * Set the data format in which the instance data will be written to the file system
	 * @param dataFormat		The data format in which the instance data will b written
	 * @return	This storage configuration for method chaining
	 */
	public FemObjectConfig dataFormat(FemDataFormat dataFormat) {
		logger.trace("Set the data format to '{}' for class '{}'", dataFormat, cls.getCanonicalName());
		this.dataFormat = dataFormat;
		return this;
	}
	/**
	 * Get the data format for this class
	 * @return	The data format for this class
	 */
	public FemDataFormat dataformat() {
		return dataFormat;
	}
	/**
	 * The location within the repository in which instance data is stored.
	 * 
	 * The default is the classes package as a path.
	 */
	@Expose private String resourcePath = "";
	/**
	 * Set the classes resource path within the repository
	 * 
	 * The resource path is relative to the repository location so if the given
	 * resource path starts with the file separator it is removed.
	 * 
	 * @param resourcePath	The new resource path in the repository
	 * @return	This storage configuration for method chaining
	 */
	public FemObjectConfig resourcePath(String resourcePath) {
		if (resourcePath != null && resourcePath.length() > 0 && resourcePath.toCharArray()[0] == File.separatorChar) {
				logger.trace("Set the resource path to '{}' for the class '{}'", resourcePath.substring(1), cls.getCanonicalName());
				this.resourcePath = resourcePath.substring(1);
		} else {
			logger.trace("Set the resource path to '{}' for the class '{}'", resourcePath, cls.getCanonicalName());
			this.resourcePath = resourcePath;
		}
		return this;
	}
	/**
	 * Get the resource path
	 * @return	The resource path or "" if it is null
	 */
	public String resourcePath() {
		if (resourcePath == null) return "";
		return resourcePath;
	}
	/**
	 * The structure of the stored data to support object mutation checking
	 * 
	 * Default to Object Change Number OCN
	 */
	@Expose private FemDataStructure dataStructure = FemDataStructure.OCN;
	/**
	 * Set the data structure for storage of object instance data
	 * 
	 * @param dataStructure	The new storage data structure
	 * @return	This storage configuration for method chaining
	 */
	public FemObjectConfig dataStructure(FemDataStructure dataStructure) {
		logger.trace("Set the data structure to '{}' for class '{}'", dataStructure, cls.getCanonicalName());
		this.dataStructure = dataStructure;
		return this;
	}
	/**
	 * Get the data structure for storage of object instance data
	 * @return	the data structure for storage of object instance data
	 */
	public FemDataStructure dataStructure() {
		return dataStructure;
	}
	/**
	 * The alias of the repository in which this classes instance data is stored
	 */
	@Expose private String repo = "default";
	/**
	 * Get the repository alias in which this classes instance data is stored
	 * @return	the repository alias in which this classes instance data is stored
	 */
	public String repository() { return repo; }
	/**
	 * Set the alias of the repository in which this classes instance data is stored
	 * @param alias	The alias of the repository in which the classes instance data is to be stored
	 * @return	This storage configuration for method chaining
	 */
	public FemObjectConfig repository(String alias) {
		logger.trace("Set the repository alias to '{}' for class '{}'", alias, cls.getCanonicalName());
		this.repo = alias;
		return this;
	}
	/**
	 * Update the file system entity manager with this configuration
	 */
	public void configure() {
		config.configure(this);
	}
	/**
	 * Get the class for which this is the storage configuration
	 * @return
	 */
	Class<?> configuredClass() {
		return cls;
	}
	
}
