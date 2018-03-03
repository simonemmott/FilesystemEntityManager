package com.k2.FilesystemEntityManager.metamodel.generator;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.classes.Dependencies;
import com.k2.Util.classes.Dependency;

public class StaticMetaModelClassAdapter<E> {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ManagedType<E> managedType;
	
	public StaticMetaModelClassAdapter(ManagedType<E> managedType) {
		this.managedType = managedType;
	}
	
	public String getPackageName() {
		return managedType.getJavaType().getPackage().getName();
	}

	public String getClassName() {
		return managedType.getJavaType().getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	public String getSuperClassName() {
		if (managedType instanceof EntityType) {
			EntityType et = (EntityType)managedType;
			IdentifiableType superType = et.getSupertype();
			if (superType == null) return "";
			return superType.getJavaType().getSimpleName();
		}
		return "";
	}

	@SuppressWarnings("rawtypes")
	public Boolean getHasSuperType() {
		if (managedType instanceof EntityType) {
			EntityType et = (EntityType)managedType;
			return (et.getSupertype() != null);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Set<Dependency> getDependencies() {
		Dependencies dependencies = new Dependencies();
		dependencies.add(StaticMetamodel.class);
		
		for (Attribute<E,?> attr : managedType.getDeclaredAttributes()) {
			if (attr instanceof SingularAttribute) {
				SingularAttribute<E,?> singleAttr = (SingularAttribute<E,?>)attr;
				if (Dependencies.requiresImport(singleAttr.getJavaType(), managedType.getJavaType().getPackage()))
					dependencies.add(singleAttr.getJavaType());
			} else if (attr instanceof SetAttribute) {
				SetAttribute<E,?> setAttr = (SetAttribute<E,?>)attr;
				if (Dependencies.requiresImport(setAttr.getBindableJavaType(), managedType.getJavaType().getPackage()))
					dependencies.add(setAttr.getBindableJavaType());
			}
			
			
			for (Class<?> iface : attr.getClass().getInterfaces()) {
				if (Attribute.class.isAssignableFrom(iface)) 
					dependencies.add(iface);
			}
		}
		
		return dependencies.getDependencies();
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<AttributeProformaAdapter<E,?>> getAttributes() {
		Set<AttributeProformaAdapter<E,?>> attributes = new TreeSet<AttributeProformaAdapter<E,?>>();
		for (Attribute<E,?> attr : managedType.getDeclaredAttributes()) {
			attributes.add(new AttributeProformaAdapter(attr));
		}
		return attributes;
	}
}
