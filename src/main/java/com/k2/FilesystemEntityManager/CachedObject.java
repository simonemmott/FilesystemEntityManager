package com.k2.FilesystemEntityManager;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

/**
 * This class represents a object in the entity managers cache of attached objects
 * 
 * @author simon
 *
 * @param <T> The type of the object in the cache
 */
public class CachedObject<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * The object change number (OCN) associated with the object when it was read from the file system.
	 * 
	 * The value is compared to the OCN of the object on the file system when the cached object is saved.
	 * If the OCN has changed then the save is disallowed since the the object has changed since it was read
	 * by the entity manager holding this cache.
	 */
	Integer originalOcn;
	/**
	 * The object in its wrapper as read from the file system. If the object is saved in its RAW state i.e. without
	 * a wrapper and OCN then the object read from the file system is wrapped in a new wrapper before being added to 
	 * the cache.
	 */
	FemWrapper<T> wrappedObj;
	
	/**
	 * Create an instance of CachedObject for the given unwrapped object
	 * 
	 * This method is called when saving a new object to add it to the cache.
	 * The original OCN is set to zero to match the OCN in the wrapper.
	 * 
	 * @param obj	The object add to the cache
	 */
	CachedObject(T obj) {
		originalOcn = 0;
		// Create a new wrapper to wrap the unwrapped object
		wrappedObj = new FemWrapper<T>(obj);
		if (obj != null)logger.trace("Caching new {}({}) with OCN:{}", obj.getClass(), IdentityUtil.getId(obj), 0);
	}

	/**
	 * Create an instance of CachedObject for the given wrapped object
	 * 
	 * This method is called when an object is added to the cache as a result of reading an object from the file system.
	 * The original OCN is set the the OCN from the wrapped object read from the file system.
	 * @param wrappedObj		The wrapped object to add to the cache.
	 */
	CachedObject(FemWrapper<T> wrappedObj) {
		originalOcn = wrappedObj.ocn;
		this.wrappedObj = wrappedObj;
		if (wrappedObj.obj != null) logger.trace("Caching {}({}) from file system with OCN:{}", wrappedObj.obj.getClass(), IdentityUtil.getId(wrappedObj.obj), 0);
	}

}
