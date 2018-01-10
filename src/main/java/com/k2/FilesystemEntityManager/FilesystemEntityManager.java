package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.FileUtil;
import com.k2.Util.StringUtil;
import com.k2.Util.Identity.IdentityUtil;
import com.k2.Util.exceptions.FileLockedException;
/**
 * This class defines the thread specific entity manager that provides the API to fetch, save and delete instances of objects into and out of 
 * repositories on the file system.
 * 
 * Instances of FileSystemEntityManager can only be created through the connect() method of an instance of the FileSystemEntityManagerFactory.
 * 
 * Object instance data is held on the file system in repositories. A repository can hold instances of multiple object classes by defining a
 * repository path for each class that identifies the path within the repository in which to store instance data for objects of the class.
 * 
 * The actual repository and format of the file system resource that persists the instance data for any given class is defined in the 
 * configuration of the FileSystemEntityManagerFactory that created this entity manager.
 * 
 * The entity manager is not thread safe and therefore should only be used within the context of a single thread.
 * 
 * The entity manager holds a cache of the objects that are managed by this entity manager. This prevents unnecessary disk reads.
 * 
 * Attempts to alter the same instance data by different threads is managed through the FileLock API and therefore any system
 * that writes data to the repositories must make use of the file systems locking architecture in order to not conflict with the file system entity manager.
 * 
 * The entity manager manages objects using an Object Change Number (OCN) to identify that the objects instance data has changed if the object is
 * stored in the repository in a wrapper. If the OCN of an object when it is being saved is not the same as the OCN of the object when the object was
 * fetched then the entity manager will throw a mutated object exception to inform the calling process that the save action has failed and that the 
 * object should be re-fetched into the entity manager and the changes to the object remade. If it is not stored in a wrapper then the MD5 hash of the file 
 * holding the objects instance data is calculated when the object is fetched and compared to the MD5 hash of the current file in the repository holding
 * the instance data. If the MD5 hashes are not the same then a mutated object exception is thrown when the object is saved.
 * 
 * When an object is saved the a version of the file holding the objects instance data is created on the file system but in a working directory specific to the 
 * entity manager that is making the change.  This minimises the chance of IOExceptions when committing changes. The object instances data is successfully 
 * written to the entity managers working directory only if it passes the locking, mutation and duplicate keys tests and when the file in the repository for 
 * the objec being written has been locked. When the edits are committed the files in the entity managers working directory are copied to the repository and
 * the locks released.
 * 
 * If the entity manager rolls back its changes then the files in the entity managers working directory are deleted, the locks are released and the 
 * entity managers cache is cleared.
 * 
 * If the entity manager commits its changes then the files in the entity managers working directory are moved to the relevant repositories, the locks are 
 * released and the working directory is cleared. The cache however is not cleared.
 * 
 * @author simon
 *
 */
public class FilesystemEntityManager {
	
	private enum State {
		STARTING,
		OPEN,
		CLOSING,
		CLOSED
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * The file system entity manager factory that created this instance
	 */
	private FilesystemEntityManagerFactory femFactory;
	/**
	 * The id of this entity manager within the file system entity manager factory
	 */
	private String id;
	/**
	 * To minimise the time taken to commit 
	 */
	private File workingDir;
	/**
	 * The entity managers cache of attached object instances
	 */
	private Map<Class<?>, Map<Serializable, CachedObject<?>>> cache = new HashMap<Class<?>, Map<Serializable, CachedObject<?>>>();
	/**
	 * The map of dirty objects. Dirty objects are the objects on the cache that need to be committed to the repositories
	 */
	private Map<Class<?>, Map<Serializable, FemWrapper<?>>> dirty = new HashMap<Class<?>, Map<Serializable, FemWrapper<?>>>();
	/**
	 * The deleted map identifies the cached objects that have been flagged as deleted.
	 */
	private Map<Class<?>, Set<Serializable>> deleted = new HashMap<Class<?>, Set<Serializable>>();
	
	private State state = State.STARTING;

	/**
	 * This method creates an instance of the entity manager for the given filesystem entity manager factory that is creating it
	 * 
	 * The entity manager is set to a 12 digit random string. This id is used to name the working directory for this entity manager
	 * as a child directory of the factories manager directory. If the working directory already exists then a connection currently 
	 * exists for h generated id and a new id is generated until an id is generated for which the working directory doesn't exist.
	 * 
	 * If the working directory doesn't exist it is created and set as the entity managers working directory. This mechanism 
	 * ensures that the connections id is unique across multiple instances of the file system entity manager factory using the same
	 * manager directory.
	 * 
	 * When the entity manager is closed any uncommitted edits are rolled back and the working directory is deleted. All the 
	 * entity managers created by a given factory are closed automatically when the factory is shutdown.
	 * @param femFactory
	 */
	FilesystemEntityManager(FilesystemEntityManagerFactory femFactory) {
		logger.trace("Creating new entity manager for factory on '{}'", femFactory.managerDictoryLocation());
		this.femFactory = femFactory;
		
		id = StringUtil.random(12);
		workingDir = new File(femFactory.connectionsDir().getAbsolutePath()+File.separatorChar+id);
		while (workingDir.exists()) {
			id = StringUtil.random(12);
			workingDir = new File(femFactory.connectionsDir().getAbsolutePath()+File.separatorChar+id);			
		}
		workingDir.mkdirs();
		state = State.OPEN;
		logger.trace("Created entity manager with id '{}'", id);
	}

