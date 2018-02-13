package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToBigInteger extends AbstractExpression<BigInteger> implements FemExpression<BigInteger> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToBigInteger(FemExpression<? extends Number> num) {
		super(BigInteger.class);
		this.num = num;
	}

	@Override
	public BigInteger getValue(Object obj) {

		Number n = num.getValue(obj);

		if (n.getClass().equals(Integer.class)) {
			return BigInteger.valueOf(n.intValue());
		} else if (n.getClass().equals(Long.class)) {
			return BigInteger.valueOf(n.longValue());
		} else if (n.getClass().equals(Float.class)) {
			return BigInteger.valueOf(n.longValue());
		} else if (n.getClass().equals(Double.class)) {
			return BigInteger.valueOf(n.longValue());
		} else 
		return (BigInteger)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
