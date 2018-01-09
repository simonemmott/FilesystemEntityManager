package com.k2.FilesystemEntityManager;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

/**
 * This class represents the wrapper that is used to wrap all object instances held in the entity managers object cache and all classes with a 
 * data structure of OCN when their objects instance is written to the file system
 * 
 * @author simon
 *
 * @param <T>	The type of the object bing wrapped
 */
@XmlRootElement(name = "wrapper", namespace="com.k2.FilesystemEntityManager.example")
public class FemWrapper<T> extends FemOcn{
	/**
	 * The object being wrapped
	 */
	@Expose public T obj;
	/**
	 * Zero arg consructor requied for JAXB and Gson
	 */
	FemWrapper() {}
	/**
	 * Create an instance of the wrapper wrapping the given object with an OCN of 0
	 * @param obj
	 */
	FemWrapper(T obj) {
		super(0);
		this.obj = obj;
	}
}
