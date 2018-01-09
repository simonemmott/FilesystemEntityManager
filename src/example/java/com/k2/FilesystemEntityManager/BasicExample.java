package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws FemException, IOException {

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
			
	}

}
