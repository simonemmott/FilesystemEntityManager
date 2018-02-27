package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.example.FemTestClient;
import com.k2.FilesystemEntityManager.example.XmlFoo;
import com.k2.FilesystemEntityManager.example.FemTestClient.*;
import com.k2.Util.FileUtil;
import com.k2.Util.StringUtil;


public class FemMultipleActionXmlTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	@Test
	public void SaveUpdateSaveAndCommitTest() throws InterruptedException, FemException
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
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        XmlFoo XmlFoo1;
	        while (true) {
	 			XmlFoo1 = new XmlFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a XmlFoo!")
						.setSequence(1);
				
				logger.debug("Saving XmlFoo with id '{}'", XmlFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(XmlFoo1).notify(); }
		        synchronized(waiter) { waiter.wait(); }
		        
		        if (ftc.getResult() instanceof Success) {
		        		break;
		        } else {
		        		if (((Fault)ftc.getResult()).getCause() instanceof FemDuplicateKeyException) {
		        			logger.debug("Duplicat key detected. Trying again");
		        		} else {
		        			throw new FemError(((Fault)ftc.getResult()).getCause());
		        		}
		        }
	        }
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(XmlFoo1));
	        assertEquals(ftc.entityManager().getOcn(XmlFoo1), ftc.entityManager().getOriginalOcn(XmlFoo1));

	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        
	        XmlFoo1.description = XmlFoo1.description + " updated!";

			logger.debug("Saving updated XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.save(XmlFoo1).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        	        
	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());

	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        ftc.interrupt();
            ftc.join();

	        assertFalse(connection.exists());

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

	@Test
	public void SaveFetchDeleteAndCommitTest() throws InterruptedException, FemException
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
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        XmlFoo XmlFoo1;
	        while (true) {
	 			XmlFoo1 = new XmlFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a XmlFoo!")
						.setSequence(1);
				
				logger.debug("Saving XmlFoo with id '{}'", XmlFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(XmlFoo1).notify(); }
		        synchronized(waiter) { waiter.wait(); }
		        
		        if (ftc.getResult() instanceof Success) {
		        		break;
		        } else {
		        		if (((Fault)ftc.getResult()).getCause() instanceof FemDuplicateKeyException) {
		        			logger.debug("Duplicat key detected. Trying again");
		        		} else {
		        			throw new FemError(((Fault)ftc.getResult()).getCause());
		        		}
		        }
	        }
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(XmlFoo1));
	        assertEquals(ftc.entityManager().getOcn(XmlFoo1), ftc.entityManager().getOriginalOcn(XmlFoo1));

	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        	        
	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());

	        logger.debug("Fetching XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.fetch(XmlFoo.class, XmlFoo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        

	        FemTestResult result = ftc.getResult();
	        logger.debug(result.getMessage());

	        assertTrue(result instanceof Success);
	        Success success = (Success)result;
	        	        
	        assertNotNull(success.result);
	        
	        logger.debug("Fetch result class: {}", success.result.getClass());
	        
	        assertTrue(success.result instanceof XmlFoo);
	        XmlFoo XmlFoo = (XmlFoo)success.result;

	        
	        logger.debug("Deleting XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.delete(XmlFoo).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));

	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertFalse(repo.exists());

	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        ftc.interrupt();
            ftc.join();

	        assertFalse(connection.exists());

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

	@Test
	public void SaveFetchDeleteAndRollbackTest() throws InterruptedException, FemException
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
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        XmlFoo XmlFoo1;
	        while (true) {
	 			XmlFoo1 = new XmlFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a XmlFoo!")
						.setSequence(1);
				
				logger.debug("Saving XmlFoo with id '{}'", XmlFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(XmlFoo1).notify(); }
		        synchronized(waiter) { waiter.wait(); }
		        
		        if (ftc.getResult() instanceof Success) {
		        		break;
		        } else {
		        		if (((Fault)ftc.getResult()).getCause() instanceof FemDuplicateKeyException) {
		        			logger.debug("Duplicat key detected. Trying again");
		        		} else {
		        			throw new FemError(((Fault)ftc.getResult()).getCause());
		        		}
		        }
	        }
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(XmlFoo1));
	        assertEquals(ftc.entityManager().getOcn(XmlFoo1), ftc.entityManager().getOriginalOcn(XmlFoo1));

	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        	        
	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());

	        logger.debug("Fetching XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.fetch(XmlFoo.class, XmlFoo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        

	        FemTestResult result = ftc.getResult();
	        logger.debug(result.getMessage());

	        assertTrue(result instanceof Success);
	        Success success = (Success)result;
	        	        
	        assertNotNull(success.result);
	        
	        logger.debug("Fetch result class: {}", success.result.getClass());
	        
	        assertTrue(success.result instanceof XmlFoo);
	        XmlFoo XmlFoo = (XmlFoo)success.result;

	        
	        logger.debug("Deleting XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.delete(XmlFoo).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));

	        logger.debug("Rolling back connection");
	        synchronized(ftc.waiter()) { ftc.rollback().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());

	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        ftc.interrupt();
            ftc.join();

	        assertFalse(connection.exists());

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


	@Test
	public void SaveFetchUpdateAndSaveTest() throws InterruptedException, FemException
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
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        XmlFoo XmlFoo1;
	        while (true) {
	 			XmlFoo1 = new XmlFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a XmlFoo!")
						.setSequence(1);
				
				logger.debug("Saving XmlFoo with id '{}'", XmlFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(XmlFoo1).notify(); }
		        synchronized(waiter) { waiter.wait(); }
		        
		        if (ftc.getResult() instanceof Success) {
		        		break;
		        } else {
		        		if (((Fault)ftc.getResult()).getCause() instanceof FemDuplicateKeyException) {
		        			logger.debug("Duplicat key detected. Trying again");
		        		} else {
		        			throw new FemError(((Fault)ftc.getResult()).getCause());
		        		}
		        }
	        }
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/xml/XmlFoo/"+XmlFoo1.getId()+".xml");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(XmlFoo1));
	        assertEquals(ftc.entityManager().getOcn(XmlFoo1), ftc.entityManager().getOriginalOcn(XmlFoo1));

	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        	        
	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertFalse(FileUtil.isLocked(repo));
	        
	        logger.debug("Fetching XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.fetch(XmlFoo.class, XmlFoo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        

	        FemTestResult result = ftc.getResult();
	        logger.debug(result.getMessage());

	        assertTrue(result instanceof Success);
	        Success success = (Success)result;
	        	        
	        assertNotNull(success.result);
	        
	        logger.debug("Fetch result class: {}", success.result.getClass());
	        
	        assertTrue(success.result instanceof XmlFoo);
	        XmlFoo XmlFoo = (XmlFoo)success.result;
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo));
	        assertEquals(ftc.entityManager().getOcn(XmlFoo), ftc.entityManager().getOriginalOcn(XmlFoo));
	        
	        XmlFoo.description = XmlFoo.description+" updated!";
	        
	        logger.debug("Saving XmlFoo with id '{}'", XmlFoo1.id);
	        synchronized(ftc.waiter()) { ftc.save(XmlFoo).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        assertNotNull(ftc.entityManager().getOcn(XmlFoo));
	        assertNotNull(ftc.entityManager().getOriginalOcn(XmlFoo));
	        Integer checkOcn = ftc.entityManager().getOriginalOcn(XmlFoo) + 1;
	        assertEquals(checkOcn, ftc.entityManager().getOcn(XmlFoo));
	        
	        logger.debug("Committing connection");
	        synchronized(ftc.waiter()) { ftc.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);

	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertFalse(FileUtil.isLocked(repo));
	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	
	        
	        result = ftc.getResult();
	        assertNotNull(result);
	        assertTrue(result instanceof FemTestClient.Success);
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
