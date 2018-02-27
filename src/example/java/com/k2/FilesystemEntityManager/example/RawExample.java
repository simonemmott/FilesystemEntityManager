package com.k2.FilesystemEntityManager.example;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.FilesystemEntityManager.FemException;
import com.k2.FilesystemEntityManager.FilesystemEntityManager;
import com.k2.FilesystemEntityManager.FilesystemEntityManagerFactory;
import com.k2.Util.StringUtil;

public class RawExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws FemException, IOException {

		logger.info("Starting RawExample");
		
		FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
/*		
		femf.config().objectConfig(RawFoo.class);
		femf.config()
			.setDefaultRepo(new File("example/repos/default"))
			.setRepo("custom", new File("example/repos/custom"));
*/		
		FilesystemEntityManager fem = femf.entityManager();
		
		
		RawFoo rawFoo1 = new RawFoo()
				.setId("thisRawFoo")
				.setDescription("This is a RawFoo!")
				.setSequence(1);
		Set<Bar> bars1 = new HashSet<Bar>();
		bars1.add(new Bar()
				.setId(1)
				.setName("Bar1")
				.setDescription("This is bar one!"));
		bars1.add(new Bar()
				.setId(2)
				.setName("Bar2")
				.setDescription("This is bar two!"));
		
		rawFoo1.setBars(bars1);
				
//		fem.save(rawFoo1);

		RawFoo RawFoo2 = new RawFoo()
				.setId("thisRawFooToo")
				.setDescription("This is a RawFoo too!")
				.setSequence(1);
		Set<Bar> bars2 = new HashSet<Bar>();
		bars2.add(new Bar()
				.setId(1)
				.setName("Bar1")
				.setDescription("This is RawFoo two bar one!"));
		bars2.add(new Bar()
				.setId(2)
				.setName("Bar2")
				.setDescription("This is RawFoo two bar two!"));
		
		RawFoo2.setBars(bars2);
				
//		fem.save(RawFoo2);
		
		fem.commit();

		
		RawFoo RawFoo = fem.fetch(RawFoo.class, "thisRawFoo");
		
		RawFoo.setDescription("This has been updated");
		
//		fem.save(RawFoo);
		
//		fem.commit();
		
//		RawFoo RawFoo3 = fem.fetch(RawFoo.class, "thisRawFooToo");
		
//		fem.delete(RawFoo2);
		
//		fem.commit();
		
		fem.close();
			
		femf.shutdown();
	
		File file = new File("example/new/femf/fem.conf");
		
		System.out.println(Hashing.crc32().toString());
		
		System.out.println(StringUtil.replaceAll("MD5 hash for '{}' is {}", "{}", file.getPath(), Files.hash(file, Hashing.md5())));

	}

}
