package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.proxy.KeyLinkProxyController;
import com.k2.FilesystemEntityManager.proxy.LinkProxy;
import com.k2.FilesystemEntityManager.proxy.ProxyUtil;
import com.k2.FilesystemEntityManager.testEntities.*;
import com.k2.FilesystemEntityManager.testEntities.proxies.Foo_DPx;
import com.k2.FilesystemEntityManager.util.KeyUtil;
import com.k2.Util.FileUtil;
import com.k2.Util.classes.ClassUtil;


public class LinkTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FilesystemEntityManagerFactory femf = null;
	File testingLocation = null;

	@Before
	public void setupData() throws FemException {
		
		String root = "example"+File.separatorChar+"linkTesting";

		testingLocation = new File(root);
		FileUtil.deleteCascade(testingLocation);
		
		logger.debug("Creating queryTesting repos");
		testingLocation.mkdir();
		
		FileUtil.buildTree(testingLocation,
				Paths.get("femf"),
				Paths.get("repos"),
				Paths.get("repos", "default")
				);
				
		logger.info("Creating new filesystem entity manager factory");
		femf = FilesystemEntityManagerFactory.startup(new File(root+File.separatorChar+"femf"));
		
		logger.debug("Adding repositories to filesystem entity manager factory");
		femf.config()
			.setDefaultRepo(new File(root+File.separatorChar+"repos"+File.separatorChar+"default"));

		logger.debug("Configuring Foo.class");
		femf.config()
			.configure(Foo.class, Bar.class, Too.class)
			.configure();
		
		EntityManager em = femf.createEntityManager();
		
		Foo foo1 = new Foo(1L)
				.setName("foo1");
		em.persist(foo1);
		
		Foo foo2 = new Foo(2L)
				.setName("foo2");
		em.persist(foo2);
		
		Foo foo3 = new Foo(3L)
				.setName("foo3");
		em.persist(foo3);
		
		Bar bar1 = new Bar("bar1", 1)
				.setName("Bar 1 (1)");
		em.persist(bar1);
		
		Bar bar2 = new Bar("bar2", 1)
				.setName("Bar 2 (1)");
		em.persist(bar2);
		
		Bar bar3 = new Bar("bar2", 2)
				.setName("Bar 2 (2)");
		em.persist(bar3);
		
		Too too1 = new Too(1L)
				.setFoo(foo1)
				.setBar(bar1)
				.setName("Too1 with foo1 and bar1 (1)");
		em.persist(too1);
		
		Too too2 = new Too(2L)
				.setFoo(foo2)
				.setBar(bar2)
				.setName("Too2 with foo2 and bar2 (1)");
		em.persist(too2);
		
		Too too3 = new Too(3L)
				.setFoo(foo3)
				.setBar(bar3)
				.setName("Too3 with foo3 and bar2 (2)");
		em.persist(too3);
		
		FilesystemEntityManager fem = em.unwrap(FilesystemEntityManager.class);
		fem.commit();
		
		em.close();
		


	}
	
	@After
	public void clearDownData() {
		
		logger.info("Shutting down filesystem entity manager factory");
		femf.shutdown();

	}
		

	
	@Test
	public void linkTest() throws IllegalArgumentException, IllegalAccessException
    {

		EntityManager em = femf.createEntityManager();

		Foo foo = em.find(Foo.class, KeyUtil.getKey(Foo.class, 1L));
		
		assertEquals(Long.valueOf(1L), foo.getId());
		assertEquals("foo1", foo.getName());
		
		Bar bar = em.find(Bar.class, KeyUtil.getKey(Bar.class, "bar2", 1));
		
		assertNotNull(bar);
		assertEquals("bar2", bar.getAlias());
		assertEquals(Integer.valueOf(1), bar.getSequence());
		assertEquals("Bar 2 (1)", bar.getName());
		
		Too too = em.find(Too.class, KeyUtil.getKey(Too.class, 3L));
		
		assertEquals(Long.valueOf(3), Long.valueOf(too.getId()));
		assertEquals("Too3 with foo3 and bar2 (2)", too.getName());
		
		assertTrue(LinkProxy.class.isAssignableFrom(too.getFoo().getClass()));
		
		assertNotNull(too.getFoo());
		
		assertEquals(Long.valueOf(3), too.getFoo().getId());
		assertEquals("foo3", too.getFoo().getName());
		
		assertTrue(LinkProxy.class.isAssignableFrom(too.getBar().getClass()));
		
		assertNotNull(too.getBar());
		
		assertEquals("bar2", too.getBar().getAlias());
		assertEquals(Integer.valueOf(2), too.getBar().getSequence());
		assertEquals("Bar 2 (2)", too.getBar().getName());
		
		
		em.close();
    }

	
}
