package com.k2.FilesystemEntityManager.testEntities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.k2.FilesystemEntityManager.example.Bar;
import com.k2.FilesystemEntityManager.metamodel.FemSingularAttribute;

@StaticMetamodel(Bar.class)
public class Bar_ {
	
	public static volatile SingularAttribute<Bar, String> alias = new FemSingularAttribute<Bar, String>(Bar.class, String.class, "alias");
	public static volatile SingularAttribute<Bar, Integer> sequnce = new FemSingularAttribute<Bar, Integer>(Bar.class, Integer.class, "sequence");
	public static volatile SingularAttribute<Bar, String> name = new FemSingularAttribute<Bar, String>(Bar.class, String.class, "name");
	

}
