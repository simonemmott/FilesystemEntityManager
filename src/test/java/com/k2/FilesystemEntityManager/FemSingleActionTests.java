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


public class FemSingleActionTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	
	@Test
	public void FetchTest() throws InterruptedException, FemException
    {
		
		FilesystemEntityManagerFactory femf = null;
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        FemTestClient ftc = new FemTestClient("Connection1", waiter, femf.connect());
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        synchronized(ftc.waiter()) { ftc.fetch(Foo.class, "thisFoo").notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
	        assertTrue(((Success)ftc.getResult()).result instanceof Foo);
	        Foo foo1 = (Foo)((Success)ftc.getResult()).result;
	        
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/default/Foo/"+foo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/default/Foo/"+foo1.getId()+".json");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertFalse(working.exists());
	        assertTrue(repo.exists());
	        assertFalse(FileUtil.isLocked(repo));
	        
	        assertEquals("thisFoo", foo1.id);
	        assertEquals(new Integer(1), foo1.sequence);
	        assertEquals("This has been updated", foo1.description);
	        assertEquals(2, foo1.bars.size());
	        	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc.getResult() instanceof Success);
	        
            ftc.join();

	        assertFalse(connection.exists());

	        logger.info("Done");

        
		} finally {
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
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        FemTestClient ftc = new FemTestClient("Connection1", waiter, femf.connect());
	        
	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        Foo foo1;
	        while (true) {
	 			foo1 = new Foo()
						.setId(StringUtil.random(6))
						.setDescription("This is a foo!")
						.setSequence(1);
				
				logger.debug("Saving Foo with id '{}'", foo1.id);
		        synchronized(ftc.waiter()) { ftc.save(foo1).notify(); }
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
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/default/Foo/"+foo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/default/Foo/"+foo1.getId()+".json");
	        logger.trace("Repo file: {}", repo.getPath());
	        
	        assertNotNull(ftc.entityManager().getOcn(foo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(foo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(foo1));
	        assertEquals(ftc.entityManager().getOcn(foo1), ftc.entityManager().getOriginalOcn(foo1));

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
	        
            ftc.join();

	        assertFalse(connection.exists());

	        logger.info("Done");

        
		} finally {
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
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        FemTestClient ftc = new FemTestClient("Connection1", waiter, femf.connect());

	        File connection = new File("example/femf/connections/"+ftc.entityManager().getId());
	        assertTrue(connection.exists());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        Foo foo1;
	        while (true) {
	 			foo1 = new Foo()
						.setId(StringUtil.random(6))
						.setDescription("This is a foo!")
						.setSequence(1);
				
				logger.debug("Saving Foo with id '{}'", foo1.id);
		        synchronized(ftc.waiter()) { ftc.save(foo1).notify(); }
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
	        assertNotNull(ftc.entityManager().getOcn(foo1));
	        assertNotNull(ftc.entityManager().getOriginalOcn(foo1));
	        assertEquals(new Integer(0), ftc.entityManager().getOcn(foo1));
	        assertEquals(ftc.entityManager().getOcn(foo1), ftc.entityManager().getOriginalOcn(foo1));
	        
	        File working = new File("example/femf/connections/"+ftc.entityManager().getId()+"/default/Foo/"+foo1.getId()+".json");
	        logger.trace("Working file: {}", working.getPath());
	        File repo = new File("example/repos/default/Foo/"+foo1.getId()+".json");
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
	        
            ftc.join();

	        assertFalse(connection.exists());

	        logger.info("Done");

        
		} finally {
			if (femf != null) {
				logger.info("Shutting down filesystem entity manager factory");
				femf.shutdown();
			}
		}
        
    }

	
}
