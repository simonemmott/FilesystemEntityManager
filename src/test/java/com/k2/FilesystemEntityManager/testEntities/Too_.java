package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Too.class)
public class Too_ {
	
	public static volatile SingularAttribute<Too, Long> id;
	public static volatile SingularAttribute<Too, Long> fooId;
	public static volatile SingularAttribute<Too, Foo> foo;
	public static volatile SingularAttribute<Too, String> barAlias;
	public static volatile SingularAttribute<Too, Integer> barSequence;
	public static volatile SingularAttribute<Too, Bar> bar;
	public static volatile SingularAttribute<Too, String> name;
	

}
