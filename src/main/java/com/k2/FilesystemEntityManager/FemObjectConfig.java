package com.k2.FilesystemEntityManager;

import java.io.File;

import com.google.gson.annotations.Expose;

public class FemObjectConfig {
	
	public FemObjectConfig(Class<?> cls) {
		resourcePath = File.separatorChar+cls.getSimpleName();
	}

	@Expose private FemDataFormat dataFormat = FemDataFormat.JSON;
	public FemObjectConfig dataFormat(FemDataFormat dataFormat) {
		this.dataFormat = dataFormat;
		return this;
	}
	public FemDataFormat dataformat() {
		return dataFormat;
	}
	
	@Expose private String resourcePath = "";
	public FemObjectConfig resourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
		return this;
	}
	public String resourcePath() {
		return resourcePath;
	}
	
	@Expose private FemDataStructure dataStructure = FemDataStructure.OCN;
	public FemObjectConfig dataStructure(FemDataStructure dataStructure) {
		this.dataStructure = dataStructure;
		return this;
	}
	public FemDataStructure dataStructure() {
		return dataStructure;
	}
	
	@Expose private String repo = "default";
	public String repository() { return repo; }
	public FemObjectConfig repository(String alias) {
		this.repo = alias;
		return this;
	}
	
}
