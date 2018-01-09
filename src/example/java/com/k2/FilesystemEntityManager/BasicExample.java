package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.k2.Util.StringUtil;

public class BasicExample {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws FemException, IOException {

		FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));
/*		
		femf.config().objectConfig(RawFoo.class);
		femf.config()
			.setDefaultRepo(new File("example/repos/default"))
			.setRepo("custom", new File("example/repos/custom"));
*/		
		FilesystemEntityManager fem = femf.entityManager();
		
		logger.info("Staring BasicExample");
		Foo foo1 = new Foo()
				.setId("thisFoo")
				.setDescription("This is a Foo!")
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
		
		foo1.setBars(bars1);
				
//		fem.save(foo1);

		Foo foo2 = new Foo()
				.setId("thisFooToo")
				.setDescription("This is a Foo too!")
				.setSequence(1);
		Set<Bar> bars2 = new HashSet<Bar>();
		bars2.add(new Bar()
				.setId(1)
				.setName("Bar1")
				.setDescription("This is Foo two bar one!"));
		bars2.add(new Bar()
				.setId(2)
				.setName("Bar2")
				.setDescription("This is Foo two bar two!"));
		
		foo2.setBars(bars2);
				
//		fem.save(foo2);
		
		fem.commit();

		
		Foo foo = fem.fetch(Foo.class, "thisFoo");
		
		foo.setDescription("This has been updated");
		
//		fem.save(foo);
		
		fem.commit();
		
//		Foo foo3 = fem.fetch(Foo.class, "thisFooToo");
		
//		fem.delete(foo3);
		
//		fem.commit();
		
		fem.close();
			
		femf.shutdown();
	
		File file = new File("example/new/femf/fem.conf");
		
		System.out.println(StringUtil.replaceAll("MD5 hash for '{}' is {}", "{}", file.getPath(), Files.hash(file, Hashing.md5())));

	}

}
