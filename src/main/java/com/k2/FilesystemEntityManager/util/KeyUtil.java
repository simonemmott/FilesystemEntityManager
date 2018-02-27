package com.k2.FilesystemEntityManager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.RootId;
import com.k2.Util.StringUtil;
import com.k2.Util.classes.ClassUtil;
/**
 * This utility class provides static methods for handing Serializable keys
 * 
 * @author simon
 *
 */
public class KeyUtil {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
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
			if (!f.isAccessible()) f.setAccessible(true);
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
	
	private static Map<Class<?>, Class<Serializable>> keyClasses = new HashMap<Class<?>, Class<Serializable>>();
	
	@SuppressWarnings("unchecked")
	public static Class<Serializable> getKeyClass(Class<?> entityClass) {
		Class<Serializable> keyClass = keyClasses.get(entityClass);
		if (keyClass != null) return keyClass;
		
		for (Field f : ClassUtil.getAllFields(entityClass)) {
			if (f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class)) {
				keyClasses.put(entityClass, (Class<Serializable>) f.getType());
				return (Class<Serializable>) f.getType();
			}
		}
		
		for (Method m : ClassUtil.getAllMethods(entityClass)) {
			if (m.isAnnotationPresent(Id.class) || m.isAnnotationPresent(EmbeddedId.class)) {
				keyClasses.put(entityClass, (Class<Serializable>) m.getReturnType());
				return (Class<Serializable>) m.getReturnType();
			}
		}
		
