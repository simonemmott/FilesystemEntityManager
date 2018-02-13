package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToInteger extends AbstractExpression<Integer> implements FemExpression<Integer> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToInteger(FemExpression<? extends Number> num) {
		super(Integer.class);
		this.num = num;
	}

	@Override
	public Integer getValue(Object obj) {

		Number n = num.getValue(obj);

		if (n == null) return null;
		 
		return Integer.valueOf(n.intValue());
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
