package com.k2.FilesystemEntityManager.example;

import java.io.File;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemException;
import com.k2.FilesystemEntityManager.FilesystemEntityManagerFactory;



public class ThreadedExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public void threadTest() throws InterruptedException, FemException
    {
		FilesystemEntityManagerFactory femf = null;
		try {
			logger.info("Starting filesystem entity manager factory");
			femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
				        
	        FemTestClient.Waiter waiter = new FemTestClient.Waiter();
	        
	        logger.debug("Creating filesystem entity manager connection");
	        FemTestClient ftc = new FemTestClient("Conection1", waiter, femf.entityManager());

	        logger.debug("Starting connection thread");
	        ftc.start();
	        synchronized(waiter) { waiter.wait(); }
	        	        
	        logger.debug("Ending connection thread");
	        synchronized(ftc.waiter()) {ftc.end().notify(); }
	        synchronized(waiter) { waiter.wait(); }
	
	        
	        FemTestClient.FemTestResult result = ftc.getResult();
	        logger.info(result.getMessage());

	        logger.info("Done");

        
            ftc.join();
        
        
		} finally {
			if (femf != null) {
				logger.info("Shutting down filesystem entity manager factory");
				femf.shutdown();
			}
		}
        
    }

	public static void main(String[] args) {
		
		ThreadedExample runner = new ThreadedExample();
		
		try {
//			((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.INFO);
			runner.threadTest();
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
