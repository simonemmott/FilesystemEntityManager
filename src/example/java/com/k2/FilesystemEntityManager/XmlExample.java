package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws FemException, IOException {

		FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
		
		femf.config()
			.setDefaultRepo(new File("example/repos/default"))
			.setRepo("xml", new File("example/repos/xml"));
		femf.config().objectConfig(XmlFoo.class)
			.dataFormat(FemDataFormat.XML)
			.repository("xml")
			.configure();
		
		FilesystemEntityManager fem = femf.entityManager();
		
		
		XmlFoo foo1 = new XmlFoo()
				.setId("xmlFoo")
				.setDescription("This is an xml foo!")
				.setSequence(1);
		Set<XmlBar> bars1 = new HashSet<XmlBar>();
		bars1.add(new XmlBar()
				.setId(1)
				.setName("XmlBar1")
				.setDescription("This is xml bar one!"));
		bars1.add(new XmlBar()
				.setId(2)
				.setName("XmlBar2")
				.setDescription("This is xml bar two!"));
		
		foo1.setBars(bars1);
				
		fem.save(foo1);

		XmlFoo foo2 = new XmlFoo()
				.setId("xmlFooToo")
				.setDescription("This is an xml foo too!")
				.setSequence(1);
		Set<XmlBar> bars2 = new HashSet<XmlBar>();
		bars2.add(new XmlBar()
				.setId(1)
				.setName("XmlBar1")
				.setDescription("This is foo two xml bar one!"));
		bars2.add(new XmlBar()
				.setId(2)
				.setName("XmlBar2")
				.setDescription("This is foo two xml  two!"));
		
		foo2.setBars(bars2);
				
		fem.save(foo2);
		
		fem.commit();
		
		fem.close();
		
		fem = femf.entityManager();

		
		XmlFoo foo = fem.fetch(XmlFoo.class, "xmlFoo");
		
		foo.setDescription("This has been updated");
		
		fem.save(foo);
		
		fem.commit();
		
		XmlFoo foo3 = fem.fetch(XmlFoo.class, "xmlFooToo");
		
		if (foo3 == null) logger.error("foo3: 'xmlFooToo' is null");
		
		fem.delete(foo3);
		
		fem.commit();
		
		fem.close();
			
		femf.shutdown();
	

	}

}