	/**
	 * This method returns the 12 digit random string id of this entity manager
	 * @return	The 12 digit string id of this entity manager
	 */
	public String getId() { return id; }
	/**
	 * This method adds the given wrapper with its object to the entity managers map of dirty objects for the given class and key
	 * 
	 * @param cls	The class of the object that is now dirty in this entity manager
	 * @param key	The key of the object that is now dirty in this entity manager
	 * @param output		The wrapper containing the object that is now dirty
	 */
	private void dirty(Class<?> cls, Serializable key, FemWrapper<?> wrapper) {
		logger.trace("Marking {}({}) as dirty", cls, key);
		Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
		if (dirtyClasses == null) {
			dirtyClasses = new HashMap<Serializable, FemWrapper<?>>();
			dirty.put(cls, dirtyClasses);
		}
		dirtyClasses.put(key, wrapper);
	}
	/**
	 * This method identifies the given class and key as referencing a deleted object
	 * 
	 * @param cls	The class of the object to be identified as deleted
	 * @param key	The key of the object to be identified as deleted 
	 */
	private void markDeleted(Class<?> cls, Serializable key) {
		logger.trace("Marking {}({}) as deleted", cls, key);
		Set<Serializable> deletedClasses = deleted.get(cls);
		if (deletedClasses == null) {
			deletedClasses = new HashSet<Serializable>();
			deleted.put(cls, deletedClasses);
		}
		deletedClasses.add(key);
	}
	/**
	 * This method identifies whether this entity manager has marked the given class and key as referencing a deleted object
	 * 
	 * @param cls	The class of the object to check for deletion status
	 * @param key	The key of the objec to check for deletion status
	 * @return	True if the given class and key refernce an object instance that this entity manager has marked as deleted
	 */
	private boolean isDeleted(Class<?> cls, Serializable key) {
		Set<Serializable> deletedClasses = deleted.get(cls);
		if (deletedClasses == null) return false;
		return deletedClasses.contains(key);
	}
	/**
	 * This method removed the object instance identified by the given class and key from this entity managers map of dirty objects
	 * 
	 * @param cls	The class of the object to remove from this entity manager map of dirty objects
	 * @param key	The key of the object to remove from this entity manager map of dirty objects
	 */
	private void clearDirty(Class<?> cls, Serializable key) {
		logger.trace("Clearing {}({}) as from the dirty map", cls, key);
		Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
		if (dirtyClasses == null) return;
		dirtyClasses.remove(key);
		if (dirtyClasses.isEmpty()) dirty.remove(cls);
	}
	/**
	 * This method removed the object identified by the given class and key from this entity managers cache of attached objects
	 * @param cls
	 * @param key
	 */
	private <T> void clearCache(Class<T> cls, Serializable key) {
		logger.trace("Clearing {}({}) from the cache", cls, key);
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) return;
		classCache.remove(key);
	}
	/**
	 * This method returns the cached object from the entity managers cache of attached object.
	 * 
	 * The cached object includes the original OCN and the current OCN and potentially MD5 hash
	 * @param cls	The class of the object whose cached information is required
	 * @param key	The key of the object whose cached information is required
	 * @return	The instance of CachedObject for the given class and key or null if it doesn't exist in the entity manager attached object cache
	 */
	@SuppressWarnings("unchecked")
	private <T> CachedObject<T> cacheFetch(Class<T> cls, Serializable key) {
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) return null;
		return (CachedObject<T>) classCache.get(key);
	}
	/**
	 * This method returns the wrapper containing the object for the given class and key from this entity managers cache of attached objects
	 * @param cls	The class of the object whose wrapper is required
	 * @param key	The key of the object whose wrappe is required
	 * @return	The wrapper containing the object for the given class and key or null if the requested instance in not in the entity manager cache of
	 * attached objects
	 */
	@SuppressWarnings("unchecked")
	private <T> FemWrapper<T> cacheFetchWrapper(Class<T> cls, Serializable key) {
		CachedObject<?> cachedObj = cacheFetch(cls, key);
		if (cachedObj == null) return null;
		return (FemWrapper<T>) cachedObj.wrappedObj;
	}
	/**
	 * This method returns the object from this entity managers cache of attached objects.
	 * @param cls	The class of the object required from the entity managers cache of attached objects
	 * @param key	The key of the object required from the entity managers cache of attached objects
	 * @return	The object for the given class and key from the entity managers cache of attached objects or null if it is not in the cache
	 */
	private <T> T cacheFetchObject(Class<T> cls, Serializable key) {
		FemWrapper<T> wrapper = cacheFetchWrapper(cls, key);
		if (wrapper == null) return null;
		return wrapper.obj;
	}
