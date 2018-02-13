package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprConcatenate extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> stringExp1 = null;
	FemExpression<String> stringExp2 = null;
	String string1 = null;
	String string2 = null;

	public ExprConcatenate(FemExpression<String> stringExp1, FemExpression<String> stringExp2) {
		super(String.class);
		this.stringExp1 = stringExp1;
		this.stringExp2 = stringExp2;
	}

	public ExprConcatenate(FemExpression<String> stringExp1, String string2) {
		super(String.class);
		this.stringExp1 = stringExp1;
		this.string2 = string2;
	}

	public ExprConcatenate(String string1, FemExpression<String> stringExp2) {
		super(String.class);
		this.string1 = string1;
		this.stringExp2 = stringExp2;
	}

	@Override
	public String getValue(Object obj) {
		String s1 = string1;
		String s2 = string2;
		if (s1 == null) s1 = stringExp1.getValue(obj);
		if (s2 == null) s2 = stringExp2.getValue(obj);
		
		logger.trace("Concatenate: {} + {} = {}", s1, s2, s1+s2);
		
		return s1+s2;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
