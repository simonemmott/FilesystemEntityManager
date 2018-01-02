package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.k2.Util.FileUtil;
import com.k2.Util.StringUtil;
import com.k2.Util.Identity.Id;
import com.k2.Util.Identity.IdentityUtil;
import com.k2.Util.Version.Increment;
import com.k2.Util.exceptions.FileLockedException;

public class FilesystemEntityManager {
	
	private FilesystemEntityManagerFactory femFactory;
	private String id;
	private File workingDir;
	
	private Map<Class<?>, Map<Serializable, CachedObject<?>>> cache = new HashMap<Class<?>, Map<Serializable, CachedObject<?>>>();
	private Map<Class<?>, Map<Serializable, FemWrapper<?>>> dirty = new HashMap<Class<?>, Map<Serializable, FemWrapper<?>>>();
	private Map<Class<?>, Set<Serializable>> deleted = new HashMap<Class<?>, Set<Serializable>>();

	public FilesystemEntityManager(FilesystemEntityManagerFactory femFactory) {
		this.femFactory = femFactory;
		
		id = StringUtil.random(12);
		workingDir = new File(femFactory.connectionsDir().getAbsolutePath()+File.separatorChar+id);
		while (workingDir.exists()) {
			id = StringUtil.random(12);
			workingDir = new File(femFactory.connectionsDir().getAbsolutePath()+File.separatorChar+id);			
		}
		workingDir.mkdirs();
	}

	public String getId() { return id; }
	
	private void dirty(Class<?> cls, Serializable key, FemWrapper<?> output) {
		Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
		if (dirtyClasses == null) {
			dirtyClasses = new HashMap<Serializable, FemWrapper<?>>();
			dirty.put(cls, dirtyClasses);
		}
		dirtyClasses.put(key, output);
	}
	
	private void markDeleted(Class<?> cls, Serializable key) {
		Set<Serializable> deletedClasses = deleted.get(cls);
		if (deletedClasses == null) {
			deletedClasses = new HashSet<Serializable>();
			deleted.put(cls, deletedClasses);
		}
		deletedClasses.add(key);
	}
	
	private boolean isDeleted(Class<?> cls, Serializable key) {
		Set<Serializable> deletedClasses = deleted.get(cls);
		if (deletedClasses == null) return false;
		return deletedClasses.contains(key);
	}

	private void clearDirty(Class<?> cls, Serializable key) {
		Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
		if (dirtyClasses == null) return;
		dirtyClasses.remove(key);
		if (dirtyClasses.isEmpty()) dirty.remove(cls);
	}

