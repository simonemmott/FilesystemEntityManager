package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.k2.FilesystemEntityManager.metamodel.FemSingularAttribute;

@StaticMetamodel(Foo.class)
public class Foo_ {
	
	public static volatile SingularAttribute<Foo, Long> id = new FemSingularAttribute<Foo, Long>(Foo.class, Long.class, "id");
	public static volatile SingularAttribute<Foo, String> name = new FemSingularAttribute<Foo, String>(Foo.class, String.class, "name");
	

}
