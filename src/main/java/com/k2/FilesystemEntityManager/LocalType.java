package com.k2.FilesystemEntityManager;

import java.lang.reflect.Type;
/**
 * This ThreadLocal variable holds the type of object being deserialized within a wrapper by Gson
 * 
 * @author simon
 *
 */
public class LocalType extends ThreadLocal<Type> {

}
