package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToDouble extends AbstractExpression<Double> implements FemExpression<Double> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToDouble(FemExpression<? extends Number> num) {
		super(Double.class);
		this.num = num;
	}

	@Override
	public Double getValue(Object obj) {

		Number n = num.getValue(obj);

		if (n == null) return null;
		 
		return Double.valueOf(n.doubleValue());
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
