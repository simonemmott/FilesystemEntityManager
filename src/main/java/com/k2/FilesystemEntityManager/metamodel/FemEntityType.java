package com.k2.FilesystemEntityManager.metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.Util.ObjectUtil;

public class FemEntityType<X> extends FemManagedType<X> implements ManagedType<X>, EntityType<X> {

	FemEntityType(FemMetamodel metamodel, Class<X> classType) {
		super(metamodel, classType);
	}
	Boolean hasSingleIdAttribute = null;
	Boolean hasVersionAttribute = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Attribute<X,?>  getAttributeFromField(Field field) {
		if (field.isAnnotationPresent(EmbeddedId.class)) {
			declaredId = new FieldSingularAttribute(this, field);
			hasSingleIdAttribute = false;
			return declaredId;
		}
		Attribute<X,?> attr = super.getAttributeFromField(field);
		if (field.isAnnotationPresent(Id.class)) {
			declaredId = (SingularAttribute<X, ?>) attr;
			hasSingleIdAttribute = true;
		}
		if (field.isAnnotationPresent(Version.class)) {
			declaredId = (SingularAttribute<X, ?>) attr;
			hasVersionAttribute = true;
		}
		return attr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Attribute<X,?>  getAttributeFromMethod(Method method) {
		if (method.isAnnotationPresent(EmbeddedId.class)) {
			declaredId = new MethodSingularAttribute(this, method);
			return declaredId;
		}
		Attribute<X,?> attr = super.getAttributeFromMethod(method);
		if (method.isAnnotationPresent(Id.class))
			declaredVersion = (SingularAttribute<X, ?>) attr;
		return attr;
	}

	@Override
	public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
		FemManagedType<? super X> superMt = this;
		FemManagedType<? super X> loop = superMt.getSuperManagedType();
		while (loop != null)
			loop = superMt.getSuperManagedType();
		return ((FemEntityType<? super X>)superMt).getDeclaredId(type);
	}

	SingularAttribute<X, ?> declaredId = null;
	@SuppressWarnings("unchecked")
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
		if (declaredId == null) 
			throw new FemError("No Id declared by the managed type {}", managedType.getName());
		if (declaredId.getJavaType().equals(type))
			return (SingularAttribute<X, Y>)declaredId;
		throw new FemError("The Id declared by the managed type {} was not of the expected type {}", managedType.getName(), type.getName());
	}

	@Override
	public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
		SingularAttribute<X, Y> version = getDeclaredVersion(type);
		if (version != null)
			return version;
		FemManagedType<? super X> superMt = this.getSuperManagedType();
		if (superMt == null)
			return null;
		return ((FemEntityType<? super X>)superMt).getVersion(type);
	}

	SingularAttribute<X, ?> declaredVersion = null;
	@SuppressWarnings("unchecked")
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
		if (declaredVersion == null) 
			return null;
		if (declaredVersion.getJavaType().equals(type))
			return (SingularAttribute<X, Y>)declaredVersion;
		throw new FemError("The Version declared by the managed type {} was not of the expected type {}", managedType.getName(), type.getName());
	}

	@Override
	public FemEntityType<? super X> getSupertype() {
		return (FemEntityType<? super X>) this.getSuperManagedType();
	}

	@Override
	public boolean hasSingleIdAttribute() {
		if (hasSingleIdAttribute != null) return hasSingleIdAttribute;
		FemEntityType<? super X> superMt = getSupertype();
		if (superMt != null)
			return superMt.hasSingleIdAttribute();
		throw new FemError("No Id field for managed type {}", managedType.getName());
	}

	@Override
	public boolean hasVersionAttribute() {
		if (hasVersionAttribute != null) return hasVersionAttribute;
		FemEntityType<? super X> superMt = getSupertype();
		if (superMt != null)
			return superMt.hasVersionAttribute();
		return false;
	}

	@Override
	public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
		FemEntityType<? super X> thisType = this;
		while (thisType.declaredId == null) {
			thisType = thisType.getSupertype();
			if (thisType == null)
				throw new FemError("No Id defined for {}", managedType.getName());
		}
		if (thisType.hasSingleIdAttribute) {
			return ObjectUtil.singletonSet(thisType.declaredId);
		} else {
			Set<SingularAttribute<? super X, ?>> set = new HashSet<SingularAttribute<? super X, ?>>();
			for (Attribute<?, ?> keyAttr : metamodel.embeddable(thisType.declaredId.getJavaType()).getAttributes()) {
				set.add(this.getSingularAttribute(keyAttr.getName(), keyAttr.getJavaType()));
			}
			return set;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Type<?> getIdType() {
		FemEntityType<? super X> thisType = this;
		while (thisType.declaredId == null) {
			thisType = thisType.getSupertype();
			if (thisType == null)
				throw new FemError("No Id defined for {}", managedType.getName());
		}
		Class<?> type = thisType.declaredId.getJavaType();
		if (thisType.hasSingleIdAttribute) {
			return new Type(){

				@Override
				public PersistenceType getPersistenceType() {
					return PersistenceType.BASIC;
				}

				@Override
				public Class getJavaType() {
					return type;
				}
				
			};
		} else {
			return metamodel.embeddable(thisType.declaredId.getJavaType());
		}
	}

	@Override
	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	@Override
	public Class<X> getBindableJavaType() {
		return managedType;
	}

	@Override
	public String getName() {
		return (managedType.getAnnotation(Entity.class).name().equals("")) ? managedType.getSimpleName() : managedType.getAnnotation(Entity.class).name();
	}


}
