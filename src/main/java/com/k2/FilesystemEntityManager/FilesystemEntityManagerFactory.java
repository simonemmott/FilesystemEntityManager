package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.k2.Util.FileUtil;

public class FilesystemEntityManagerFactory {
	
	public static final String CONF = "fem.conf";
	public static final String JSON = ".json";
	public static final String XML = ".xml";
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static FilesystemEntityManagerFactory startup(File managerDir) throws FemException {
		
		if (!managerDir.exists()) throw new FemException("The filesystem entity manager directory '"+managerDir.getAbsolutePath()+"' does not exist!");
		if (!managerDir.isDirectory()) throw new FemException("The filesystem entity manager directory '"+managerDir.getAbsolutePath()+"' is not a directory!");
		if (!managerDir.canRead()) throw new FemException("Unable to read from the filesystem entity manager directory '"+managerDir.getAbsolutePath()+"'");
		if (!managerDir.canWrite()) throw new FemException("Unable to write to the filesystem entity manager directory '"+managerDir.getAbsolutePath()+"'");
		
		FilesystemEntityManagerFactory femFactory = new FilesystemEntityManagerFactory(managerDir);		
		
		return femFactory;
	}
	
	private FilesystemEntityManagerFactory(File managerDir) {
		this.managerDir = managerDir;
		this.connectionsDir = new File(managerDir.getAbsolutePath()+File.separatorChar+"connections");
		if (!connectionsDir.exists()) connectionsDir.mkdirs();
		
		File confFile = FileUtil.fetch(managerDir, CONF);
		if (confFile != null) readConfig(confFile);
		
	}
	
	private File managerDir;
	private Map<String, FilesystemEntityManager> connections = new HashMap<String, FilesystemEntityManager>();
	
	File repository(String alias) {
		return config().repo(alias);
	}
	
	private File connectionsDir;
	File connectionsDir() { return connectionsDir; }

	private FemConfig config;
	public FilesystemEntityManagerFactory configure(FemConfig config) {
		this.config = config;
		return this;
	}
	public FemConfig config() { 
		if (config == null) config = new FemConfig();
		return config; 
	}
	
	private LocalType localType = new LocalType();
	public LocalType localType() { return localType; }
	
	private Gson gsonConf = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	private Gson gson;
	public Gson gson() { 
		if (gson == null) gson = new GsonBuilder()
									.setPrettyPrinting()
									.excludeFieldsWithoutExposeAnnotation()
									.registerTypeAdapter(FemWrapper.class, new FemWrapperDeserializer(localType))
									.registerTypeAdapter(FemWrapperAndVersion.class, new FemWrapperAndVersionDeserializer(localType))
									.create();
		return gson;
	}
	public FilesystemEntityManagerFactory gson(Gson gson) {
		this.gson = gson;
		return this;
	}
	
	public FilesystemEntityManager connect() {
		FilesystemEntityManager conn = new FilesystemEntityManager(this);
		connections.put(conn.getId(), conn);
		return conn;
	}
	
	private void readConfig(File confFile) {
		try {
			config = gsonConf.fromJson(new FileReader(confFile), FemConfig.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			throw new FemError("Unable to read configuation file '"+confFile.getAbsolutePath()+"'", e);
		}
	}
	
	public void saveConfig() {
		File configFile = new File(managerDir.getAbsolutePath()+File.separatorChar+CONF);
		if (configFile.exists()) configFile.delete();
		try {
			Writer w = new FileWriter(configFile);
			gsonConf.toJson(config, w);
			w.flush();
			w.close();
		} catch (IOException e) {
			throw new FemError("Unable to write to the configuration file '"+configFile.getAbsolutePath()+"'", e);
		}
	}
	
	public void shutdown() {
		Set<FilesystemEntityManager> connsToClose = new HashSet<FilesystemEntityManager>();
		for (FilesystemEntityManager fem : connections.values()) connsToClose.add(fem);
		for (FilesystemEntityManager fem : connsToClose) { 
			fem.rollback(); 
			fem.close(); 
		}
		saveConfig();
	}

	void closed(FilesystemEntityManager fem) {
		connections.remove(fem.getId());
	}
	
	

}
