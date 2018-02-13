package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprToBigDecimal extends AbstractExpression<BigDecimal> implements FemExpression<BigDecimal> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprToBigDecimal(FemExpression<? extends Number> num) {
		super(BigDecimal.class);
		this.num = num;
	}

	@Override
	public BigDecimal getValue(Object obj) {

		Number n = num.getValue(obj);

		if (n.getClass().equals(Integer.class)) {
			return BigDecimal.valueOf(n.intValue());
		} else if (n.getClass().equals(Long.class)) {
			return BigDecimal.valueOf(n.longValue());
		} else if (n.getClass().equals(Float.class)) {
			return BigDecimal.valueOf(n.floatValue());
		} else if (n.getClass().equals(Double.class)) {
			return BigDecimal.valueOf(n.doubleValue());
		} else 
		return (BigDecimal)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
