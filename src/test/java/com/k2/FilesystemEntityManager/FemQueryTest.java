package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.FilesystemEntityManager.example.Foo_;
import com.k2.FilesystemEntityManager.example.Too;
import com.k2.FilesystemEntityManager.example.Too_;
import com.k2.FilesystemEntityManager.example.FemTestClient.*;
import com.k2.Util.FileUtil;


public class FemQueryTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FilesystemEntityManagerFactory femf = null;
	File testingLocation = null;

	@Before
	public void setupData() throws FemException {

		testingLocation = new File("example"+File.separatorChar+"queryTesting");
		FileUtil.deleteCascade(testingLocation);
		
		logger.debug("Creating queryTesting repos");
		testingLocation.mkdir();
		
		FileUtil.buildTree(testingLocation,
				Paths.get("femf"),
				Paths.get("repos"),
				Paths.get("repos", "default")
				);
				
		logger.info("Creating new filesystem entity manager factory");
		femf = FilesystemEntityManagerFactory.startup(new File("example/queryTesting/femf"));
		
		logger.debug("Adding repositories to filesystem entity manager factory");
		femf.config()
			.setDefaultRepo(new File("example/queryTesting/repos/default"));

		logger.debug("Configuring Foo.class");
		femf.config().objectConfig(Foo.class)
			.dataFormat(FemDataFormat.JSON)
			.dataStructure(FemDataStructure.OCN)
			.resourcePath("Foo")
			.configure();
		
		femf.manage(Foo.class, Too.class);
		
		EntityManager em = femf.createEntityManager();
		
		em.persist(new Foo()
				.setId("foo1")
				.setSequence(1)
				.setDescription("This is foo 1")
				.setIntVal(100)
				.setDoubleVal(1.234)
				.setDateVal(new Date(900000000))
				.setBooleanVal(true)
				.setToo(new Too()
						.setId("foo1")
						.setName("Too for foo 1")
						.setDescription("This too is foo 1's too")));
		
		em.persist(new Foo()
				.setId("foo2")
				.setSequence(2)
				.setDescription("This is foo 2")
				.setDoubleVal(2.456)
				.setDateVal(new Date(987654321))
				.setBooleanVal(true));
		
		em.persist(new Foo()
				.setId("foo3")
				.setSequence(2)
				.setDescription("This is foo 3")
				.setIntVal(101)
				.setFloatVal((float) 2.2)
				.setDoubleVal(3.789)
				.setDateVal(new Date(999999999))
				.setBooleanVal(true)
				.setToo(new Too()
						.setId("foo3")
						.setName("Too 3")
						.setDescription("Too 3 is foo 3's too")));
		
		em.persist(new Foo()
				.setId("foo4")
				.setSequence(4)
				.setDescription("This is foo 4, xxxx")
				.setIntVal(-102)
				.setFloatVal((float) 1.2)
				.setDoubleVal(-1.234)
				.setDateVal(new Date(888888888))
				.setBooleanVal(false)
				.setToo(new Too()
						.setId("foo4")
						.setName("Too for foo 4")
						.setDescription("This describes the too for foo 4")));
		
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
	public void listAllTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();

		assertEquals(4, result.size());
		
		Foo foo = em.find(Foo.class, "foo3");
		em.remove(foo);
		
		result = query.getResultList();
		assertEquals(3, result.size());
		
		EntityManager em2 = femf.createEntityManager();
		
		TypedQuery<Foo> query2 = em2.createQuery(criteria);

		List<Foo> result2 = query2.getResultList();

		assertEquals(4, result2.size());

		
		FilesystemEntityManager fem = em.unwrap(FilesystemEntityManager.class);
		fem.rollback();
	
		result = query.getResultList();
		assertEquals(4, result.size());
			

		em2.close();
		em.close();
		       
    }

	@Test
	public void filteredListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.equal(root.get(Foo_.sequence), builder.literal(2)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();

		assertEquals(2, result.size());		
		
		em.close();
		       
    }

	@Test
	public void filteredListTest2() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.equal(root.get(Foo_.description), builder.literal("This is foo 3")));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();

		assertEquals(1, result.size());
		
		Foo listedFoo = result.get(0);
		assertEquals("foo3", listedFoo.getId());
		assertEquals(new Integer(2), listedFoo.getSequence());
		assertEquals("This is foo 3", listedFoo.getDescription());
		
		em.close();
		       
    }

	@Test
	public void filteredListTest3() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.equal(root.get(Foo_.description), root.get(Foo_.description)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();

		assertEquals(4, result.size());
				
		em.close();
		       
    }

	@Test
	public void filteredListTest4() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.ge(root.get(Foo_.sequence), builder.literal(2)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
				
		em.close();
		       
    }

	@Test
	public void filteredListTest5() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.ge(root.get(Foo_.sequence), 2));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
				
		em.close();
		       
    }

	@Test
	public void nullListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(root.get(Foo_.intVal).isNull());

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());

		criteria.select(root).where(root.get(Foo_.intVal).isNotNull().not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());

		em.close();
		       
    }

	@Test
	public void notNullListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(root.get(Foo_.intVal).isNotNull());

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
				
		criteria.select(root).where(root.get(Foo_.intVal).isNull().not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
				
		em.close();
		       
    }

	@Test
	public void absoluteValueListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		
		criteria.select(root).where(builder.ge(builder.abs(root.get(Foo_.intVal)), 101));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void andListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.and(
				builder.ge(builder.abs(root.get(Foo_.intVal)), 101),
				builder.ge(root.get(Foo_.sequence), 2),
				builder.equal(root.get(Foo_.floatVal), 1.2F)
			)
		);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void orListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.or(
				builder.ge(builder.abs(root.get(Foo_.intVal)), 101),
				builder.ge(root.get(Foo_.sequence), 2),
				builder.equal(root.get(Foo_.floatVal), 1.2F)
			)
		);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void betweenListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.between(
				root.get(Foo_.doubleVal),
				(Double)1.0,
				(Double)3.0)
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void betweenListTest2() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.between(
				root.get(Foo_.doubleVal),
				builder.literal(-2.0),
				builder.literal(3.0))
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void concatenateListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.equal(
				builder.concat(root.get(Foo_.id), root.get(Foo_.description)),
				"foo2This is foo 2")
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void differenceListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.equal(
				builder.diff(root.get(Foo_.sequence), 2),
				0)
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void greaterThanOrEqualListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.greaterThanOrEqualTo(root.get(Foo_.dateVal), new Date(900000000))
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void greaterThanListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.greaterThan(root.get(Foo_.dateVal), new Date(900000000))
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void gtListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.gt(root.get(Foo_.sequence), 2)
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void isFalseListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.isFalse(root.get(Foo_.booleanVal)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.isFalse(root.get(Foo_.booleanVal)).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void isTrueListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.isTrue(root.get(Foo_.booleanVal)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		criteria.select(root).where(builder.isTrue(root.get(Foo_.booleanVal)).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void isNullListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.isNull(root.get(Foo_.intVal)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.isNull(root.get(Foo_.intVal)).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void isNotNullListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.isNotNull(root.get(Foo_.intVal)));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		criteria.select(root).where(builder.isNotNull(root.get(Foo_.intVal)).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void leListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.le(root.get(Foo_.sequence), 2));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		criteria.select(root).where(builder.le(root.get(Foo_.sequence), 2).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void lenghListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.length(root.get(Foo_.description)), 19));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.equal(builder.length(root.get(Foo_.description)), 19).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void lessThanOrEqualListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.lessThanOrEqualTo(root.get(Foo_.dateVal), new Date(900000000))
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void lessThanListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
			builder.lessThan(root.get(Foo_.dateVal), new Date(900000000))
			);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }

	@Test
	public void likeListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
				builder.like(root.get(Foo_.description), "%xxxx")
				);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(
				builder.like(root.get(Foo_.description), "%xxxx").not()
				);

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
										
		em.close();
		       
    }

	@Test
	public void locateListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
				builder.equal(builder.locate(root.get(Foo_.description), "is", 4), 6)
				);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(4, result.size());
								
		criteria.select(root).where(
				builder.equal(builder.locate(root.get(Foo_.description), "is", 4), 6).not()
				);

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(0, result.size());
										
		em.close();
		       
    }

	@Test
	public void lowerListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(
				builder.equal(builder.lower(root.get(Foo_.description)), "this is foo 1")
				);

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(
				builder.equal(builder.lower(root.get(Foo_.description)), "this is foo 1").not()
				);

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
										
		em.close();
		       
    }

	@Test
	public void ltListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.lt(root.get(Foo_.sequence), 2));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.lt(root.get(Foo_.sequence), 2).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void modListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.mod(root.get(Foo_.sequence), 3), 1));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		criteria.select(root).where(builder.equal(builder.mod(root.get(Foo_.sequence), 3), 1).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void negListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.neg(root.get(Foo_.sequence)), -2));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		criteria.select(root).where(builder.equal(builder.neg(root.get(Foo_.sequence)), -2).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void prodListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.prod(root.get(Foo_.sequence), 2), 4));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		criteria.select(root).where(builder.equal(builder.prod(root.get(Foo_.sequence), 2), 4).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void quotListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.quot(root.get(Foo_.sequence), 2), 1));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		criteria.select(root).where(builder.equal(builder.quot(root.get(Foo_.sequence), 2), 1).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(2, result.size());
								
		em.close();
		       
    }

	@Test
	public void sqrtListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.sqrt(root.get(Foo_.sequence)), 2.0));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.equal(builder.sqrt(root.get(Foo_.sequence)), 2.0).not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void substringListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);

		criteria.select(root).where(builder.equal(builder.substring(root.get(Foo_.description), 13, 4), "3"));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.equal(builder.substring(root.get(Foo_.description), 13, 4), "3").not());

		query = em.createQuery(criteria);

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(3, result.size());
								
		em.close();
		       
    }

	@Test
	public void parameterListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();		
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		ParameterExpression<String> parm = builder.parameter(String.class);

		criteria.select(root).where(builder.equal(root.get(Foo_.id), parm));

		TypedQuery<Foo> query = em.createQuery(criteria);
		
		query.setParameter(parm, "foo1");

		List<Foo> result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		criteria.select(root).where(builder.equal(root.get(Foo_.id), builder.parameter(String.class, "id")));

		query = em.createQuery(criteria);
		
		query.setParameter("id", "foo2");

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		query.setParameter("id", "foo3");

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		query.setParameter(1, "foo4");

		result = query.getResultList();
		
		for (Foo foo : result) {
			logger.debug("found Foo '{}'", foo.getId());
		}

		assertEquals(1, result.size());
								
		em.close();
		       
    }
	
	@Test
	public void exposedEntityFetchTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		Too too = em.find(Too.class, "foo1");
		
		assertNotNull(too);
		assertEquals("foo1", too.getId());
		assertEquals("Too for foo 1", too.getName());
		assertEquals("This too is foo 1's too", too.getDescription());
								
		too = em.find(Too.class, "foo2");
		
		assertNull(too);
								
		too = em.find(Too.class, "foo3");
		
		assertNotNull(too);
		assertEquals("foo3", too.getId());
		assertEquals("Too 3", too.getName());
		assertEquals("Too 3 is foo 3's too", too.getDescription());
								
		too = em.find(Too.class, "foo4");
		
		assertNotNull(too);
		assertEquals("foo4", too.getId());
		assertEquals("Too for foo 4", too.getName());
		assertEquals("This describes the too for foo 4", too.getDescription());
								
		em.close();
		       
    }
	
	@Test
	public void exposedEntitySaveTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		Too too = em.find(Too.class, "foo1");
		
		assertNotNull(too);
		assertEquals("foo1", too.getId());
		assertEquals("Too for foo 1", too.getName());
		assertEquals("This too is foo 1's too", too.getDescription());
								
		too.setName("Updated name for too 1");
		
		em.persist(too);
		
		FilesystemEntityManager fem = em.unwrap(FilesystemEntityManager.class);
		
		fem.commit();
								
		em.close();
		
		em = femf.createEntityManager();
		
		too = em.find(Too.class, "foo1");

		assertNotNull(too);

		assertEquals("foo1", too.getId());
		assertEquals("Updated name for too 1", too.getName());
		assertEquals("This too is foo 1's too", too.getDescription());
		
		too.setName("Too for foo 1");
		
		em.persist(too);
		
		fem = em.unwrap(FilesystemEntityManager.class);
		
		fem.commit();
		
		em.close();
		       
    }
	
	@Test
	public void exposedEntityDeleteTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		Too too = em.find(Too.class, "foo1");
		
		assertNotNull(too);
		assertEquals("foo1", too.getId());
		
		em.remove(too);
		
		FilesystemEntityManager fem = em.unwrap(FilesystemEntityManager.class);
		
		fem.commit();
								
		em.close();
		
		em = femf.createEntityManager();
		
		Too deletedToo = em.find(Too.class, "foo1");

		assertNull(deletedToo);
		
		em.persist(too);
		
		fem = em.unwrap(FilesystemEntityManager.class);
		
		fem.commit();
		
		em.close();
		       
		em = femf.createEntityManager();
		
		Too too2 = em.find(Too.class, "foo1");

		assertNotNull(too2);
		assertEquals("foo1", too2.getId());
		
		em.close();
		       
    }

	@Test
	public void joinListTest() throws FemException, IOException
    {
		
		EntityManager em = femf.createEntityManager();

		CriteriaBuilder builder = femf.getCriteriaBuilder();
		
		CriteriaQuery<Foo> criteria = builder.createQuery(Foo.class);

		Root<Foo> root = criteria.from(Foo.class);
		Join<Foo,Too> too = root.join(Foo_.too);
		
		criteria.select(root).where(builder.equal(too.get(Too_.name), builder.literal("Too 3")));

		TypedQuery<Foo> query = em.createQuery(criteria);

		List<Foo> result = query.getResultList();

		assertEquals(1, result.size());		
		
		em.close();
		       
    }


}
