package com.k2.FilesystemEntityManager;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.k2.Util.ClassUtil;
/**
 * This utility class provides static methods for handing Serializable keys
 * 
 * @author simon
 *
 */
public class KeyUtil {
	/**
	 * This method extracts from a given Serializable key the Key of the Root instance which contains the given key.
	 * 
	 * The root Id is identified from within a composite key by the RootId annotation
	 * 
	 * @param key	The key from which to extract the root key
	 * @return		The instance of the root id key extracted from the given key.
	 * 				If the given key is not a composite key then the given key is returned
	 */
	public static Serializable getRootKey(Serializable key) {
		Class<? extends Serializable> keyClass = key.getClass();
		if (keyClass.getName().startsWith("java.")) return key;
		for (Field f : ClassUtil.getDeclaredFields(keyClass)) {
			if (f.isAnnotationPresent(RootId.class) && f.getType() instanceof Serializable) {
				if (!f.isAccessible()) f.setAccessible(true);
				try {
					return (Serializable) f.get(key);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new FemError("Unable to extract root key from key '{}'", e, toString(key));
				}
			}
		}
		throw new FemError("Unable to identify RootId field from key");
	}
	/**
	 * This static method returns a human readable expression of the given key
	 * @param key	The key to express as a human readable string
	 * @return		The human readable string representing the given key
	 */
	public static String toString(Serializable key) {
		Class<? extends Serializable> keyClass = key.getClass();
		if (keyClass.getName().startsWith("java.")) return key.toString();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Field f : ClassUtil.getDeclaredFields(keyClass)) {
			sb.append(f.getName()+":");
			if (f.getType().getName().startsWith("java.")) {
				if (!f.isAccessible()) f.setAccessible(true);
				try {
					sb.append(f.get(key));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new FemError("Unable to extract values from key {}", e, key);
				}
			} else if (f.getType() instanceof Serializable){
				try {
					sb.append(toString((Serializable) f.get(key)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new FemError("Unable to extract values from key {}", e, key);
				}
			}
			sb.append(";");
		}
		sb.append("]");
		return sb.toString();
	}

}
