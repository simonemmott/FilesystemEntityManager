package com.k2.FilesystemEntityManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class FemConfig {
		
	@Expose private FemDataFormat dataFormat = FemDataFormat.JSON;
	public FemDataFormat dataFormat() { return dataFormat; }
	public FemConfig dataFormat(FemDataFormat dataFormat) {
		this.dataFormat = dataFormat;
		return this;
	}
	
	@Expose private Map<String, FemObjectConfig> objectConfigs = new HashMap<String, FemObjectConfig>();
	public Map<String, FemObjectConfig> objectConfigs() { return objectConfigs; }
	public FemConfig objectConfig(Class<?> cls, FemObjectConfig conf) {
		objectConfigs.put(cls.getCanonicalName(), conf);
		return this;
	}
	public FemObjectConfig objectConfig(Class<?> cls) {
		FemObjectConfig conf = objectConfigs.get(cls.getCanonicalName());
		if (conf == null) {
			conf = new FemObjectConfig(cls);
			objectConfigs.put(cls.getCanonicalName(), conf);
		}
		return conf;
	}
	
	@Expose private Map<String, String> repos = new HashMap<String, String>();
	
	public FemConfig setDefaultRepo(File dir) {
		repos.put("default", dir.getAbsolutePath());
		return this;
	}
	
	public FemConfig setRepo(String alias, File dir) {
		repos.put(alias, dir.getAbsolutePath());
		return this;
	}
	
	public File repo() {
		return repo("default");
	}

	public File repo(String alias) {
		String repoPath = repos.get(alias);
		if (repoPath == null) throw new FemError("The repo '"+alias+"' is undfined");
		File repo = new File(repoPath);
		if (!repo.exists()) throw new FemError("The repo '"+alias+"' at '"+repoPath+"' does not exist");
		if (!repo.isDirectory()) throw new FemError("The repo '"+alias+"' at '"+repoPath+"' is not a directory");
		if (!repo.canRead()) throw new FemError("Unable to read from the repo '"+alias+"' at '"+repoPath+"'");
		if (!repo.canWrite()) throw new FemError("Unable to write to the repo '"+alias+"' at '"+repoPath+"'");
		return repo;
	}

}