	private <T> void clearCache(Class<T> cls, Serializable key) {
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) return;
		classCache.remove(key);
	}
	
	@SuppressWarnings("unchecked")
	private <T> CachedObject<T> cacheFetch(Class<T> cls, Serializable key) {
		Map<Serializable, CachedObject<?>> classCache = cache.get(cls);
		if (classCache == null) return null;
		return (CachedObject<T>) classCache.get(key);
	}
	
	@SuppressWarnings("unchecked")
	private <T> FemWrapper<T> cacheFetchWrapper(Class<T> cls, Serializable key) {
		CachedObject<?> cachedObj = cacheFetch(cls, key);
		if (cachedObj == null) return null;
		return (FemWrapper<T>) cachedObj.wrappedObj;
	}
	
	private <T> T cacheFetchObject(Class<T> cls, Serializable key) {
		FemWrapper<T> wrapper = cacheFetchWrapper(cls, key);
		if (wrapper == null) return null;
		return wrapper.obj;
	}
	
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
	
	private <T> T cache(Class<?> cls, Serializable key, FemWrapper<T> wrappedObj) {
		if (wrappedObj == null) return null;
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
	
	private FemDataFormat dataFormat(FemObjectConfig conf) {
		if (conf.dataformat() == null) {
			return femFactory.config().dataFormat();
		} else {
			return conf.dataformat();
		}
	}
	
	private File repoLocation(FemObjectConfig conf) {
		File repo = femFactory.repository(conf.repository());
		File repoLocation = new File(repo.getAbsolutePath()+conf.resourcePath());
		return repoLocation;
	}
	
	private File workingLocation(FemObjectConfig conf) {
		File workingRepo = new File(workingDir.getAbsolutePath()+File.separatorChar+conf.repository());
		if (!workingRepo.exists()) {
			workingRepo.mkdirs();
		} else {
			if(!workingRepo.isDirectory()) throw new FemError("The working repository '"+workingRepo.getAbsolutePath()+"' exists and is not a directory");
			if(!workingRepo.canWrite()) throw new FemError("Unable to write to the working repository '"+workingRepo.getAbsolutePath()+"'");
			if(!workingRepo.canRead()) throw new FemError("Unable to read from the working repository '"+workingRepo.getAbsolutePath()+"'");
		}
		File workingLocation = new File(workingRepo.getAbsolutePath()+conf.resourcePath());
		if (!workingLocation.exists()) {
			workingLocation.mkdirs();
		} else {
			if(!workingLocation.isDirectory()) throw new FemError("The working repository location '"+workingLocation.getAbsolutePath()+"' exists and is not a directory");
			if(!workingLocation.canWrite()) throw new FemError("Unable to write to the working repository location '"+workingLocation.getAbsolutePath()+"'");
			if(!workingLocation.canRead()) throw new FemError("Unable to read from the working repository location '"+workingLocation.getAbsolutePath()+"'");
		}
		return workingLocation;
	}
	
	private FemObjectConfig checkConfig(Class<?> cls) {
		FemObjectConfig objConf = femFactory.config().objectConfig(cls);
		if (objConf == null) throw new FemError("The file system entity manager has not been configured to handle instances of the class '"+cls.getCanonicalName()+"'");
		return objConf;
	}

	private boolean repoHasResource(Class<?> cls, Serializable key) {
		FemObjectConfig objConf = checkConfig(cls);

		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) return false;

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		File objResource = objResource(repoLocation, objDataFormat, cls, key);
		
		return objResource.exists();
	}
	private Map<Class<?>, Map<Serializable, FileLock>> locks = new HashMap<Class<?>, Map<Serializable, FileLock>>();
	private void addLock(Class<?> cls, Serializable key, FileLock lock) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) {
			classLocks = new HashMap<Serializable, FileLock>();
			locks.put(cls, classLocks);
		}
		classLocks.put(key, lock);
	}
	private FileLock getLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) {
			throw new FemError("No locks held for class '"+cls.getCanonicalName()+"'");
		}
		FileLock lock = classLocks.get(key);
		if (lock == null) throw new FemError("No lock held for class '"+cls.getCanonicalName()+"' and key '"+key+"'");
		return lock;
	}
	private boolean holdsLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) return false;
		return classLocks.containsKey(key);
	}
	
	private void removeLock(Class<?> cls, Serializable key) {
		Map<Serializable, FileLock> classLocks = locks.get(cls);
		if (classLocks == null) return;
		classLocks.remove(key);
		if (classLocks.isEmpty()) locks.remove(cls);
	}
	
	private <T> void lockInRepo(Class<T> cls, Serializable key) throws FemObjectLockedException {
		if (!holdsLock(cls, key)) {
		
			FemObjectConfig objConf = checkConfig(cls);
			
			FemDataFormat objDataFormat = dataFormat(objConf);
			
			File repoLocation = repoLocation(objConf);
			if (!(repoLocation.exists())) repoLocation.mkdirs();
			
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
				throw new FemObjectLockedException(cls, key, objResource, e);
			}
		}
		
	}
	
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

	private String logMessage(String msg) {
		return "Connection: '"+id+"' "+msg;
	}
	private void error(String msg, Throwable e) {
		femFactory.logger().log(Level.SEVERE, logMessage(msg), e);
	}
	private void warning(String msg, Throwable e) {
		femFactory.logger().log(Level.WARNING, logMessage(msg), e);
	}
	private void severe(String msg) {
		femFactory.logger().severe(logMessage(msg));
	}
	private void warning(String msg) {
		femFactory.logger().warning(logMessage(msg));
	}
	private void info(String msg) {
		femFactory.logger().info(logMessage(msg));
	}
	private void fine(String msg) {
		femFactory.logger().fine(logMessage(msg));
	}
	private void finer(String msg) {
		femFactory.logger().finer(logMessage(msg));
	}
	private void finest(String msg) {
		femFactory.logger().finest(logMessage(msg));
	}
	
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
			break;
		}
		
		if (wrappedOcn == null) return null;
		return wrappedOcn.ocn;
		
	}

	@SuppressWarnings("unchecked")
	public <T> T fetch(Class<T> cls, Serializable key) {
		
		if (cls == null || key == null) return null;
		if (isDeleted(cls, key)) return null;
		
		T cachedObj = cacheFetchObject(cls, key);
		if (cachedObj != null) return cachedObj;
		
		FemObjectConfig objConf = checkConfig(cls);

		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) return null;

		FemDataFormat objDataFormat = dataFormat(objConf);
		
		FileReader objReader = objectReader(cls, key);
		if (objReader == null) return null;		
		
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
				break;
			}
			break;
		case OCN_AND_VER:
			switch(objDataFormat) {
			case JSON:
				femFactory.localType().set(cls);
				wrappedObj = femFactory.gson().fromJson(objReader, FemWrapperAndVersion.class);
				femFactory.localType().remove();
				break;
			case XML:
				break;
			}
			break;
		case RAW:
			switch(objDataFormat) {
			case JSON:
				wrappedObj = new FemWrapper<T>(femFactory.gson().fromJson(objReader, cls));
				break;
			case XML:
				break;
			}
			break;
		default:
			throw new FemError("Unsupported data structure: "+objConf.dataStructure()+" fetching '"+cls.getCanonicalName()+"("+key+")'");
		}
		
		cache(cls, key, wrappedObj);
		
		if (wrappedObj == null) return null;
		return wrappedObj.obj;
		
	}

	@SuppressWarnings("unchecked")
	public <T> T save(T obj) throws FemObjectLockedException, FemDuplicateKeyException, FemMutatedObjectException {

		Serializable key;
		try {
			key = IdentityUtil.getId(obj);
		} catch (IllegalAccessException e) {
			throw new FemError("Unable to read id field", e);
		}
		
		FemObjectConfig objConf = checkConfig(obj.getClass());
		
		FemDataFormat objDataFormat = dataFormat(objConf);
		
		FileWriter objWriter = objectWriter(obj.getClass(), key);
		
		File repoLocation = repoLocation(objConf);
		if (!(repoLocation.exists()&&repoLocation.isDirectory())) repoLocation.mkdirs();

		
		CachedObject<T> cached = (CachedObject<T>) cacheFetch(obj.getClass(), key);
	
		Object output = null;
		switch(objConf.dataStructure()) {
		case OCN:
			if (cached == null) {
				if (repoHasResource(obj.getClass(), key)) throw new FemDuplicateKeyException(obj.getClass(), key, repoLocation);
				
				output = new FemWrapper<T>(obj);
				cache(obj.getClass(), key, (FemWrapper<T>)output);
				dirty(obj.getClass(), key, (FemWrapper<T>)output);
			} else {
				if (!cached.originalOcn.equals(readOcn(obj.getClass(), key))) throw new FemMutatedObjectException(obj.getClass(), key, repoLocation);
				cached.wrappedObj.ocn++;
				cached.wrappedObj.obj = obj;
				output = cached.wrappedObj;
				dirty(obj.getClass(), key, cached.wrappedObj);
			}
			break;
		case OCN_AND_VER:
			if (cached == null) {
				if (repoHasResource(obj.getClass(), key)) throw new FemDuplicateKeyException(obj.getClass(), key, repoLocation);
				output = new FemWrapperAndVersion<T>((T)obj);
				cache(obj.getClass(), key, (FemWrapperAndVersion<T>)output);
				dirty(obj.getClass(), key, (FemWrapper<T>)output);
			} else {
				if (!cached.originalOcn.equals(readOcn(obj.getClass(), key))) throw new FemMutatedObjectException(obj.getClass(), key, repoLocation);
				cached.wrappedObj.ocn++;
				cached.wrappedObj.obj = obj;
				((FemWrapperAndVersion<T>)cached.wrappedObj).version.increment(Increment.POINT);
				output = cached.wrappedObj;
				dirty(obj.getClass(), key, cached.wrappedObj);
			}
			break;
		case RAW:
			if (cached == null) {
				if (repoHasResource(obj.getClass(), key)) throw new FemDuplicateKeyException(obj.getClass(), key, repoLocation);
				output = obj;
				FemWrapper<T> wrapper = new FemWrapper<T>(obj);
				cache(obj.getClass(), key, wrapper);
				dirty(obj.getClass(), key, wrapper);
			} else {
				cached.wrappedObj.obj = obj;
				output = obj;
				dirty(obj.getClass(), key, cached.wrappedObj);
			}
			break;
		default:
			throw new FemError("Unsupported data structure: "+objConf.dataStructure()+" saving '"+obj.getClass().getCanonicalName()+"("+key+")'");
		}
		
		
		lockInRepo(obj.getClass(), key);
		
		switch(objDataFormat) {
		case JSON:
			femFactory.gson().toJson(output, objWriter);
			try {
				objWriter.flush();
				objWriter.close();
			} catch (IOException e) {
				throw new FemError("Unable to write to '"+objWriter.toString()+"'", e);
			}
			break;
		case XML:
			break;
		}

		
		return obj;
	}

	public <T> T delete(T obj) throws FemObjectLockedException {
		
		Serializable key;
		try {
			key = IdentityUtil.getId(obj);
		} catch (IllegalAccessException e) {
			throw new FemError("Unable to read id field", e);
		}
		
		@SuppressWarnings("unchecked")
		CachedObject<T> cached = (CachedObject<T>) cacheFetch(obj.getClass(), key);
		if (cached == null) throw new FemError("You must fetch an object into the entity manager before you can delete it!");
		
		lockInRepo(obj.getClass(), key);
		
		markDeleted(obj.getClass(), key);
		clearCache(obj.getClass(), key);
		clearDirty(obj.getClass(), key);
		
		return obj;
	}
	public void commit() {
		
		for (Class<?> cls : dirty.keySet()) {
			Map<Serializable, FemWrapper<?>> dirtyClasses = dirty.get(cls);
			FemObjectConfig objConfig = checkConfig(cls);
			FemDataFormat dataFormat = dataFormat(objConfig);
			File repoLocation = repoLocation(objConfig);
			File workingLocation = workingLocation(objConfig);
			for (Serializable key : dirtyClasses.keySet()) {
				
				File workingFile = objResource(workingLocation, dataFormat, cls, key);
				File repoFile = objResource(repoLocation, dataFormat, cls, key);
				
				try {
					Files.move(workingFile.toPath(), repoFile.toPath(), ATOMIC_MOVE, REPLACE_EXISTING);
				} catch (IOException e) {
					try {
						Files.move(workingFile.toPath(), repoFile.toPath(), REPLACE_EXISTING);
					} catch (IOException e1) {
						throw new FemError("Unable to move '"+workingFile.getAbsolutePath()+"' to '"+repoLocation.getAbsolutePath()+"'", e);
					}
				}
				
			}
			
		}
		
		dirty.clear();
		
		for (Class<?> cls : deleted.keySet()) {
			Set<Serializable> deletedClasses = deleted.get(cls);
			FemObjectConfig objConfig = checkConfig(cls);
			FemDataFormat dataFormat = dataFormat(objConfig);
			File repoLocation = repoLocation(objConfig);
			for (Serializable key : deletedClasses) {
				
				objResource(repoLocation, dataFormat, cls, key).delete();
				
			}
		}
		
		deleted.clear();
		
		locks.clear();
		
		
		
	}

	
	public void rollback() {
		for (File f : workingDir.listFiles()) FileUtil.deleteCascade(f);
		deleted.clear();
		dirty.clear();
		cache.clear();
		
		for (Class<?> cls : locks.keySet()) {
			Map<Serializable, FileLock> classLocks = locks.get(cls);
			for (Serializable key : classLocks.keySet()) {
				FileLock lock = classLocks.get(key);
				try {
					lock.release();
				} catch (IOException e) {
					error("Unable to relase lock for '"+cls.getName()+"("+key+")", e);
				}
			}
		}
		locks.clear();
		
	}

	public void close() {
		FileUtil.deleteCascade(workingDir);
		femFactory.closed(this);
		
	}
	

}
