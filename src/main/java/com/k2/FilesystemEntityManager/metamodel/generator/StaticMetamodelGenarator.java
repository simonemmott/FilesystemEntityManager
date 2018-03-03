package com.k2.FilesystemEntityManager.metamodel.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Set;

import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Expressions.predicate.PredicateBuilder;
import com.k2.FilesystemEntityManager.FemError;
import com.k2.Proforma.Proforma;

public class StaticMetamodelGenarator {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static Proforma P = new Proforma();
	private static PredicateBuilder pb = new PredicateBuilder();

	@SuppressWarnings("static-access")
	private static Proforma attributeProforma = new Proforma("attributeProforma")
			.add("public static volatile ", P.p("attributeClassName"), "<", P.p("className"), ", ", P.p("attributeDataTypeClassName"), "> ", P.p("attributeAlias"), ";");
	
	@SuppressWarnings("static-access")
	private static Proforma importProforma = new Proforma("import").setEmbedded(false)
			.add(P.p("importClause"));

	@SuppressWarnings("static-access")
	private static Proforma staticMetamodelClass = new Proforma("staticMetamodelClass")
			.add("package ", P.p("packageName"), ";")
			.add()
			.add(importProforma.with(P.p(Set.class, "dependencies")))
			.add("@StaticMetamodel(", P.p("className"), ".class)")
			.addIf(pb.not(P.p(Boolean.class, "hasSuperType")), "public class ", P.p("className"), "_ {")
			.addIf(P.p(Boolean.class, "hasSuperType"), "public class ", P.p("className"), "_ extends ", P.p("superClassName"), "_ {")
			.add()
			.add(attributeProforma.with(P.p(Set.class, "attributes")))
			.add("}");
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			
		} else {
			
		}
		
		
		
	}

	
	Metamodel metamodel;
	
	public StaticMetamodelGenarator(Metamodel metamodel) {
		this.metamodel = metamodel;
	}
	
	public <T> Writer generateStaticMetamodelSource(Writer out, Class<T> cls) throws IOException {
		
		staticMetamodelClass.with(new StaticMetaModelClassAdapter<T>(metamodel.managedType(cls))).setIndent("\t").write(out).flush();
		
		return out;
	}

}
