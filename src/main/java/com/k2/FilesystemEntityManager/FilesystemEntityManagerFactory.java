package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.k2.Util.FileUtil;
/**
 * This class creates instances of file system entity managers.
 * 
 * It holds the configuration of how to save instances of the classes that are configured to be managed entity managers created by this entity manager
 * factory.
 * 
 * Instance's of FilesystemEntityManagerFactory are created using the startup static method specifying the directory containing the fem.conf file
 * that holds the configuration file containing the configuration of how to save instance data of classes by the entity managers created by the 
 * entity manager factory instance.
 * 
 * The configuration defines storage parameters for storing data for each class to be managed by entity managers created by the entity manager factory
 * 
 * Contention over update access to the persisted instance data is managed with the java FileLock API. Consequently multiple instances of the entity 
 * manager factory can manage the same classes in multiple JVM's though it is only necessary to have a single entity manager factory running within a
 * single JVM.
 * 
 * The entity manager factory can be configured systematically, however doing so should be avoided in systems that create entity managers
 * 
 * The entity manager factory configuration allows the definition of multiple repositories identified by an alias. Each class specifies one repository
 * by its alias and a location within that repository in which to store instances data for the class.
 * 
 * @author simon
 *
 */
public class FilesystemEntityManagerFactory {
	
	public static final String CONF = "fem.conf";
	public static final String JSON = ".json";
	public static final String XML = ".xml";
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	/**
	 * This method creates an instance of FilesystemEntityManager to manage persistence of instance data in the file system.
	 * @param managerDir		The directory containing the configuration file for the entity manager factory fem.conf
	 * @return		An instance of the file system entity manager configured according to the contents of the fem.conf file
	 * @throws FemException	If the given file does not exist, is not a direcory or doesn't allow read and write.
	 */
	public static FilesystemEntityManagerFactory startup(File managerDir) throws FemException {
		
		if (!managerDir.exists()) throw new FemException("The filesystem entity manager directory '"+managerDir.getAbsolutePath()+"' does not exist!");
		if (!managerDir.isDirectory()) throw new FemException("The filesystem entity manager directory '"+managerDir.getAbsolutePath()+"' is not a directory!");
		if (!managerDir.canRead()) throw new FemException("Unable to read from the filesystem entity manager directory '"+managerDir.getAbsolutePath()+"'");
		if (!managerDir.canWrite()) throw new FemException("Unable to write to the filesystem entity manager directory '"+managerDir.getAbsolutePath()+"'");
		
		FilesystemEntityManagerFactory femFactory = new FilesystemEntityManagerFactory(managerDir);		
		
		return femFactory;
	}
	
	/**
	 * This private constructor creates an instance of the file system entity manager factory for the given directory
	 * 
	 * If the fem.conf file exists it is read and the resultant entity manager factory is configured accodingly
	 * @param managerDir		The directory containing the fem.conf file
	 */
	private FilesystemEntityManagerFactory(File managerDir) {
		this.managerDir = managerDir;
		this.connectionsDir = new File(managerDir.getAbsolutePath()+File.separatorChar+"connections");
		if (!connectionsDir.exists()) connectionsDir.mkdirs();
		
		File confFile = FileUtil.fetch(managerDir, CONF);
		if (confFile != null) readConfig(confFile);
		
	}
	
	/**
	 * This method returns the absolute location of the entity manager factories manager directoy. This effectively intifies the entity
	 * manager factory as all instance of FilesystemEntityManagerFactory with the same manager directory manage the same classes and instance
	 * data in the same way and can be considered the same instance. 
	 * @return	The absolute path of this entity managers manager directory
	 */
	public String managerDictoryLocation() { return managerDir.getAbsolutePath(); }
	/**
	 * The manager directory of this entity manager factory
	 */
	private File managerDir;
	/**
	 * The map of connections currently active and created by this entity manager factory
	 */
	private Map<String, FilesystemEntityManager> connections = new HashMap<String, FilesystemEntityManager>();
	/**
	 * This method returns a File representing the repository for the given alias
	 * @param alias	The alias of the repository
	 * @return	A file representing the repository identified by the alias
	 */
	File repository(String alias) {
		File repo = config().repository(alias);
		if (repo == null) throw new FemError("There is no repository defined for the alias '{}'", alias);
		return repo;
	}
	/**
	 * The directory in the manager directory in which each connection to the entity manager factory will create its working directory
	 */
	private File connectionsDir;
	/**
	 * This method returns a File representing the connections directory
	 * @return	An instance of File representing the connections directory
	 */
	File connectionsDir() { return connectionsDir; }