		throw new FemError("No primary key defined for class {}", entityClass.getName());
	}
	
	public static Serializable getKey(Class<?> entityClass, Object ... keyValues) {
		return constructKey(getKeyClass(entityClass), keyValues);
	}
	
	private static Map<Class<Serializable>, Constructor<Serializable>> keyConstructors = new HashMap<Class<Serializable>, Constructor<Serializable>>();

	private static Serializable constructKey(Class<Serializable> keyClass, Object ... keyValues) {
		
		if (keyClass.isPrimitive()/* || keyClass.getName().startsWith("java.")*/) {
			if (keyValues.length != 1)
				throw new FemError("Too many values {} to create primitive key", keyValues.length);
			if (keyClass == keyValues[0].getClass()) return (Serializable) keyValues[0]; 
			if (keyClass.equals(boolean.class) && keyValues[0] instanceof Boolean) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(byte.class) && keyValues[0] instanceof Byte) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(char.class) && keyValues[0] instanceof Character) {
				try {
					return URLEncoder.encode(String.valueOf((char)keyValues[0]), StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					throw new FemError("Unable to URL encode char {} ", e, keyValues[0]); 
				}
			} else if (keyClass.equals(short.class) && keyValues[0] instanceof Short) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(int.class) && keyValues[0] instanceof Integer) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(long.class) && keyValues[0] instanceof Long) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(float.class) && keyValues[0] instanceof Float) {
				return (Serializable) keyValues[0];
			} else if (keyClass.equals(double.class) && keyValues[0] instanceof Double) {
				return (Serializable) keyValues[0];
			}
			throw new FemError("Unknown primitive {}", keyClass.getName());			

		}
		if (keyClass.getName().startsWith("java.")) {
			if (keyValues.length != 1)
				throw new FemError("Too many values {} to create java key", keyValues.length);
			if (keyClass == keyValues[0].getClass()) return (Serializable) keyValues[0]; 
			if (!keyClass.isAssignableFrom(keyValues[0].getClass())) {
				throw new FemError("The given primitive key value {} of type {} is no compatible with the required key class {}",
						keyValues[0],
						keyValues[0].getClass().getName(),
						keyClass.getName());
			}
			return (Serializable) keyValues[0];
		}
		Constructor<Serializable> c = keyConstructors.get(keyClass);
		if (c == null) {
			logger.trace("Constructor for key class: {} not cached", keyClass.getName());
			StringBuilder keyValueClassNames = new StringBuilder();
			try {	
				Class<?>[] keyValueClasses = new Class<?>[keyValues.length];
				keyValueClassNames.append("(");
				for (int i=0; i<keyValues.length; i++) {
					keyValueClasses[i] = keyValues[i].getClass();
					keyValueClassNames.append(keyValues[i].getClass().getName());
					if (i<keyValues.length-1) keyValueClassNames.append(", ");
					
				}
				keyValueClassNames.append(")");

				c = keyClass.getConstructor(keyValueClasses);
				if (!c.isAccessible()) c.setAccessible(true);
				logger.trace("Caching constructor for key class: {}", keyClass.getName());
				keyConstructors.put(keyClass, c);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new FemError("Unable to access key constructor for class {} with parameters {}", e, keyClass.getCanonicalName(), keyValueClassNames.toString());
			}
		}	
		try {
			return c.newInstance(keyValues);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (int i=0; i<c.getParameterCount(); i++) {
				sb.append(c.getParameters()[i].getClass());
				if (i < c.getParameterCount() -1) 
					sb.append(", ");
			}
			sb.append(")");
			throw new FemError("Unable to create new instance of key class {} using constructor {} with values {}", e, keyClass.getName(), c.getName()+sb.toString(), Arrays.toString(keyValues));
		}

	}
	public static Serializable keyFor(Class<?> entityClass, Object sourceObj, Field ... keyFields) {
		
		if (keyFields.length == 0) throw new FemError("Unable to create a key for class {} without any key fields", entityClass.getName());
		if (sourceObj == null) throw new FemError("Unable to create key for class {} without a source object", entityClass.getName());
		for (int i=0; i<keyFields.length; i++) {
			if (!(keyFields[i].getType() instanceof Serializable)) 
				throw new FemError("The supplied key field {}.{} does not provide a seerialiable value", keyFields[i].getDeclaringClass().getName(), keyFields[i].getName());
			if (!keyFields[i].getDeclaringClass().isAssignableFrom(sourceObj.getClass()))
				throw new FemError("The supplied key field {}.{} is not a field of the source object class {}", keyFields[i].getDeclaringClass().getName(), keyFields[i].getName(), sourceObj.getClass().getName());
		}
		
		Class<Serializable> keyClass = getKeyClass(entityClass);
		
		logger.trace("Identified key class as {}", keyClass.getName());
		
		if (keyFields.length == 1) {
			
			logger.trace("Single field key identified");
			if (keyClass.equals(keyFields[0].getType())) {
				try {
					if (!keyFields[0].isAccessible()) keyFields[0].setAccessible(true);
					Serializable key = (Serializable) keyFields[0].get(sourceObj);
					logger.trace("Key value is: {}", key);
					return key;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new FemError("Unable to access field {}.{} to extract key value for {}", e, sourceObj.getClass().getName(), keyFields[0].getName(), entityClass.getName());
				}
			}
			throw new FemError("The defined key {} class of {} does not match the type of the supplied single key field {}:{}", keyClass.getName(), entityClass.getName(), keyFields[0].getName(), keyFields[0].getType().getName());
		}
		
		Object[] keyValues = new Object[keyFields.length];
		for (int i=0; i<keyFields.length; i++) {
			try {
				keyValues[i] = keyFields[i].get(sourceObj);
				logger.trace("Key value {} is: {}", i, keyValues[i]);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new FemError("Unable to access key field");
			}
		}
		
		return constructKey(keyClass, keyValues);
		
	}
	
	private static String serializeFieldValue(Object val) {
		Class<?> cls = val.getClass();
		if (cls.isPrimitive()) {
			if (cls == boolean.class) {
				return ((boolean)val)?"1":"0";
			} else if (cls == byte.class) {
				return Byte.toString((byte)val);
			} else if (cls == char.class) {
				try {
					return URLEncoder.encode(String.valueOf((char)val), StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					throw new FemError("Unable to URL encode char {} ", e, val); 
				}
			} else if (cls == short.class) {
				return Short.toString((short)val);
			} else if (cls == int.class) {
				return Integer.toString((int)val);
			} else if (cls == long.class) {
				return Long.toString((long)val);
			} else if (cls == float.class) {
				return Float.toString((float)val);
			} else if (cls == double.class) {
				return Double.toString((double)val);
			}
			throw new FemError("Unknown primitive {}", cls.getName());			
		}
		if (val instanceof String) {
			String s = (String)val;
			try {
				return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				throw new FemError("Unable to URL encode string {}", s);
			}
		} else if (val instanceof Date) {
			Date d = (Date)val;
			return String.valueOf(d.getTime());
		} else if (val instanceof Boolean) {
			Boolean b = (Boolean)val;
			return (b)?"1":"0";
		} else {
			return val.toString();
		}		
	}
	
	private static Serializable deserializeFieldValue(Class<? extends Serializable> cls, String val) {
		try {
			if (cls.isPrimitive()) {
				if (cls == boolean.class) {
					return val.equals("1");
				} else if (cls == byte.class) {
					return Byte.valueOf(val).byteValue();
				} else if (cls == char.class) {
					String cString = URLDecoder.decode(val, StandardCharsets.UTF_8.toString());
					return cString.toCharArray()[0];
				} else if (cls == short.class) {
					return Short.valueOf(val).shortValue();
				} else if (cls == int.class) {
					return Integer.valueOf(val).intValue();
				} else if (cls == long.class) {
					return Long.valueOf(val).longValue();
				} else if (cls == float.class) {
					return Float.valueOf(val).floatValue();
				} else if (cls == double.class) {
					return Double.valueOf(val).doubleValue();
				}
				throw new FemError("Unknown primitive {}", cls.getName());
			} else if (String.class.isAssignableFrom(cls)) {
				return URLDecoder.decode(val, StandardCharsets.UTF_8.toString());
			} else if (Date.class.isAssignableFrom(cls)) {
				Constructor<? extends Serializable> cFromLong = cls.getConstructor(Long.class);
				if (cFromLong != null) {
					return cFromLong.newInstance(Long.valueOf(val));
				}
				Constructor<? extends Serializable> cFromDate = cls.getConstructor(Date.class);
				if (cFromDate != null) {
					return cFromDate.newInstance(new Date(Long.valueOf(val)));
				}
				throw new FemError("Unable to convert {} to an instance of {}", val, cls.getName());
			} else if (Boolean.class.isAssignableFrom(cls)) {
				return val.equals("1");
			} else {
				Constructor<? extends Serializable> cFromString = cls.getConstructor(String.class);
				return cFromString.newInstance(val);
			}	
		} catch (Exception e) {
			throw new FemError("Unable to deserialize field encoded as {} into class {}", e, val, cls.getName());
		}
	}
	
	public static String serialize(Serializable key) {
		
		if (key == null)
			throw new FemError("Unable to serialise a null key");
		
		StringBuilder sb = new StringBuilder();
		Field[] keyFields = ClassUtil.getDeclaredFields(key.getClass());
		int i = 0;
		try {
			if (key.getClass().getName().startsWith("java.") || !key.getClass().getName().contains(".")) {
				sb.append(serializeFieldValue(key));
			} else {
				for (i=0; i<keyFields.length; i++) {
					Object val = keyFields[i].get(key);
					if (val == null)
						throw new FemError("The value of the key field {}.{} is null", key.getClass().getName(), keyFields[i].getName());
					sb.append(serializeFieldValue(val));
					if (i<keyFields.length -1) {
						sb.append(":");
					}
					
				}
			}
			
			return sb.toString();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new FemError("Unable to read key field {}.{}", e, key.getClass().getName(), keyFields[i].getName());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <K extends Serializable> K deserialise(Class<K> keyClass, String ser) {
		
		if (keyClass.getName().startsWith("java.") || !keyClass.getName().contains(".")) {
			return (K) deserializeFieldValue(keyClass, ser);
		} else {
			String[] fieldSers = ser.split(":");
			Field[] keyFields = ClassUtil.getDeclaredFields(keyClass);
			if (fieldSers.length != keyFields.length)
				throw new FemError("The number of serializd fields {} does not match the number of key fields {} in the class {}",
						fieldSers.length,
						keyFields.length,
						keyClass.getName());
			try {
			K key = keyClass.newInstance();
			for (int i=0; i<fieldSers.length; i++) {
				Serializable keyFieldValue = deserializeFieldValue((Class<? extends Serializable>) keyFields[i].getType(), fieldSers[i]);
				keyFields[i].set(key, keyFieldValue);
			}
			return key;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new FemError("Unable to create instance of key {} for serialized string {}", e, keyClass.getName(), ser);
			}
		}
	}
	
	public static Serializable toKey(Class<?> entityClass, String ser) {
		return deserialise(getKeyClass(entityClass), ser);
	}
	public static Field[] getKeyFields(Class<?> entityClass) {
		List<Field> keyFields = new ArrayList<Field>();
		for (Field f : ClassUtil.getAllFields(entityClass)) {
			if (f.isAnnotationPresent(Id.class)) {
				keyFields.add(f);
				return keyFields.toArray(new Field[keyFields.size()]);
			}
			if (f.isAnnotationPresent(EmbeddedId.class)) {
				Class<?> keyClass = getKeyClass(entityClass);
				for (Field kf : ClassUtil.getDeclaredFields(keyClass)) {
					if (kf.isAnnotationPresent(Column.class)) {
						Column c = kf.getAnnotation(Column.class);
						String columnName = (StringUtil.isSet(c.name()))?c.name():kf.getName();
						keyFields.add(EntityUtil.getColumnByName(entityClass, columnName));
					}
				}
				return keyFields.toArray(new Field[keyFields.size()]);
			}
		}
		
		throw new FemError("The entity class {} does not define a primary key", entityClass.getName());
	}

}
