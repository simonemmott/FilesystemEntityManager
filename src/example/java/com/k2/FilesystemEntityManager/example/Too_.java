package com.k2.FilesystemEntityManager.example;

import javax.persistence.metamodel.*;

import com.k2.FilesystemEntityManager.example.Too;
import com.k2.FilesystemEntityManager.metamodel.*;

@StaticMetamodel(Too.class)
public class Too_ {
	
	public static volatile SingularAttribute<Too, String> id = new FemSingularAttribute<Too, String>(Too.class, String.class, "id");
	public static volatile SingularAttribute<Too, String> name = new FemSingularAttribute<Too, String>(Too.class, String.class, "name");
	public static volatile SingularAttribute<Too, String> description = new FemSingularAttribute<Too, String>(Too.class, String.class, "description");;
	

}