	/**
	 * The configuration of this entity manager factory
	 */
	private FemConfig config;
	/**
	 * This method sets the configuration for this entity manager factory to the given configuration.
	 * @param config		The new configuration object for this entity manager factory
	 * @return	This entity manager factory for method chaining
	 */
	public FilesystemEntityManagerFactory configure(FemConfig config) {
		this.config = config;
		config.configure();
		return this;
	}
	/**
	 * This method returns the current configuration of the entity manager factory.
	 * If no configuration has been defined a new configuration object is created
	 * @return	The configuration for this entity manager factory
	 */
	public FemConfig config() { 
		if (config == null) config = new FemConfig(this);
		return config; 
	}
	/**
	 * This ThreadLocal variable is used to pass the Type of class contained by a wrapped object to Gson to allow it to deserialize
	 * The generic OcnWrapper class
	 */
	private LocalType localType = new LocalType();
	/**
	 * This method returns the ThreadLocal variable to pass configuration to Gson
	 * @return	The ThreadLocal&lt;Type&gt; variable used to passs configuration to Gson during deserialization 
	 */
	public LocalType localType() { return localType; }
	/**
	 * The Gson instance used to read and write the entity manager factories configuration file
	 */
	private Gson gsonConf = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	/**
	 * The Gson instance used to read and writer JSON data representing persisted object instances
	 */
	private Gson gson;
	/**
	 * This method returns the current Gson instance used to read and write JSON data representing persisted object instances
	 * @return	The current Gson instance used to read and write JSON data representing persisted object instances. If the Gson 
	 * instance doesn't exist then a new one is created automcatically
	 */
	public Gson gson() { 
		if (gson == null) gson = new GsonBuilder()
									.setPrettyPrinting()
									.excludeFieldsWithoutExposeAnnotation()
									.registerTypeAdapter(FemWrapper.class, new FemWrapperDeserializer(localType))
									.create();
		return gson;
	}
	/**
	 * This method sets the Gson instance to read and write the JSON data representing persisted object instances
	 * @param gson	A new Gson instance for reading and writing JSON data representing persisted object instances
	 * @return	This entity manager factory for instance chaining.
	 */
	public FilesystemEntityManagerFactory gson(Gson gson) {
		this.gson = gson;
		return this;
	}
	/**
	 * This method creates an instance of FilesystemEntityManager to manage instances of the classes configured in
	 * this entity manager factories configuration.
	 * @return	A new entity manager able to manage entities of classes configured in this entity managers factory configuration
	 */
	public FilesystemEntityManager entityManager() {
		FilesystemEntityManager em = new FilesystemEntityManager(this);
		connections.put(em.getId(), em);
		return em;
	}

	/**
	 * This method reads the given configuration file and sets the configuration of the entity manager facory accordingly
	 * @param confFile	A file representing the configuration file femf.conf
	 */
	private void readConfig(File confFile) {
		try {
			config = gsonConf.fromJson(new FileReader(confFile), FemConfig.class);
			config.configure();
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			throw new FemError("Unable to read configuation file '"+confFile.getAbsolutePath()+"'", e);
		}
	}
	/**
	 * This method causes this entity manager factory to save it's configuration to the femf.conf file
	 * 
	 * This is done automatically when the entity manager factory is shut down
	 */
	public void saveConfig() {
		File configFile = new File(managerDir.getAbsolutePath()+File.separatorChar+CONF);
		if (configFile.exists()) configFile.delete();
		try {
			Writer w = new FileWriter(configFile);
			gsonConf.toJson(config, w);
			w.flush();
			w.close();
		} catch (IOException e) {
			throw new FemError("Unable to write to the configuration file '"+configFile.getAbsolutePath()+"'", e);
		}
	}
	/**
	 * This method shuts down this entity manager factory. All connections currently active created by this entity manager
	 * factory are automatically rolled back and closed
	 */
	public void shutdown() {
		Set<FilesystemEntityManager> connsToClose = new HashSet<FilesystemEntityManager>();
		for (FilesystemEntityManager fem : connections.values()) connsToClose.add(fem);
		for (FilesystemEntityManager fem : connsToClose) { 
			fem.rollback(); 
			fem.close(); 
		}
		saveConfig();
	}
	/**
	 * This method is called by the entity manager when it closes.
	 * It removes the entity manager from the list of active connections held by this entity manager factory
	 * @param fem	The entity manager that has closed
	 */
	void closed(FilesystemEntityManager fem) {
		connections.remove(fem.getId());
	}

	

}
