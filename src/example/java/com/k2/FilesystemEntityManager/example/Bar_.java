package com.k2.FilesystemEntityManager.example;

	import javax.persistence.metamodel.SingularAttribute;
	import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Bar.class)
public class Bar_ {

	public static volatile SingularAttribute<Bar, String> description;
	public static volatile SingularAttribute<Bar, Integer> id;
	public static volatile SingularAttribute<Bar, String> name;

}
