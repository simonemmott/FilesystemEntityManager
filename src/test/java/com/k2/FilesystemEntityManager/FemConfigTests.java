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
import com.k2.FilesystemEntityManager.example.FemTestClient;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.FilesystemEntityManager.example.FemTestClient.*;
import com.k2.Util.FileUtil;


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
			.dataStructure(FemDataStructure.RAW)
			.repository("custom")
			.configure();

		/*
		logger.debug("Configuring Bar.class");
		femf.config().objectConfig(Bar.class)
			.dataFormat(FemDataFormat.XML)
			.repository("custom")
			.configure();
	
		logger.debug("Configuring Too.class");
		femf.config().objectConfig(Too.class)
			.dataStructure(FemDataStructure.RAW)
			.resourcePath(Too.class.getSimpleName())
			.repository("custom")
			.configure();
		*/
	
		logger.info("Saving filesystem entity manager configuration");
		femf.saveConfig();
		
		File config = new File("example/new/femf/fem.conf");
		assertTrue(config.exists());
		assertEquals("1340d37800aaf6c02a2648a2cc34f431", Files.hash(config, Hashing.md5()).toString());
		
        
		logger.info("Shutting down filesystem entity manager factory");
		femf.shutdown();
		       
    }

	@Test
	public void threadTest() throws InterruptedException, FemException
    {
		
		FilesystemEntityManagerFactory femf = null;
		FemTestClient ftc = null;
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        ftc = new FemTestClient("Connection1", waiter, femf.entityManager());

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

	        ftc.interrupt();
            ftc.join();

	        logger.info("Done");

        
		} finally {
			if (ftc != null) {
				if (ftc.isAlive()) {
					ftc.end();
					ftc.interrupt();
				}
			}
			if (femf != null) {
				logger.info("Shutting down filesystem entity manager factory");
				femf.shutdown();
			}
		}
        
    }
	
	
}