/*	
	private <T> T cache(Class<T> cls, Serializable key, T obj) {
		if (obj == null) return obj;
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) {
			classCache = new HashMap<Serializable, CachedObject<?>>();
			classCache.put(key, new CachedObject<T>(obj));
			cache.put(cls, classCache);
			return obj;
		}
		CachedObject<?> cachedObj = classCache.get(key);
		if (cachedObj == null) {
			classCache.put(key, new CachedObject<T>(obj));
			return obj;
		}
		if (!obj.equals(cachedObj.wrappedObj.obj)) throw new FemError("Attempted to cache different objects with the same class and key '"+cls.getCanonicalName()+"("+key+")'");
		return obj;
	}
*/	
	/**
	 * Add the given wrapper containing an object instance to the cache of attached objects with the given class and key
	 * @param cls	The class of the object in the given wrapper
	 * @param key	The key of the object in the given wrapper
	 * @param wrappedObj		The wrapper containing the object to be added to this entity managers cache of attached objects
	 * @return	The object contained in the given wrapper
	 */
	private <T> T cache(Class<?> cls, Serializable key, FemWrapper<T> wrappedObj) {
		if (wrappedObj == null) return null;
		logger.trace("Adding {}({}) to the entity managers cache", cls, key);
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) {
			classCache = new HashMap<Serializable, CachedObject<?>>();
			classCache.put(key, new CachedObject<T>(wrappedObj));
			cache.put(cls, classCache);
			return wrappedObj.obj;
		}
		CachedObject<?> cachedObj = classCache.get(key);
		if (cachedObj == null) {
			classCache.put(key, new CachedObject<T>(wrappedObj));
			return wrappedObj.obj;
		}
		if (!wrappedObj.obj.equals(cachedObj.wrappedObj.obj)) throw new FemError("Attempted to cache different objects with the same class and key '"+cls.getCanonicalName()+"("+key+")'");
		return wrappedObj.obj;
	}
	/**
	 * This method is a utility for obtaining an instance of FileReader to read the resource for the given class and key in the repository appropriate to the given class and key
	 * @param cls	The class of the object for which a file reader is required
	 * @param key	The key of the object for which a file reader is required
	 * @return	An instance of FileReader ready to read the resource that holds the instance data for the object instance identified by the given class and key or null if the relevant resource 
	 * doesn't exist
	 */
	private FileReader objectReader(Class<?> cls, Serializable key) {
		FemObjectConfig objConf = checkConfig(cls);

		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) return null;

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		File objResource = objResource(repoLocation, objDataFormat, cls, key);

		FileReader objReader;
		try {
			objReader = new FileReader(objResource);
		} catch (FileNotFoundException e) {
			return null;
		}
		
		return objReader;

	}
	/**
	 * This method is a utility to identity the resource that holds or will hold the instance data for the object instance identified by the given class and key in the given resource
	 * using the given data structure (JSON or XML)
	 * @param resourceLocation	The directory within the relevant repository in which the objects instance data is configured to be persisted either the entity managers working directory
	 * or the repository configured to save the instances persistent data.
	 * @param dataFormat		The format in which the instances data is configured to be persisted (JSON or XML)
	 * @param cls			The class of the object whose resource file is required
	 * @param key			The key of the object whose resource file is required
	 * @return				A file referencing the storage resource (working or repository) for the given class and key
	 */
	private File objResource(File resourceLocation, FemDataFormat dataFormat, Class<?> cls, Serializable key) {
		switch(dataFormat) {
		case JSON:
			return new File(resourceLocation.getAbsolutePath()+File.separatorChar+key+FilesystemEntityManagerFactory.JSON);
		case XML:
			return new File(resourceLocation.getAbsolutePath()+File.separatorChar+key+FilesystemEntityManagerFactory.XML);
		default:
			throw new FemError("Unsupported data format: "+dataFormat+" fetching '"+cls.getCanonicalName()+"("+key+")'");
		}

	}
	/**
	 * This method is a utility to retrieve an instance of FileWriter to write an objects persistent data
	 * 
	 * @param cls	The class of the object whose persistent data will be written by the returned file writer.
	 * @param key	The key of the object whose persistent data will be written by the turned file writer
	 * @return	An instance of FileWriter to write the persistent data from an objects instance data using a file extension 
	 * appropriate for the current storage configuration for the given class and key. If the writers file does not exist then a new file is created
	 */
	private FileWriter objectWriter(Class<?> cls, Serializable key) {
		
		FemObjectConfig objConf = checkConfig(cls);

		File workingLocation = workingLocation(objConf);

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		File objResource = objResource(workingLocation, objDataFormat, cls, key);
		
		if (!objResource.exists())
			try {
				objResource.createNewFile();
			} catch (IOException e) {
				throw new FemError("Unable to write to '"+objResource.getAbsolutePath()+"'", e);
			}

		FileWriter objWriter;
		try {
			objWriter = new FileWriter(objResource);
		} catch (FileNotFoundException e) {
			// This shouldn't happen
			return null;
		} catch (IOException e) {
			throw new FemError("Unable to write to '"+objResource.getAbsolutePath()+"'", e);
		}
		
		return objWriter;

	}
	/**
	 * This method returns the data format from the given storage configuration
	 * @param conf	The storage configuration from which to extract the storage data format
	 * @return	The storage data format in which the objects instance data has been or will be stored. If the storage configuration does not
	 * define a storage format then the default storage format configured for the entity manager factory is returned
	 */
	private FemDataFormat dataFormat(FemObjectConfig conf) {
		if (conf.dataFormat() == null) {
			return femFactory.config().dataFormat();
		} else {
			return conf.dataFormat();
		}
	}
	/**
	 * This method returns as an instance of File (directory) the location within the repository in which an objects persistent data will be/is written 
	 * @param conf	The storage configuration for a particular class
	 * @return	An instance of File representing the directory in which an objects instance data will be/is saved as a file. 
	 */
	private File repoLocation(FemObjectConfig conf) {
		File repo = femFactory.repository(conf.repository());
		File repoLocation = new File(repo.getAbsolutePath()+File.separatorChar+conf.resourcePath());
		return repoLocation;
	}
	/**
	 * This method returns as an instance of File (directory) the location within this entity managers working directory in which an objects persistent data
	 * will be written.
	 * @param conf	The storage configuration for the class for which the working location is required
	 * @return	An instance of File representing the directory in this entity managers working directory where instances of the classes data will be written 
	 * as files. If the directory doesn't exist it is created. If it does exist but is not a directory or cannot be read or written an unchecked FemError is thrown
	 */
	private File workingLocation(FemObjectConfig conf) {
		File workingRepo = new File(workingDir.getAbsolutePath()+File.separatorChar+conf.repository());
		if (!workingRepo.exists()) {
			workingRepo.mkdirs();
		} else {
			if(!workingRepo.isDirectory()) throw new FemError("The working repository '"+workingRepo.getAbsolutePath()+"' exists and is not a directory");
			if(!workingRepo.canWrite()) throw new FemError("Unable to write to the working repository '"+workingRepo.getAbsolutePath()+"'");
			if(!workingRepo.canRead()) throw new FemError("Unable to read from the working repository '"+workingRepo.getAbsolutePath()+"'");
		}
		File workingLocation = new File(workingRepo.getAbsolutePath()+File.separatorChar+conf.resourcePath());
		if (!workingLocation.exists()) {
			workingLocation.mkdirs();
		} else {
			if(!workingLocation.isDirectory()) throw new FemError("The working repository location '"+workingLocation.getAbsolutePath()+"' exists and is not a directory");
			if(!workingLocation.canWrite()) throw new FemError("Unable to write to the working repository location '"+workingLocation.getAbsolutePath()+"'");
			if(!workingLocation.canRead()) throw new FemError("Unable to read from the working repository location '"+workingLocation.getAbsolutePath()+"'");
		}
		return workingLocation;
	}
	/**
	 * This method gets the storage configuration for a given class
	 * @param cls	The class for which the storage configuration is required
	 * @return	The storage configuration for the given class. If the class is not configured to be persisted by this entity manager an unchecked
	 * FemError is thrown
	 */
	private FemObjectConfig checkConfig(Class<?> cls) {
		FemObjectConfig objConf = femFactory.config().getObjectConfig(cls);
		if (objConf == null) throw new FemError("The file system entity manager has not been configured to handle instances of the class '"+cls.getCanonicalName()+"'");
		return objConf;
	}
	/**
	 * This method identifies whether the given class and key have a resource in the relevant repository
	 * @param cls	The class to be checked
	 * @param key	The key of the instance of this class to be check for a resource in the repository
	 * @return	True if the class and key combination has a resource in the relevant repository
	 */
	private boolean repoHasResource(Class<?> cls, Serializable key) {
		FemObjectConfig objConf = checkConfig(cls);

		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) return false;

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		File objResource = objResource(repoLocation, objDataFormat, cls, key);
		
		return objResource.exists();
	}
	/**
	 * The map of file locks held by this entity manager
	 */
	private Map<Class<?>, Map<Serializable, FileLock>> locks = new HashMap<Class<?>, Map<Serializable, FileLock>>();
	/**
	 * This method add the given lock to the map of locks held by this entity manager
	 * @param cls	The class of the object that is locked by the given file lock
	 * @param key	The key of the object that is locked by the given file lock
	 * @param lock	The file lock locking the resource for the instance data of the given class and key
	 */
	private void addLock(Class<?> cls, Serializable key, FileLock lock) {
		logger.trace("Adding lock to the locks map for {}({})", cls, key);
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) {
			classLocks = new HashMap<Serializable, FileLock>();
			locks.put(cls, classLocks);
		}
		classLocks.put(key, lock);
	}
