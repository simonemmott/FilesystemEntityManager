package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Expressions.metamodel.MetamodelImpl;
import com.k2.Expressions.metamodel.generator.StaticMetamodelGenarator;
import com.k2.FilesystemEntityManager.testEntities.*;


public class MetamodelTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	Metamodel metamodel = new MetamodelImpl("com.k2.FilesystemEntityManager.testEntities");
	
	@Test
	public void managedTypeTest() 
    {

		assertNotNull(metamodel);
		assertEquals(4, metamodel.getManagedTypes().size());
		
		EntityType<Foo> entFoo = metamodel.entity(Foo.class);
		
		assertEquals("Foo", entFoo.getName());
    }

/*	
	@Test
	public void staticMetamodelGeneratorTest() throws IOException 
    {
		
		StaticMetamodelGenarator smg = new StaticMetamodelGenarator(metamodel);
		PrintWriter pw = new PrintWriter(System.out);
		smg.generateStaticMetamodelSource(pw, Foo.class);
		smg.generateStaticMetamodelSource(pw, Bar.class);
		smg.generateStaticMetamodelSource(pw, Too.class);
    }
*/
	
}
