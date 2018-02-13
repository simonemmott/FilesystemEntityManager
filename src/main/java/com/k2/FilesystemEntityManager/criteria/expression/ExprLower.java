package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

public class ExprLower extends AbstractExpression<String> implements FemExpression<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> string;

	public ExprLower(FemExpression<String> string) {
		super(String.class);
		this.string = string;
	}

	@Override
	public String getValue(Object obj) {

		
		if (string==null || string.getValue(obj)==null) return null;
		String s = string.getValue(obj);
		logger.trace("Lower '{}' is '{}'", s, s.toLowerCase());
		return s.toLowerCase();
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
