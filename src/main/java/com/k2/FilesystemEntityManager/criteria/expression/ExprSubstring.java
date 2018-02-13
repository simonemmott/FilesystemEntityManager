package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import javax.persistence.criteria.Expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprSubstring extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> stringExp = null;
	FemExpression<Integer> fromExp = null;
	Integer from = null;
	FemExpression<Integer> lengthExp = null;
	Integer length = null;

	public ExprSubstring(FemExpression<String> string, FemExpression<Integer> from) {
		super(String.class);
		this.stringExp = string;
		this.fromExp = from;
	}

	public ExprSubstring(FemExpression<String> string, int from) {
		super(String.class);
		this.stringExp = string;
		this.from = from;
	}

	public ExprSubstring(FemExpression<String> string, FemExpression<Integer> from, FemExpression<Integer> length) {
		super(String.class);
		this.stringExp = string;
		this.fromExp = from;
		this.lengthExp = length;
	}

	public ExprSubstring(FemExpression<String> string, int from, int length) {
		super(String.class);
		this.stringExp = string;
		this.from = from;
		this.length = length;
	}

	@Override
	public String getValue(Object obj) {
		String s = stringExp.getValue(obj);
		int f = (fromExp == null) ? from: fromExp.getValue(obj);
		Integer l = (lengthExp == null) ? length: lengthExp.getValue(obj);
		
		if (s == null) return null;
		if (f-1 < 0) {
			return s;
		} else if (f-1 > s.length()) {
			return "";
		} else if (l == null || f+l-1 > s.length()) {
			return s.substring(f-1);
		} else {
			return s.substring(f-1, f+l-1);
		}
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
