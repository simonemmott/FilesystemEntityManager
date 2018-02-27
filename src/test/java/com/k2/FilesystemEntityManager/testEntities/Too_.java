package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.FilesystemEntityManager.metamodel.FemSingularAttribute;

@StaticMetamodel(Too.class)
public class Too_ {
	
	public static volatile SingularAttribute<Too, Long> id = new FemSingularAttribute<Too, Long>(Too.class, long.class, "alias");
	public static volatile SingularAttribute<Too, Long> fooId = new FemSingularAttribute<Too, Long>(Too.class, Long.class, "fooId");
	public static volatile SingularAttribute<Too, Foo> foo = new FemSingularAttribute<Too, Foo>(Too.class, Foo.class, "foo");
	public static volatile SingularAttribute<Too, String> barAlias = new FemSingularAttribute<Too, String>(Too.class, String.class, "barAlias");
	public static volatile SingularAttribute<Too, Integer> barSequence = new FemSingularAttribute<Too, Integer>(Too.class, Integer.class, "barSequence");
	public static volatile SingularAttribute<Too, Bar> bar = new FemSingularAttribute<Too, Bar>(Too.class, Bar.class, "bar");
	public static volatile SingularAttribute<Too, String> name = new FemSingularAttribute<Too, String>(Too.class, String.class, "name");
	

}
