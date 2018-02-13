package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.StringUtil;

public class ExprTrim extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> string;
	FemExpression<Character> trimCharExp;
	char trimChar = ' ';
	Trimspec trimSpec = Trimspec.BOTH;

	public ExprTrim(FemExpression<String> string) {
		super(String.class);
		this.string = string;
	}

	public ExprTrim(FemExpression<Character> trimChar, FemExpression<String> string) {
		super(String.class);
		this.string = string;
		this.trimCharExp = trimChar;
	}

	public ExprTrim(char trimChar, FemExpression<String> string) {
		super(String.class);
		this.string = string;
		this.trimChar = trimChar;
	}

	public ExprTrim(Trimspec trimSpec, FemExpression<Character> trimChar, FemExpression<String> string) {
		super(String.class);
		this.string = string;
		this.trimCharExp = trimChar;
		this.trimSpec = trimSpec;
	}

	public ExprTrim(Trimspec trimSpec, char trimChar, FemExpression<String> string) {
		super(String.class);
		this.string = string;
		this.trimChar = trimChar;
		this.trimSpec = trimSpec;
	}

	public ExprTrim(Trimspec trimSpec, FemExpression<String> string) {
		super(String.class);
		this.string = string;
		this.trimSpec = trimSpec;
	}

	public String getValue(Object obj) {

		String s = string.getValue(obj);
		char tc = (trimCharExp == null) ? trimChar: trimCharExp.getValue(obj);
		
		return StringUtil.trim(trimSpec, tc, s);

	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
