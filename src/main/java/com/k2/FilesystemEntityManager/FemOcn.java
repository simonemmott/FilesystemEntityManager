package com.k2.FilesystemEntityManager;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;
/**
 * This class is the super class of the wrapper used to persists object instances that have a storage data structure of OCN.
 * 
 * This class is used to read the OCN from the file system without having to parse the persisted object instance data
 * 
 * All classes whether persisted as a RAW or as an OCN are held in the file system entity managers cache in a FemWrapper
 * instance
 * 
 * @author simon
 *
 */
@XmlRootElement(name = "wrapper", namespace="com.k2.FilesystemEntityManager.example")
public class FemOcn {
	/**
	 * The Object Change Number (OCN) of the persisted object instance data
	 * 
	 * The OCN starts at zero and is incremented each time an object saved is updated and if the OCN of the object
	 * when it was read doesn't match the OCN of the object in the repository a mutated object exception is thrown
	 */
	@Expose public Integer ocn;
	/**
	 * Classes with a data structure of RAW do not maintain an OCN instead the MD5 hash of the file persisting 
	 * the objects instance data is used to identify a mutating object
	 */
	@XmlTransient
	public String hash;
	/**
	 * Zero arg constructor required by JAXB and Gson
	 */
	FemOcn() {}
	/**
	 * Create a instance of the class setting the OCN
	 * @param ocn	The OCN of the new wrapper
	 */
	FemOcn(Integer ocn) {
		this.ocn = ocn;
	}
}
