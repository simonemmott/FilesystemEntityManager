package com.k2.FilesystemEntityManager.example;

	import java.util.Date;
	import javax.persistence.metamodel.SetAttribute;
	import javax.persistence.metamodel.SingularAttribute;
	import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Foo.class)
public class Foo_ {

	public static volatile SetAttribute<Foo, Bar> bars;
	public static volatile SingularAttribute<Foo, Boolean> booleanVal;
	public static volatile SingularAttribute<Foo, Date> dateVal;
	public static volatile SingularAttribute<Foo, String> description;
	public static volatile SingularAttribute<Foo, Double> doubleVal;
	public static volatile SingularAttribute<Foo, Float> floatVal;
	public static volatile SingularAttribute<Foo, String> id;
	public static volatile SingularAttribute<Foo, Integer> intVal;
	public static volatile SingularAttribute<Foo, Long> longVal;
	public static volatile SingularAttribute<Foo, Integer> sequence;
	public static volatile SingularAttribute<Foo, Too> too;

}
