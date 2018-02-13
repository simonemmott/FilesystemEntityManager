package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

public class ExprUpper extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> string;

	public ExprUpper(FemExpression<String> string) {
		super(String.class);
		this.string = string;
	}

	@Override
	public String getValue(Object obj) {

		
		if (string==null) return null;
		String s = string.getValue(obj);
		if (s == null) return null;
		logger.trace("Upper '{}' is '{}'", s, s.toUpperCase());
		return s.toUpperCase();
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