/*
	private FileLock getLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) {
			throw new FemError("No locks held for class '"+cls.getCanonicalName()+"'");
		}
		FileLock lock = classLocks.get(key);
		if (lock == null) throw new FemError("No lock held for class '"+cls.getCanonicalName()+"' and key '"+key+"'");
		return lock;
	}
*/
	/**
	 * This method identifies whether this entity manager holds a lock for the given class and key
	 * @param cls	The class to check for a lock
	 * @param key	The key of the object in the class for check for a lock
	 * @return	True if this entity manager holds a lock for the given class and key
	 */
	private boolean holdsLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) return false;
		return classLocks.containsKey(key);
	}
/*	
	private void removeLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) return;
		classLocks.remove(key);
		if (classLocks.isEmpty()) locks.remove(cls);
	}
*/	
	/**
	 * This method attempts to lock the resource file for the given class and key in the repository.
	 * 
	 * If the resource file dosn't exist it is created before being locked. Thus ensuring that another entity manager can't
	 * also save an object with the same class and key
	 * @param cls	The class on the object to be locked
	 * @param key	The key of the object to be locked
	 * @throws FemObjectLockedException If the resource for the class and key is already locked
	 */
	private <T> void lockInRepo(Class<T> cls, Serializable key) throws FemObjectLockedException {
		if (!holdsLock(cls, key)) {

			logger.trace("Locking resource for {}({})", cls, key);

			FemObjectConfig objConf = checkConfig(cls);
			
			FemDataFormat objDataFormat = dataFormat(objConf);
			
			File repoLocation = repoLocation(objConf);
			
			File objResource = objResource(repoLocation, objDataFormat, cls, key);
			
			if (!objResource.exists()) {
				try {
					objResource.createNewFile();
				} catch (IOException e) {
					throw new FemError("Unable to create file '"+objResource.getAbsolutePath()+"'", e);
				}
			}
		
			try {
				addLock(cls, key, FileUtil.lock(objResource));
			} catch (FileLockedException e) {
				throw new FemObjectLockedException(cls, key, objResource);
			}
		}
		
	}
