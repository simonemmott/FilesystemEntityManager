package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.criteria.Expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToString extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<Character> character;

	public ExprToString(FemExpression<Character> character) {
		super(String.class);
		this.character = character;
	}

	@Override
	public String getValue(Object obj) {

		Character c = character.getValue(obj);
		
		if (c == null) return null;
		
		return String.valueOf(c);

	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
