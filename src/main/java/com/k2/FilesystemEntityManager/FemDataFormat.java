package com.k2.FilesystemEntityManager;
/**
 * This enumeration defines the possible storage data formats supported by file system entity managers
 * 
 * @author simon
 *
 */
public enum FemDataFormat {
	/**
	 * Data is stored in javascript object notation format using Gson
	 */
	JSON("json"),
	/**
	 * Date is stored in extensible markup language format using JAXB
	 */
	XML("xml");
	
	private String ext;
	FemDataFormat(String ext) {
		this.ext = ext;
	}
	
	public String getExtention() { return ext; }
}
