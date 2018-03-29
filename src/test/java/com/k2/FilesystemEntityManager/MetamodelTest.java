package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.metamodel.FemMetamodel;
import com.k2.FilesystemEntityManager.metamodel.generator.StaticMetamodelGenarator;
import com.k2.FilesystemEntityManager.testEntities.*;
import com.k2.Proxy.AProxy;
import com.k2.Util.FileUtil;
import com.k2.Util.KeyUtil;


public class MetamodelTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	Metamodel metamodel = new FemMetamodel("com.k2.FilesystemEntityManager.testEntities");
	
	@Test
	public void managedTypeTest() 
    {

		assertNotNull(metamodel);
		assertEquals(4, metamodel.getManagedTypes().size());
		
		EntityType<Foo> entFoo = metamodel.entity(Foo.class);
		
		assertEquals("Foo", entFoo.getName());
    }

	@Test
	public void staticMetamodelGeneratorTest() throws IOException 
    {
		
		StaticMetamodelGenarator smg = new StaticMetamodelGenarator(metamodel);
		PrintWriter pw = new PrintWriter(System.out);
		smg.generateStaticMetamodelSource(pw, Foo.class);
		smg.generateStaticMetamodelSource(pw, Bar.class);
		smg.generateStaticMetamodelSource(pw, Too.class);
    }

	
}
