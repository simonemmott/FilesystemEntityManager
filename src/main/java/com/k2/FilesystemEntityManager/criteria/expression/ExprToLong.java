package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToLong extends AbstractExpression<Long> implements FemExpression<Long> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToLong(FemExpression<? extends Number> num) {
		super(Long.class);
		this.num = num;
	}

	@Override
	public Long getValue(Object obj) {

		Number n = num.getValue(obj);
		
		if (n == null) return null;
 
		return Long.valueOf(n.longValue());
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
