package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToFloat extends AbstractExpression<Float> implements FemExpression<Float> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToFloat(FemExpression<? extends Number> num) {
		super(Float.class);
		this.num = num;
	}

	@Override
	public Float getValue(Object obj) {

		Number n = num.getValue(obj);

		if (n == null) return null;
		 
		return Float.valueOf(n.floatValue());
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
