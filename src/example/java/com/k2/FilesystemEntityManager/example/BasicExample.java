package com.k2.FilesystemEntityManager.example;

import static org.junit.Assert.assertEquals;

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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemDataFormat;
import com.k2.FilesystemEntityManager.FemDataStructure;
import com.k2.FilesystemEntityManager.FemException;
import com.k2.FilesystemEntityManager.FilesystemEntityManager;
import com.k2.FilesystemEntityManager.FilesystemEntityManagerFactory;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.Util.FileUtil;

public class BasicExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FilesystemEntityManagerFactory femf = null;
	File testingLocation = null;

	public static void main(String[] args) throws FemException, IOException {

		/*
		FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
		
		femf.config().objectConfig(Too.class);
		femf.config().setDefaultRepo(new File("example/repos/default"));
		
		FilesystemEntityManager fem = femf.entityManager();
		
		Too too = new Too()
				.setId("too")
				.setDescription("This is a Too!")
				.setSequence(1)
				.addBar(new Bar()
						.setId(1)
						.setName("Bar 1")
						.setDescription("This is bar one!"))
				.addBar(new Bar()
						.setId(2)
						.setName("Bar 2")
						.setDescription("This is bar two!"));
				
		fem.save(too);
		
		fem.commit();
		
		fem.close();
		
		femf.shutdown();
			
			*/
		
		BasicExample be = new BasicExample();
		be.setupData();
		be.parameterListTest();
		be.clearDownData();
	}

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
								
		em.close();
		       
    }

	
	public void clearDownData() {
		
		logger.info("Shutting down filesystem entity manager factory");
		femf.shutdown();

	}

	
	
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
		
		EntityManager em = femf.createEntityManager();
		
		em.persist(new Foo()
				.setId("foo1")
				.setSequence(1)
				.setDescription("This is foo 1")
				.setIntVal(100)
				.setDoubleVal(1.234)
				.setDateVal(new Date(900000000))
				.setBooleanVal(true));
		
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
				.setBooleanVal(true));
		
		em.persist(new Foo()
				.setId("foo4")
				.setSequence(4)
				.setDescription("This is foo 4, xxxx")
				.setIntVal(-102)
				.setFloatVal((float) 1.2)
				.setDoubleVal(-1.234)
				.setDateVal(new Date(888888888))
				.setBooleanVal(false));
		
		FilesystemEntityManager fem = em.unwrap(FilesystemEntityManager.class);
		fem.commit();
		
		em.close();
		


	}


}
