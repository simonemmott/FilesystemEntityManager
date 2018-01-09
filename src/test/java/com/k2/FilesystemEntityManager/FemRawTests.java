package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemTestClient.*;
import com.k2.Util.FileUtil;
import com.k2.Util.StringUtil;


public class FemRawTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	
	@Test
	public void FetchTest() throws InterruptedException, FemException
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
	        
	        synchronized(ftc.waiter()) { ftc.fetch(RawFoo.class, "thisRawFoo").notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        assertTrue(((Success)ftc.getResult()).result instanceof RawFoo);
	        RawFoo rawFoo1 = (RawFoo)((Success)ftc.getResult()).result;
	        
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertFalse(FileUtil.isLocked(repo));
	        
	        assertEquals("thisRawFoo", rawFoo1.id);
	        assertEquals(new Integer(1), rawFoo1.sequence);
	        assertEquals("This has been updated", rawFoo1.description);
	        assertEquals(2, rawFoo1.bars.size());
	        	        
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
	public void SaveAndCommitTest() throws InterruptedException, FemException
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
	        
	        RawFoo rawFoo1;
	        while (true) {
	 			rawFoo1 = new RawFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a RawFoo!")
						.setSequence(1);
				
				logger.debug("Saving RawFoo with id '{}'", rawFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(rawFoo1).notify(); }
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
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(rawFoo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(rawFoo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(rawFoo1));
	        assertEquals(ftc.entityManager().getOcn(rawFoo1), ftc.entityManager().getOriginalOcn(rawFoo1));

	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        	        
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
	public void SaveAndRollbackTest() throws InterruptedException, FemException
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
	        
	        RawFoo rawFoo1;
	        while (true) {
	 			rawFoo1 = new RawFoo()
						.setId(StringUtil.random(6))
						.setDescription("This is a RawFoo!")
						.setSequence(1);
				
				logger.debug("Saving RawFoo with id '{}'", rawFoo1.id);
		        synchronized(ftc.waiter()) { ftc.save(rawFoo1).notify(); }
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
	        
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/raw/RawFoo/"+rawFoo1.getId()+".json");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertTrue(working.exists());
	        assertTrue(repo.exists());
	        assertTrue(FileUtil.isLocked(repo));
	        	        
	        logger.debug("Rolling back connection");
	        synchronized(ftc.waiter()) { ftc.rollback().notify(); }
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

	
}
