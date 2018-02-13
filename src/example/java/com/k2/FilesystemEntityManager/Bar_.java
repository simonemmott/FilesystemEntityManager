package com.k2.FilesystemEntityManager;

import javax.persistence.metamodel.*;

import com.k2.FilesystemEntityManager.metamodel.*;

@StaticMetamodel(Bar.class)
public class Bar_ {
	
	public static volatile SingularAttribute<Bar, Integer> id = new FemSingularAttribute<Bar, Integer>(Bar.class, Integer.class, "id");
	public static volatile SingularAttribute<Bar, String> name = new FemSingularAttribute<Bar, String>(Bar.class, String.class, "name");
	public static volatile SingularAttribute<Bar, String> description = new FemSingularAttribute<Bar, String>(Bar.class, String.class, "description");;
	

}
