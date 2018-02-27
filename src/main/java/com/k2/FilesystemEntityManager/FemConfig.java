package com.k2.FilesystemEntityManager;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.k2.Util.StringUtil;

/**
 * This class represents the file system entity manager configuration.
 * 
 * The configuration is be read from the configuration file 'femf.conf' in the manager directory when the FileSystemEntityManagerFactory is
 * instantiated and can be added to systematically.
 * 
 * The configuration can be saved from the FileSystemEntityManagerFactory instance and is automatically saved when the instance is shutdown.
 * 
 * @author simon
 *
 */
public class FemConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@SuppressWarnings("unused")
	private FilesystemEntityManagerFactory femf;
	
	FemConfig(FilesystemEntityManagerFactory femf){
		this.femf = femf;
		logger.trace("Creating configuration for file system entity manager '{}'", femf.managerDictoryLocation());
	}
	/**
	 * The default data format for the file system entity manager factory instance. If a data format is not defined for a given class
	 * then instances of that class will be saved in the default format.
	 * 
	 * Default value FemDataFormat.JSON
	 */
	@Expose private FemDataFormat dataFormat = FemDataFormat.JSON;
	/**
	 * Get the default data format
	 * 
	 * @return	This instances default data format
	 */
	public FemDataFormat dataFormat() { return dataFormat; }
	/**
	 * Set the default data format of the file system entity manager factory instance.
	 * 
	 * @param dataFormat		The new default format for the file system entity manager factory instance.
	 * 
	 * @return	The file system entity manager factory instance.
	 */
	public FemConfig dataFormat(FemDataFormat dataFormat) {
		logger.trace("Setting the default date format of the file system entity manager '{}' to '{}'", femf.managerDictoryLocation(), dataFormat);
		this.dataFormat = dataFormat;
		return this;
	}
	/**
	 * This Map holds the storage configuration for a given class mapped with the classes canonical name.
	 */
	@Expose private Map<String, FemObjectConfig> objectConfigs = new HashMap<String, FemObjectConfig>();
	/**
	 * Get the map of class storage configurations.
	 * 
	 * @return	The map of class storage configurations mapped by the classes canonical name
	 */
	public Map<String, FemObjectConfig> objectConfigs() { return objectConfigs; }
	/**
	 * Set the storage configuration for a given class to the given storage configuration.
	 * 
	 * @param cls	The class to which the storage configuration applies
	 * @param conf	The storage configuration to be used for persisting instances of the given class
	 * @return	This file system entity manager factories configuration.
	 */
	public FemConfig objectConfig(Class<?> cls, FemObjectConfig conf) {
		logger.trace("Updating the storage configuration for the class '{}'", cls.getCanonicalName());
		objectConfigs.put(cls.getCanonicalName(), conf);
		configure(cls, conf);
		return this;
	}
	
	public FemConfig configure(Class<?> ... classes) {
		for (Class<?> cls : classes) {
			objectConfig(cls);
		}
		return this;
	}
	/**
	 * Get the storage configuration for the given class.
	 * 
	 * If the instance is not configured to store the given class a new storage configuration with default values
	 * is created and returned.
	 * 
	 * If the configuration is changed then the configure() method of the storage configuration must be called
	 * to load the configuration in to the file system entity manager factory instance.
	 * 
	 * @param cls	The class for which the storage configuration is required.
	 * @return		The storage configuration for the given class
	 */
	public FemObjectConfig objectConfig(Class<?> cls) {
		FemObjectConfig conf = managedClassConfigs.get(cls);
		if (conf == null) {
			logger.trace("Configuing a new storage configuration for the class '{}'", cls.getCanonicalName());
			conf = new FemObjectConfig(this, cls);
			objectConfigs.put(cls.getCanonicalName(), conf);
			managedClassConfigs.put(cls,  conf);
		}
		return conf;
	}
	/**
	 * This method returns the current storage configuration for given class
	 * @param cls	The class for which the storage configuration is required
	 * @return	The current storage configuration for the given class or null if storage of the class
	 * has not been configured
	 */
	public FemObjectConfig getObjectConfig(Class<?> cls) {
		return managedClassConfigs.get(cls);
	}
	/**
	 * The repositories managed by this file system entity manager factory instance.
	 */
	@Expose private Map<String, String> repos = new HashMap<String, String>();
	/**
	 * Set the default repository for this file system entity manager factory instance
	 * @param dir	The default repository directory.
	 * @return		This file system entity manager factory instance for method chaining.
	 */
	public FemConfig setDefaultRepo(File dir) {
		if (!dir.exists()) throw new FemError("The default repository '{}' doesn't exist", dir.getAbsolutePath());
		if (!dir.isDirectory()) throw new FemError("The default repository '{}' is not a directory", dir.getAbsolutePath());
		if (!dir.canRead()) throw new FemError("Unable to read from the default repository '{}'", dir.getAbsolutePath());
		if (!dir.canWrite()) throw new FemError("Unable to write to the default repositry '{}'", dir.getAbsolutePath());
		logger.trace("Setting the default repositry location to '{}'", dir.getAbsolutePath());
		repos.put("default", dir.getAbsolutePath());
		return this;
	}
	/**
	 * Set the location of the repository with the given alias.
	 * 
	 * @param alias	The alias of the repository
	 * @param dir	The location of the repository
	 * @return		This file system entity manager factory instance for method chaining.
	 */
	public FemConfig setRepo(String alias, File dir) {
		if (!StringUtil.isSet(alias)) throw new FemError("Unable to configure a repository with a null or blank alias");
		if (!dir.exists()) throw new FemError("The default repository '{}' doesn't exist", dir.getAbsolutePath());
		if (!dir.isDirectory()) throw new FemError("The default repository '{}' is not a directory", dir.getAbsolutePath());
		if (!dir.canRead()) throw new FemError("Unable to read from the default repository '{}'", dir.getAbsolutePath());
		if (!dir.canWrite()) throw new FemError("Unable to write to the default repositry '{}'", dir.getAbsolutePath());
		logger.trace("Setting the repositry '{}' location to '{}'",alias, dir.getAbsolutePath());
		repos.put(alias, dir.getAbsolutePath());
		return this;
	}
	
	/**
	 * Get the location of the default repository
	 * 
	 * @return	The location of the default repository
	 */
	public File defaultRepository() {
		return repository("default");
	}
	/**
	 * Get the location of the repository with the given alias
	 * 
	 * @param alias	The alias of the required repository
	 * @return	The location of the aliased repository
	 */
	public File repository(String alias) {
		String repoPath = repos.get(alias);
		if (repoPath == null) throw new FemError("The repo '{}' is undfined", alias);
		File repo = new File(repoPath);
		if (!repo.exists()) throw new FemError("The repo '{}' at '{}' does not exist", alias, repoPath);
		if (!repo.isDirectory()) throw new FemError("The repo '{}' at '{}' is not a directory", alias, repoPath);
		if (!repo.canRead()) throw new FemError("Unable to read from the repo '{}' at '{}'", alias, repoPath);
		if (!repo.canWrite()) throw new FemError("Unable to write to the repo '{}' at '{}'", alias, repoPath);
		return repo;
	}
	/**
	 * Add the given storage configuration to the configuration and configure the file system entity manager factory instance
	 * 
	 * @param femObjectConfig	The storage configuration and class to configure
	 */
	void configure(FemObjectConfig femObjectConfig) {
		configure(femObjectConfig.configuredClass(), femObjectConfig);
		
	}
	/**
	 * Configure the given class and storage configuration in the file system entity manager factory instance
	 * 	
	 * @param cls				The class to be configured with the storage configuration
	 * @param femObjectConfig	The storage configuration for the given class
	 */
	private void configure(Class<?> cls, FemObjectConfig femObjectConfig) {
		if (cls == null) throw new FemError("Unable to configure an object config for a null class");
		if (femObjectConfig == null) throw new FemError("Unable to configure a null configuration");
		logger.trace("Configuring storage configuration for '{}'", cls.getCanonicalName());
		femObjectConfig.setClass(cls);
		femObjectConfig.setConfig(this);
		if (managedClassConfigs == null) managedClassConfigs = new HashMap<Class<?>, FemObjectConfig>();
		if (xmlContexts == null) xmlContexts = new HashMap<Class<?>, JAXBContext>();
		managedClassConfigs.put(cls, femObjectConfig);
		if (femObjectConfig.dataFormat() == FemDataFormat.XML) {
			try {
				xmlContexts.put(cls, JAXBContext.newInstance(FemOcn.class, FemWrapper.class, cls));
			} catch (JAXBException e) {
				throw new FemError(e);
			}
		}
	}
	/**
	 * A map of storage configurations mapped by class
	 */
	private Map<Class<?>, FemObjectConfig> managedClassConfigs = new HashMap<Class<?>, FemObjectConfig>();
	/**
	 * A map of JAXB contexts mapped by class
	 */
	private Map<Class<?>, JAXBContext> xmlContexts = new HashMap<Class<?>, JAXBContext>();
	/**
	 * This method configures the file system entity manager factory instance with the currently loaded
	 * configuration
	 * 
	 * This is required after reading the configuration from a file to link storage configuration to the loaded
	 * configuration and to evaluate the stored class names into instaces of Class
	 */
	void configure() {
		logger.trace("Configuring file system entity manager factory instance");
		for (Entry<String, FemObjectConfig> entry : objectConfigs().entrySet()) {
			try {
				Class<?> cls = Class.forName(entry.getKey());
				configure(cls, entry.getValue());
			} catch (ClassNotFoundException e) {
				throw new FemError("Cannot find class for name '{}'", e, entry.getKey()); 
			}
			
		}
	}
	/**
	 * Get the JAXB context for the give class.
	 * 
	 * The configuration holds a map of JAXB contexts mapped by class to avoid having to create new JAXB contexts each time
	 * an instance of a class is stored or retrieved as XML
	 * 
	 * @param cls	The class for which to get the relevant JAXB context
	 * @return		The JAXB context capable of reading XML for the given class.
	 */
	JAXBContext getJaxbContext(Class<?> cls) {
		JAXBContext ctx = xmlContexts.get(cls);
		if (ctx == null) throw new FemError("No JAXB contexted defined for class '{}'", cls.getCanonicalName());
		return ctx;
	}
}
