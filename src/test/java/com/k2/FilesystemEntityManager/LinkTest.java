package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.testEntities.*;
import com.k2.FilesystemEntityManager.util.KeyUtil;
import com.k2.Proxy.AProxy;
import com.k2.Util.FileUtil;


public class LinkTest {
	
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
		
		femf.manage(Foo.class, Bar.class, Too.class);
		
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
	public void staticMetamodelTest() {
		assertNotNull(Foo_.id);
		assertNotNull(Foo_.name);
		assertNotNull(Bar_.alias);
		assertNotNull(Bar_.name);
		assertNotNull(Bar_.sequence);
		assertNotNull(Too_.bar);
		assertNotNull(Too_.barAlias);
		assertNotNull(Too_.barSequence);
		assertNotNull(Too_.foo);
		assertNotNull(Too_.fooId);
		assertNotNull(Too_.id);
		assertNotNull(Too_.name);
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
		
		assertTrue(AProxy.class.isAssignableFrom(too.getFoo().getClass()));
		
		assertNotNull(too.getFoo());
		
		assertEquals(Long.valueOf(3), too.getFoo().getId());
		assertEquals("foo3", too.getFoo().getName());
		
		assertTrue(AProxy.class.isAssignableFrom(too.getBar().getClass()));
		
		assertNotNull(too.getBar());
		
		assertEquals("bar2", too.getBar().getAlias());
		assertEquals(Integer.valueOf(2), too.getBar().getSequence());
		assertEquals("Bar 2 (2)", too.getBar().getName());
		
		
		em.close();
    }

	@Test
	public void listTest() throws IllegalArgumentException, IllegalAccessException
    {

		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();
		
		CriteriaQuery<Too> criteria = builder.createQuery(Too.class);
		
		Root<Too> root = criteria.from(Too.class);
		
		criteria.select(root).where(builder.equal(root.join(Too_.bar).get(Bar_.sequence), builder.literal(1)));
		
		TypedQuery<Too> query = em.createQuery(criteria);

		List<Too> result = query.getResultList();
		
		assertEquals(2, result.size());
		
		Too too1 = null;
		Too too2 = null;
		
		for (Too too : result) {
			if (too.getId() == 1) too1 = too;
			if (too.getId() == 2) too2 = too;
		}
		
		assertNotNull(too1);
		assertNotNull(too2);
		
		assertEquals("Too1 with foo1 and bar1 (1)", too1.getName());
		assertEquals("Too2 with foo2 and bar2 (1)", too2.getName());
		
		criteria = builder.createQuery(Too.class);
		
		root = criteria.from(Too.class);
		
		criteria.select(root).where(builder.equal(root.join(Too_.bar).get(Bar_.alias), "bar2"));
		
		query = em.createQuery(criteria);

		result = query.getResultList();
		
		assertEquals(2, result.size());
		
		too1 = null;
		too2 = null;
		
		for (Too too : result) {
			if (too.getId() == 2) too1 = too;
			if (too.getId() == 3) too2 = too;
		}
		
		assertNotNull(too1);
		assertNotNull(too2);
		
		assertEquals("Too2 with foo2 and bar2 (1)", too1.getName());
		assertEquals("Too3 with foo3 and bar2 (2)", too2.getName());
		
		
		
		em.close();
    }

	
}
