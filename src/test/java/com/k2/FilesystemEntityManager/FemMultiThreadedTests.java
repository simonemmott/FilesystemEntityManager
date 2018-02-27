package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.example.FemTestClient;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.FilesystemEntityManager.example.FemTestClient.*;
import com.k2.Util.StringUtil;


public class FemMultiThreadedTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	@Test
	public void SaveUpdateSaveAndCommitTest() throws InterruptedException, FemException
    {
		
		FilesystemEntityManagerFactory femf = null;
		FemTestClient ftc1 = null;
		FemTestClient ftc2 = null;
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
			
	        assertNotNull( femf );
	        
	        Waiter waiter = new Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection 1");
	        ftc1 = new FemTestClient("Connection1", waiter, femf.entityManager());
	        
	        logger.debug("Starting connection 1 thread");
	        ftc1.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        logger.debug("Creating filesystem entity manager connection 2");
	        ftc2 = new FemTestClient("Connection2", waiter, femf.entityManager());
	        
	        logger.debug("Starting connection 2 thread");
	        ftc2.start();
	        synchronized(waiter) { waiter.wait(); }
	        
	        Foo foo1;
	        while (true) {
	 			foo1 = new Foo()
						.setId(StringUtil.random(6))
						.setDescription("This is a foo!")
						.setSequence(1);
				
				logger.debug("Saving Foo with id '{}'", foo1.id);
		        synchronized(ftc1.waiter()) { ftc1.save(foo1).notify(); }
		        synchronized(waiter) { waiter.wait(); }
		        
		        if (ftc1.getResult() instanceof Success) {
		        		break;
		        } else {
		        		if (((Fault)ftc1.getResult()).getCause() instanceof FemDuplicateKeyException) {
		        			logger.debug("Duplicat key detected. Trying again");
		        		} else {
		        			throw new FemError(((Fault)ftc1.getResult()).getCause());
		        		}
		        }
	        }
	        
	        logger.debug("Committing connection 1");
	        synchronized(ftc1.waiter()) { ftc1.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc1.getResult() instanceof Success);
	        
			logger.debug("Fetch Foo with id '{}' on connection 2", foo1.id);
	        synchronized(ftc2.waiter()) { ftc2.fetch(Foo.class, foo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);
	        
	        Foo foo2 = (Foo)((Success)ftc2.getResult()).result;
	        logger.debug("Fetched Foo({}) from foo1.id: {} on connection 1", foo2.id, foo1.id); 
	        
	        foo2 = null;
	        
	        synchronized(ftc2.waiter()) { ftc2.rollback().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc1.getResult() instanceof Success);
	        
	        foo1.description = foo1.description + " updated!";

			logger.debug("Saving updated foo1 with id '{}' on connection 1", foo1.id);
	        synchronized(ftc1.waiter()) { ftc1.save(foo1).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc1.getResult() instanceof Success);
	        
			logger.debug("Fetch Foo with id '{}' on connection 2", foo1.id);
	        synchronized(ftc2.waiter()) { ftc2.fetch(Foo.class, foo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);
	        
	        foo2 = (Foo)((Success)ftc2.getResult()).result;
	        logger.debug("Fetched Foo({}) from foo1.id: {} on connection 2", foo2.id, foo1.id); 
	        
	        foo2.description = "This will fail";
	        
			logger.debug("Saving updated foo2 with id '{}' before committing connecion 1", foo2.id);
	        synchronized(ftc2.waiter()) { ftc2.save(foo2).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Fault);
	        
	        Fault fault = (Fault)ftc2.getResult();
	        logger.debug("Fault message: '{}'", fault.getMessage());
	        assertTrue(fault.getCause() instanceof FemObjectLockedException);
	        
	        logger.debug("Committing connection 1");
	        synchronized(ftc1.waiter()) { ftc1.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc1.getResult() instanceof Success);
	        
			logger.debug("Saving updated foo2 with id '{}' after committing connection 1", foo2.id);
	        synchronized(ftc2.waiter()) { ftc2.save(foo2).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Fault);
	        
	        fault = (Fault)ftc2.getResult();
	        logger.debug("Fault message: '{}'", fault.getMessage());
	        assertTrue(fault.getCause() instanceof FemMutatedObjectException);
	        
			logger.debug("Fetch Foo with id '{}' on connection 2", foo1.id);
	        synchronized(ftc2.waiter()) { ftc2.fetch(Foo.class, foo1.id).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);
	        
	        foo2 = (Foo)((Success)ftc2.getResult()).result;
	        logger.debug("Fetched Foo({}) from foo1.id: {} on connection 2", foo2.id, foo1.id); 
	        
	        foo2.description = "This will succeed";
	        
			logger.debug("Saving updated foo2 with id '{}' fetched and updated after committing connecion 1", foo2.id);
	        synchronized(ftc2.waiter()) { ftc2.save(foo2).notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);
	        	        
	        logger.debug("Committing connection 2");
	        synchronized(ftc2.waiter()) { ftc2.commit().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);

	        logger.debug("Ending connection 1 thread");
	        synchronized(ftc1.waiter()) {ftc1.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc1.getResult() instanceof Success);
	        
	        logger.debug("Ending connection 2 thread");
	        synchronized(ftc2.waiter()) {ftc2.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	        assertTrue(ftc2.getResult() instanceof Success);
	        
	        ftc1.interrupt();
	        ftc1.join();
	        ftc2.interrupt();
	        ftc2.join();

	        logger.info("Done");
        
		} finally {
			if (ftc1 != null) {
				if (ftc1.isAlive()) {
					ftc1.end();
					ftc1.interrupt();
				}
			}
			if (ftc2 != null) {
				if (ftc2.isAlive()) {
					ftc2.end();
					ftc2.interrupt();
				}
			}
			if (femf != null) {
				logger.info("Shutting down filesystem entity manager factory");
				femf.shutdown();
			}
		}
        
    }

	
}
