package com.k2.FilesystemEntityManager;

import com.google.gson.annotations.Expose;
import com.k2.Util.ClassUtil;

public class FemObjectConfig {
	
	public FemObjectConfig(Class<?> cls) {
		resourcePath = ClassUtil.packageNameToPath(cls.getCanonicalName());
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
		if (resourcePath == null) return "";
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
