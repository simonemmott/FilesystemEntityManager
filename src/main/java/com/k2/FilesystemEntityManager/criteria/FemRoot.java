package com.k2.FilesystemEntityManager.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public class FemRoot<T> extends FemFrom<T,T> implements Root<T> {

	private Class<T> cls;
	public FemRoot(Class<T> cls) {
		super(cls);
		this.cls = cls;
	}

	
	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	@Override
	public boolean isCompoundSelection() {
		return false;
	}

	@Override
	public Class<? extends T> getJavaType() {
		return cls;
	}

	@Override
	public EntityType<T> getModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
