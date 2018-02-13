package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.Expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

public class ExprLocate extends AbstractExpression<Integer> implements FemExpression<Integer> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> stringExp;
	FemExpression<String> patternExp;
	FemExpression<Integer> fromExp;
	String pattern;
	int from = 0;

	public ExprLocate(FemExpression<String> string, FemExpression<String> pattern) {
		super(Integer.class);
		this.stringExp = string;
		this.patternExp = pattern;
	}

	public ExprLocate(FemExpression<String> string, String pattern) {
		super(Integer.class);
		this.stringExp = string;
		this.pattern = pattern;
	}

	public ExprLocate(FemExpression<String> string, FemExpression<String> pattern, FemExpression<Integer> from) {
		super(Integer.class);
		this.stringExp = string;
		this.patternExp = pattern;
		this.fromExp = from;
	}

	public ExprLocate(FemExpression<String> string, String pattern, int from) {
		super(Integer.class);
		this.stringExp = string;
		this.pattern = pattern;
		this.from = (from < 0) ? 0: from;
	}

	@Override
	public Integer getValue(Object obj) {
		
		String s = stringExp.getValue(obj);
		String p = (patternExp == null) ? pattern: patternExp.getValue(obj);
		int f = from;
		if (fromExp != null) {
			Integer fromExpValue = fromExp.getValue(obj);
			if (fromExpValue != null && fromExpValue.intValue() >= 0) {
				f = fromExpValue.intValue();
			}
		}
		int i = s.indexOf(p, f)+1;
		logger.trace("Locate '{}' in '{}' after position {}{}", p, s, f, (i==0) ? "": " Found at "+i);
		return s.indexOf(p, f)+1;
			
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