/*	
	@SuppressWarnings("resource")
	private <T> void unlockInRepo(Class<T> cls, Serializable key) {
		
		FileLock lock = getLock(cls, key);
		
		try {
			lock.release();
		} catch (IOException e) {
			throw new FemError("Failed to unlock class '"+cls.getCanonicalName()+"' with key '"+key+"'", e);
		}
		
		removeLock(cls, key);
	}
*/
	/**
	 * This method reads the current OCN of the given class and key in the repository
	 * @param cls	The class to check
	 * @param key	The key of the instance of the class to check
	 * @return	The OCN of the object in the repository
	 */
	private <T> Integer readOcn(Class<T> cls, Serializable key) {
		
		if (cls == null || key == null) return null;
		if (isDeleted(cls, key)) return null;
		
		FemObjectConfig objConf = checkConfig(cls);

		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) return null;

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		FileReader objReader = objectReader(cls, key);
		if (objReader == null) return null;		
		
		FemOcn wrappedOcn = null;
	
		switch(objDataFormat) {
		case JSON:
			wrappedOcn = femFactory.gson().fromJson(objReader, FemOcn.class);
			break;
		case XML:
			JAXBContext ctx = femFactory.config().getJaxbContext(cls);
			try {
				Unmarshaller um = ctx.createUnmarshaller();
				wrappedOcn = (FemOcn)um.unmarshal(objReader);
			} catch (JAXBException e) {
				throw new FemError(e);
			}

			break;
		}
		
		if (wrappedOcn == null) return null;
		return wrappedOcn.ocn;
		
	}
	/**
	 * Get the OCN of the current version of the object held in this entity managers cache for the given object
	 * @param obj	The object for which to get the OCN	
	 * @return	The OCN of the given object in this entity managers cache of attached objects. null if the object is
	 * null or not cached by this entity manager.
	 */
	public Integer getOcn(Object obj) {
		if (state != State.OPEN) throw new FemError("Unable to execute methods on a connection that is {}", state); 
		if (obj == null) return null;
		Serializable key;
		key = IdentityUtil.getId(obj);
		FemWrapper<?> wrapper = cacheFetchWrapper(obj.getClass(), key);
		
		return (wrapper == null) ? null : wrapper.ocn;
	}
	/**
	 * Get the OCN for the given object identified when the object was fetched into this entity managers cache of attached objects
	 * @param obj for which to extract the original OCN from this entity managers cache
	 * @return	The original OCN of the object when it was fetched into this entity managers cache
	 */
	public Integer getOriginalOcn(Object obj) {
		if (state != State.OPEN) throw new FemError("Unable to execute methods on a connection that is {}", state); 
		if (obj == null) return null;
		Serializable key;
		key = IdentityUtil.getId(obj);
		CachedObject<?> cached = cacheFetch(obj.getClass(), key);
		
		return (cached == null) ? null : cached.originalOcn;
	}

	/**
	 * This method fetches the persisted data for the given class and key
	 * 
	 * @param cls	The class of the object to fetch
	 * @param key	The key identifying the object to fetch
	 * @param <T>	The type of the object to fetch
	 * @return	The instance of the given class with the given key. If the class and key do not identify a persisted instance of the given
	 * class a null is returned
	 */
	@SuppressWarnings("unchecked")
	public <T> T fetch(Class<T> cls, Serializable key) {
		if (state != State.OPEN) throw new FemError("Unable to fetch on a connection that is {}", state); 
		if (cls == null) return null;
		if (key == null) return null;
		
		logger.debug("fetch: {}({})", cls, key);

		// Check this class is configured to be managed by this entity manager and get the storage configuration if it is
		FemObjectConfig objConf = checkConfig(cls);

		// If this object has been deleted in this entity manager return null
		if (isDeleted(cls, key)) {
			logger.trace("Has been deleted: {}({})", cls, key);
			return null;
		}
		// Fetch the object from this entity managers cache
		T cachedObj = cacheFetchObject(cls, key);
		if (cachedObj != null) {
			// If the object was found in the cache return it
			logger.trace("Cache hit: {}({})", cls, key);
			return cachedObj;
		}
		
		// The object wasn't found in the cache so check the repository
		File repoLocation = repoLocation(objConf);
		logger.trace("repo location: '{}'", repoLocation.getAbsolutePath());
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) {
			// If there isn't a resource location in the repository for the given class and key return null
			logger.trace("not found: {}({})", cls, key);
			return null;
		}

		// Identify the format in which the objects data is stored in the repository
		FemDataFormat objDataFormat = dataFormat(objConf);
		
		// If there isn't a resource in the repository for the given class and key return null
		FileReader objReader = objectReader(cls, key);
		if (objReader == null) {
			logger.trace("not found: {}({})", cls, key);
			return null;		
		}
		
		// Read the object resource and unmarshall into a wrapped object using an unmarshaller appropriate for the objects 
		// data structure
		logger.trace("read from reader: '{}'", objReader);
		FemWrapper<T> wrappedObj = null;
		switch(objConf.dataStructure()) {
		case OCN:
			switch(objDataFormat) {
			case JSON:
				femFactory.localType().set(cls);
				wrappedObj = femFactory.gson().fromJson(objReader, FemWrapper.class);
				femFactory.localType().remove();
				break;
			case XML:
				JAXBContext ctx = femFactory.config().getJaxbContext(cls);
				try {
					Unmarshaller um = ctx.createUnmarshaller();
					wrappedObj = (FemWrapper<T>)um.unmarshal(objReader);
				} catch (JAXBException e) {
					throw new FemError(e);
				}
				break;
			}
			break;
		case RAW:
			switch(objDataFormat) {
			case JSON:
				wrappedObj = new FemWrapper<T>(femFactory.gson().fromJson(objReader, cls));
				break;
			case XML:
				JAXBContext ctx = femFactory.config().getJaxbContext(cls);
				try {
					Unmarshaller um = ctx.createUnmarshaller();
					wrappedObj = new FemWrapper<T>((T)um.unmarshal(objReader));
				} catch (JAXBException e) {
					throw new FemError(e);
				}
				break;
			}
			File objResource = objResource(repoLocation, objDataFormat, cls, key);
			if (objResource.exists()) {
				wrappedObj.hash = FileUtil.hash(objResource);
			} else {
				wrappedObj.hash = "";
			}
			break;
		default:
			throw new FemError("Unsupported data structure: "+objConf.dataStructure()+" fetching '"+cls.getCanonicalName()+"("+key+")'");
		}
		
		// If the unmarshaller returned a null object return  null
		if (wrappedObj == null || wrappedObj.obj == null) {
			logger.trace("not found: {}({})", cls, key);
			return null;
		}
		// Add the fetched and wrapped instance to this entity managers cache of attachd objects
		cache(cls, key, wrappedObj);
		
		// Return the object fetched from the repository
		logger.trace("found: {}({})", cls, key);
		return wrappedObj.obj;
		
	}

	/**
	 * Save the given object in the repository appropriate for the class of the object to save
	 * @param obj	The object to save
	 * @param <T>	The type of the object to save
	 * @return	The object that was saved and cached in this entity managers cache of attached objects
	 * @throws FemObjectLockedException		If the object is locked by another entity manager instance
	 * @throws FemDuplicateKeyException		If the object is not attached to this entity manager and already exists in the the 
	 * 										relevant repository
	 * @throws FemMutatedObjectException		If the object has been saved and committed by another entity manager since this object
	 * 										was fetched into this entity managers cache of attached objects
	 */
	@SuppressWarnings("unchecked")
	public <T> T save(T obj) throws FemObjectLockedException, FemDuplicateKeyException, FemMutatedObjectException {
		if (state != State.OPEN) throw new FemError("Unable to save on a connection that is {}", state); 
		if (obj == null) return null;

		// Get the key identifying the object
		Serializable key;
		key = IdentityUtil.getId(obj);

		logger.trace("Save {}({})", obj.getClass(), key);

		// Check that this objects class has been configured to be persisted by by this entity manager and fetch the storage configuration
		// for the class of the object to save
		FemObjectConfig objConf = checkConfig(obj.getClass());
		
		// Get the data format of the object to save
		FemDataFormat objDataFormat = dataFormat(objConf);
		
		// Get a FileWriter to write the instance data for the given object in this entity managers working directory
		FileWriter objWriter = objectWriter(obj.getClass(), key);
		
		// Ensure that there is a location in the repository to save this object instance data
		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) repoLocation.mkdirs();

		// Get the cached version of this object from this instances cache of attachd objects
		CachedObject<T> cached = (CachedObject<T>) cacheFetch(obj.getClass(), key);
	
		// Add the object to this entity managers cache and check for duplicate keys and mutated objects
		Object output = null;
		switch(objConf.dataStructure()) {
		case OCN:
		// If the object is wrapped with an OCN
			// If the object doesn't exist in the cache
			if (cached == null) {
				// If there is a resource in the repository for the given object throw a duplicate key exception
				if (repoHasResource(obj.getClass(), key)) throw new FemDuplicateKeyException(obj.getClass(), key, repoLocation);
				// Add the object to this entity managers cache and mark it as dirty
				output = new FemWrapper<T>(obj);
				cache(obj.getClass(), key, (FemWrapper<T>)output);
				dirty(obj.getClass(), key, (FemWrapper<T>)output);
			} else {
			// If the object does exist in the cache
				// Check the OCN of the cached version of this object. If it doesn't match the OCN currently stored in the repository
				// Throw an object mutated exception
				if (!holdsLock(obj.getClass(), key)) {
					Integer readOcn = readOcn(obj.getClass(), key);
					if (readOcn != null && !cached.originalOcn.equals(readOcn)) {
						clearCache(obj.getClass(), key);
						throw new FemMutatedObjectException(obj.getClass(), key, repoLocation);
					}
				}
				// Update the cache with the object to save and mark it as dirty
				cached.wrappedObj.ocn++;
				cached.wrappedObj.obj = obj;
				output = cached.wrappedObj;
				dirty(obj.getClass(), key, cached.wrappedObj);
			}
			break;
		case RAW:
		// If the object is stored RAW
			// If the object doesn't exist in the cache
			if (cached == null) {
				// If the repository has a resource for the given object throw a duplicate key exception
				if (repoHasResource(obj.getClass(), key)) throw new FemDuplicateKeyException(obj.getClass(), key, repoLocation);
				// Add the object to this enity managers cache of attached objects and mark it as dirty
				output = obj;
				FemWrapper<T> wrapper = new FemWrapper<T>(obj);
				cache(obj.getClass(), key, wrapper);
				dirty(obj.getClass(), key, wrapper);
			} else {
			// If the object does exist in the cache
				// Compare the hash generated when the object was fetched into the cache with the has of the object in the repository
				// If the hashes don't match throw an object mutated exception
				if (!holdsLock(obj.getClass(), key)) {
					File objResource = objResource(repoLocation, objDataFormat, obj.getClass(), key);
					if (!cached.wrappedObj.hash.equals(FileUtil.hash(objResource))) {
						clearCache(obj.getClass(), key);
						throw new FemMutatedObjectException(obj.getClass(), key, repoLocation);
					}
				}
				// Update the cache with the object to save and mark it as dirty
				cached.wrappedObj.obj = obj;
				output = obj;
				dirty(obj.getClass(), key, cached.wrappedObj);
			}
			break;
		default:
			throw new FemError("Unsupported data structure: "+objConf.dataStructure()+" saving '"+obj.getClass().getCanonicalName()+"("+key+")'");
		}
		
		// Lock the file in the repository to prevent any other entity manager from editing this object
		// If the object is new an empty file is created to hold the lock
		lockInRepo(obj.getClass(), key);
		
		// Write the objects instance data to this entity managers working directory in the appropriate format using the appropriate marshaller
		switch(objDataFormat) {
		case JSON:
			femFactory.gson().toJson(output, objWriter);
			break;
		case XML:
			JAXBContext ctx = femFactory.config().getJaxbContext(obj.getClass());
			try {
				Marshaller m = ctx.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(output, objWriter);
			} catch (JAXBException e) {
				throw new FemError(e);
			}
			break;
		}
		try {
			objWriter.flush();
			objWriter.close();
		} catch (IOException e) {
			throw new FemError("Unable to write to '{}'", e, objWriter);
		}

		// return the saved object 
		return obj;
	}

	/**
	 * This method identifies an object as deleted in this entity manager and locks its resource in the repository to prevent other entity managers
	 * from updating it
	 * @param obj	The object to delete
	 * @param <T>	The type of the object to delete
	 * @return		The object that has been identified as deleted
	 * @throws FemObjectLockedException	If this object has been locked by another entity manager
	 */
	public <T> T delete(T obj) throws FemObjectLockedException {
		if (state != State.OPEN) throw new FemError("Unable to delete on a connection that is {}", state); 
		if (obj == null) return null;
		
		// Check that this objects class has been configured to be persisted by by this entity manager and fetch the storage configuration
		// for the class of the object to save
		@SuppressWarnings("unused")
		FemObjectConfig objConf = checkConfig(obj.getClass());
				
		// Get the key identifying the object
		Serializable key;
		key = IdentityUtil.getId(obj);
		
		// If the object doesn't have a key then it cannot be deleted by the entity manager
		if (key == null) throw new FemError("Unable to delete an instance of '{}' with a null key", obj.getClass()) ;

		logger.trace("Delete {}({})", obj.getClass(), key);

		// Check that the object is managed by this entity manager
		// If it is not managed by this entity manager throw an uncheck FemError
		@SuppressWarnings("unchecked")
		CachedObject<T> cached = (CachedObject<T>) cacheFetch(obj.getClass(), key);
		if (cached == null) throw new FemDetachedObjectError(obj.getClass(), key, id);
		
		// Lock the resource for this object in the entity manager
		lockInRepo(obj.getClass(), key);
		
		// Identity this object as deleted in this entity manager
		markDeleted(obj.getClass(), key);
		// Remove this object from this entity managers cache
		clearCache(obj.getClass(), key);
		// Remove this object from this entity managers map of dirty objects
		clearDirty(obj.getClass(), key);
		
		// Return the object that has been deleted
		return obj;
	}
	
	/**
	 * Commit the current changes managed by this entity manager to the repository
	 */
	public void commit() {
		if (state != State.OPEN) throw new FemError("Unable to commit on a connection that is {}", state); 
		
		logger.trace("Commit changes in entity manager '{}'", id);

		// For all objects that are identified as dirty
		for (Class<?> cls : dirty.keySet()) {
			Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
			FemObjectConfig objConfig = checkConfig(cls);
			FemDataFormat dataFormat = dataFormat(objConfig);
			File repoLocation = repoLocation(objConfig);
			File workingLocation = workingLocation(objConfig);
			for (Serializable key : dirtyClasses.keySet()) {
				FemWrapper<?> wrapper = dirtyClasses.get(key);
				CachedObject<?> cached = cacheFetch(cls, key);
				File workingFile = objResource(workingLocation, dataFormat, cls, key);
				File repoFile = objResource(repoLocation, dataFormat, cls, key);
				
				try {
					Files.move(workingFile.toPath(), repoFile.toPath(), ATOMIC_MOVE, REPLACE_EXISTING);
					cached.originalOcn = wrapper.ocn;
				} catch (IOException e) {
					try {
						Files.move(workingFile.toPath(), repoFile.toPath(), REPLACE_EXISTING);
					} catch (IOException e1) {
						throw new FemError("Unable to move '"+workingFile.getAbsolutePath()+"' to '"+repoLocation.getAbsolutePath()+"'", e);
					}
				}
				
			}
			
		}
		// Clear the dirty map
		dirty.clear();
		
		// For all the objects that have been marked as deleted delete the resource in the repository
		for (Class<?> cls : deleted.keySet()) {
			Set<Serializable> deletedClasses = deleted.get(cls);
			FemObjectConfig objConfig = checkConfig(cls);
			FemDataFormat dataFormat = dataFormat(objConfig);
			File repoLocation = repoLocation(objConfig);
			for (Serializable key : deletedClasses) {
				
				objResource(repoLocation, dataFormat, cls, key).delete();
				
			}
		}
		// Clear the deleted map
		deleted.clear();
		// Clear the locks held by this entity manager
		// Because all the files that were locked are delete or replaced the old locks are no longer active
		locks.clear();
		
		
		
	}

	/**
	 * Discard all the changes currently managed by this entity manager and release all the locks held by this entity manager
	 */
	public void rollback() {
		if (state != State.OPEN) throw new FemError("Unable to rollback on a connection that is {}", state); 
		
		logger.trace("Rollback changes in entity manager '{}'", id);

		// Delete all the files currently in the entity managers working directory
		for (File f : workingDir.listFiles()) FileUtil.deleteCascade(f);
		// Clear the deleted map
		deleted.clear();
		// Clear the dirty map
		dirty.clear();
		// Clear the cache
		cache.clear();
		
		// release all the locks held by this entity manager
		for (Class<?> cls : locks.keySet()) {
			Map<Serializable, FileLock> classLocks = locks.get(cls);
			for (Serializable key : classLocks.keySet()) {
				FileLock lock = classLocks.get(key);
				try {
					if (lock.channel().size() == 0) {
						FemObjectConfig objConfig = checkConfig(cls);
						File repoLocation = repoLocation(objConfig);
						FemDataFormat dataFormat = dataFormat(objConfig);
						File repoFile = objResource(repoLocation, dataFormat, cls, key);
						repoFile.delete();
					}
					lock.release();
				} catch (IOException e) {
					logger.error("Unable to relase lock for '"+cls.getName()+"("+key+")", e);
				}
			}
		}
		// Clear the map of locks held by this entity manager
		locks.clear();
		
	}

	/**
	 * This method closes this entity manager
	 * 
	 * Any uncommitted changes are rolled back and the entity manager factory is informed that the entity manager has been closed
	 * All the contents of the working directory and the working directory itself is deleted
	 */
	public void close() {
		if (state == State.OPEN) {
			logger.trace("Close entity manager '{}'", id);

			state = State.CLOSING;
			FileUtil.deleteCascade(workingDir);
			femFactory.closed(this);
			state = State.CLOSED;
		}
		
	}
	/**
	 * When the last reference to this entity manager is lost then the entity manager will close automatically
	 */
	@Override
	public void finalize() {
		close();
	}
	

}
