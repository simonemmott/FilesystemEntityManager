package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprSqrt extends AbstractExpression<Double> implements FemExpression<Double> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num;

	public ExprSqrt(FemExpression<? extends Number> num) {
		super(Double.class);
		this.num = num;
	}

	@Override
	public Double getValue(Object obj) {

		Object n = num.getValue(obj);
		if (n == null) return (Double)null;
		
		if (n.getClass().equals(Integer.class)) {
			return Math.sqrt(new Double((Integer)n));
		} else if (n.getClass().equals(Long.class)) {
			return Math.sqrt(new Double((Long)n));
		} else if (n.getClass().equals(Float.class)) {
			return Math.sqrt(new Double((Float)n));
		} else if (n.getClass().equals(Double.class)) {
			return Math.sqrt((Double)n);
		} else 
		return (Double)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
