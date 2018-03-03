package com.k2.FilesystemEntityManager.metamodel;

import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.ManagedType;

public class FemEmbeddableType<X> extends FemManagedType<X> implements ManagedType<X>, EmbeddableType<X> {

	FemEmbeddableType(FemMetamodel metamodel, Class<X> classType) {
		super(metamodel, classType);
	}

}
