package com.k2.FilesystemEntityManager;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

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
	 * This is a list of the entity class names that are exposed by this class.
	 */
	@Expose List<String> exposesEntities;
	/**
	 * This method returns the exposed entity class names
	 * @return	A list of the exposed entity class names
	 */
	private List<String> getExposesEntities() {
		if (exposesEntities == null) exposesEntities = new ArrayList<String>();
		return exposesEntities;
	}
	/**
	 * This method populates the exposed entity class names into a map, keyed on the path to the location of the exposed entity in the 
	 * original calling class. This method calls itself recursively to identify exposed entity classes at any level in the original 
	 * calling class.
	 * @param path	The path from the original calling class to the current class 
	 * @param cls	The current class being searched for exposed entity classes
	 * @param map	The map into which the exposed entity classes and paths are recorded
	 * @return		The populated map of exposed entity classes and paths for the original calling class.
	 */
	private Map<String, Class<?>> populateExposesEntities(String path, Class<?> cls, Map<String, Class<?>> map) {
		for (Field f : ClassUtil.getDeclaredFields(cls)) {
			if (f.isAnnotationPresent(Expose.class)) {
				Class<?> fCls = f.getType();
				if (!fCls.getName().startsWith("java.")) {
					if (fCls.isAnnotationPresent(Entity.class)) {
						map.put(path+"/"+f.getName(), fCls);
					}
				}
				if (Set.class.isAssignableFrom(fCls) || List.class.isAssignableFrom(fCls)) {
					Class<?> collectionCls = ClassUtil.getFieldGenericTypeClass(f, 0);
					if (collectionCls != null) {
						if (!collectionCls.getName().startsWith("java.")) {
							if (collectionCls.isAnnotationPresent(Entity.class)) {
								map.put(path+"/"+f.getName(), collectionCls);
							}
						}
					}
				}
				populateExposesEntities(path+"/"+f.getName(), fCls, map);
			}
		}
		return map;
	}
	/**
	 * Create a new instance of the storage configuration for the given file system entity manager configuration and class
	 * @param config		The file system entity manager configuration using this storage configuration
	 * @param cls		The class to which this storage configuration applies
	 */
	FemObjectConfig(FemConfig config, Class<?> cls) {
		this.config = config;
		this.cls = cls;
		logger.trace("Set the resource path to '{}' for class '{}'", ClassUtil.packageNameToPath(cls.getCanonicalName()), cls.getCanonicalName());
		if (cls.isAnnotationPresent(Table.class)) {
			Table aTable = cls.getAnnotation(Table.class);
			resourcePath = aTable.name();
		} else {
			resourcePath = ClassUtil.packageNameToPath(cls.getCanonicalName());
		}
		
		// Identify the exposed entity classes in this class and add them to the configuration
		Map<String, Class<?>> exposesEntities = populateExposesEntities("", cls, new HashMap<String, Class<?>>());
		for (Entry<String, Class<?>> e : exposesEntities.entrySet()) {
			config.objectConfig(e.getValue()).resourcePath("class: "+cls.getName()+":"+e.getKey());
			getExposesEntities().add(e.getValue().getName());
		}
		
	}
	/**
	 * This method identified whether this object configuration identifies a entity that is embedded in another entity
	 * @return	true if this object is embedded in another entity
	 */
	boolean isExposedEntity() {
		return (resourcePath.startsWith("class: "));
	}
	/**
	 * This method returns the class of the entity that embeds this entity within it
	 * @return	The class that embeds instances of this object
	 */
	Class<?> getRootClass() {
		if (isExposedEntity()) {
			String[] parts = resourcePath.split(":");
			if (parts.length == 3) {
				String className = parts[1].trim();
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new FemError("Class not found '{}' from resource path '{}'", e, className, resourcePath);
				}
			} else {
				throw new FemError("Unexpected exposed entity class in resource path '{}'", resourcePath);
			}
		} else {
			return cls;
		}
	}
	/**
	 * This method returns the '/' separated path of field aliases to the at which this object is embedded in the root object
	 * @return	A '/' separated path of field aliases to the field in which instance of this object are embedded
	 */
	public String getEmbeddedPath() {
		if (isExposedEntity()) {
			String[] parts = resourcePath.split(":");
			if (parts.length == 3) {
				return parts[2].trim();
			} else {
				throw new FemError("Unexpected exposed entity class in resource path '{}'", resourcePath);
			}
		}
		throw new FemError("The configured object is not an exposed entity. Unable to extract embedded path");
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
	public FemDataFormat dataFormat() {
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
		// Update the storage configuration for all entity classes exposed by this class to be the same as this class
		for (String className : getExposesEntities()) {
			try {
				Class<?> cls = Class.forName(className);
				config.objectConfig(cls)
					.dataFormat(dataFormat)
					.dataStructure(dataStructure)
					.repository(repo);
			} catch (ClassNotFoundException e) {
				throw new FemError("Class not found '{}'", className);
			}
		}
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
