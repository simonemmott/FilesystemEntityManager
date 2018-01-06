package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Paths;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.k2.FilesystemEntityManager.FemTestClient.*;
import com.k2.Util.ClassUtil;
import com.k2.Util.FileUtil;
import com.k2.Util.StringUtil;


public class FemConfigTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	@Test
	public void configTest() throws FemException, IOException
    {
		
		FilesystemEntityManagerFactory femf = null;
		
		File testingLocation = new File("example"+File.separatorChar+"new");
		FileUtil.deleteCascade(testingLocation);
		
		logger.debug("Creating new repos");
		testingLocation.mkdir();
		
		FileUtil.buildTree(testingLocation,
				Paths.get("femf"),
				Paths.get("repos"),
				Paths.get("repos", "default"),
				Paths.get("repos", "custom")
				);
				
		logger.info("Creating new filesystem entity manager factory");
		femf = FilesystemEntityManagerFactory.startup(new File("example/new/femf"));

		logger.debug("Adding repositories to filesystem entity manager factory");
		femf.config()
			.setDefaultRepo(new File("example/new/repos/default"))
			.setRepo("custom", new File("example/new/repos/custom"));

		logger.debug("Configuring Foo.class");
		femf.config().objectConfig(Foo.class)
			.dataFormat(FemDataFormat.JSON)
			.dataStructure(FemDataStructure.OCN);
		
		logger.debug("Configuring Bar.class");
		femf.config().objectConfig(Bar.class)
			.dataFormat(FemDataFormat.XML)
			.dataStructure(FemDataStructure.OCN_AND_VER)
			.repository("custom");
	
		logger.debug("Configuring Too.class");
		femf.config().objectConfig(Too.class)
			.dataStructure(FemDataStructure.RAW)
			.resourcePath(Too.class.getSimpleName())
			.repository("custom");
	
		logger.info("Saving filesystem entity manager configuration");
		femf.saveConfig();
		
		File config = new File("example/new/femf/fem.conf");
		assertTrue(config.exists());
		assertEquals("81a2dff9a929a6e6ea911a7015756e2c", Files.hash(config, Hashing.md5()).toString());
		
        
		logger.info("Shutting down filesystem entity manager factory");
		femf.shutdown();
		       
    }

	@Test
	public void threadTest() throws InterruptedException, FemException
    {
		
		FilesystemEntityManagerFactory femf = null;
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        FemTestClient ftc = new FemTestClient("Connection1", waiter, femf.connect());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        assertEquals(State.WAITING, ftc.state());
	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	
	        
	        FemTestClient.FemTestResult result = ftc.getResult();
	        assertNotNull(result);
	        assertTrue(result instanceof Success);
	        assertEquals("Ending:", result.getMessage());
	        logger.info(result.getMessage());

            ftc.join();

	        logger.info("Done");

        
		} finally {
			if (femf != null) {
				logger.info("Shutting down filesystem entity manager factory");
				femf.shutdown();
			}
		}
        
    }
	
	
}
