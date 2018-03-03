package com.k2.FilesystemEntityManager.metamodel.generator;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import com.k2.FilesystemEntityManager.FemError;

public class AttributeProformaAdapter<E,T> implements Comparable<AttributeProformaAdapter<?,?>>{

	private Attribute<E,T> attr;
	public AttributeProformaAdapter(Attribute<E,T> attr) {
		this.attr = attr;
	}

	public String getAttributeClassName() {
		if (attr instanceof SingularAttribute) return "SingularAttribute";
		if (attr instanceof SetAttribute) return "SetAttribute";
		throw new FemError("Unexpected attribute instance {}", attr.getClass().getName());
	}
	
	public String getClassName() {
		return attr.getDeclaringType().getJavaType().getSimpleName();
	}
	
	public String getAttributeDataTypeClassName() {
		Class<?> type = attr.getJavaType();
		if (attr instanceof SetAttribute) {
			type = ((SetAttribute)attr).getBindableJavaType();
		}
		if (type.isPrimitive()) {
			if (type == int.class) return "Integer";
			if (type == long.class) return "Long";
			if (type == float.class) return "Float";
			if (type == double.class) return "Double";
			if (type == char.class) return "Character";
			if (type == boolean.class) return "Boolean";
			if (type == short.class) return "Short";
			if (type == byte.class) return "Byte";
		} 
		return type.getSimpleName();
	}
	
	public String getAttributeAlias() {
		return attr.getName();
	}

	@Override
	public int compareTo(AttributeProformaAdapter<?,?> o) {
		return attr.getName().compareTo(o.attr.getName());
	}
}
