package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Foo.class)
public class Foo_ {
	
	public static volatile SingularAttribute<Foo, Long> id;
	public static volatile SingularAttribute<Foo, String> name;
	

}
