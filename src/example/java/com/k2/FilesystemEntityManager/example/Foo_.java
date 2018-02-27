package com.k2.FilesystemEntityManager.example;

import java.util.Date;

import javax.persistence.metamodel.*;

import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.FilesystemEntityManager.example.Foo;
import com.k2.FilesystemEntityManager.example.Too;
import com.k2.FilesystemEntityManager.metamodel.*;

@StaticMetamodel(Foo.class)
public class Foo_ {
	
	public static volatile SingularAttribute<Foo, String> id = new FemSingularAttribute<Foo, String>(Foo.class, String.class, "id");
	public static volatile SingularAttribute<Foo, Integer> sequence = new FemSingularAttribute<Foo, Integer>(Foo.class, Integer.class, "sequence");
	public static volatile SingularAttribute<Foo, String> description = new FemSingularAttribute<Foo, String>(Foo.class, String.class, "description");;
	public static volatile SingularAttribute<Foo, Integer> intVal = new FemSingularAttribute<Foo, Integer>(Foo.class, Integer.class, "intVal");;
	public static volatile SingularAttribute<Foo, Long> longVal = new FemSingularAttribute<Foo, Long>(Foo.class, Long.class, "longVal");;
	public static volatile SingularAttribute<Foo, Float> floatVal = new FemSingularAttribute<Foo, Float>(Foo.class, Float.class, "floatVal");;
	public static volatile SingularAttribute<Foo, Double> doubleVal = new FemSingularAttribute<Foo, Double>(Foo.class, Double.class, "doubleVal");;
	public static volatile SingularAttribute<Foo, Boolean> booleanVal = new FemSingularAttribute<Foo, Boolean>(Foo.class, Boolean.class, "booleanVal");;
	public static volatile SingularAttribute<Foo, Date> dataVal = new FemSingularAttribute<Foo, Date>(Foo.class, Date.class, "dateVal");;
	public static volatile SingularAttribute<Foo, Too> too = new FemSingularAttribute<Foo, Too>(Foo.class, Too.class, "too");;
	public static volatile SetAttribute<Foo, Bar> bars = new FemSetAttribute<Foo, Bar>(Foo.class, Bar.class, "bars");
	
}
