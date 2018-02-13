package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprProd<N extends Number> extends AbstractExpression<N> implements FemExpression<N> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends N> num1Exp = null;
	FemExpression<? extends N> num2Exp = null;
	N num1 = null;
	N num2 = null;

	public ExprProd(FemExpression<? extends N> num1, FemExpression<? extends N> num2) {
		super(num1.getJavaType());
		this.num1Exp = num1;
		this.num2Exp = num2;
	}

	public ExprProd(FemExpression<? extends N> num1, N num2) {
		super(num1.getJavaType());
		this.num1Exp = num1;
		this.num2 = num2;
	}

	public ExprProd(N num1, FemExpression<? extends N> num2) {
		super(num2.getJavaType());
		this.num1 = num1;
		this.num2Exp = num2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getValue(Object obj) {

		N n1 = (num1Exp == null) ? num1: num1Exp.getValue(obj);
		N n2 = (num2Exp == null) ? num2: num2Exp.getValue(obj);
		
		if (n1 == null || n2 == null) return (N)null;
		Double d = n1.doubleValue() * n2.doubleValue();
		
		if (getJavaType().equals(Integer.class)) {
			return (N) new Integer(d.intValue());
		} else if (getJavaType().equals(Long.class)) {
			return (N) new Long(d.longValue());
		} else if (getJavaType().equals(Float.class)) {
			return (N) new Float(d.floatValue());
		} else if (getJavaType().equals(Double.class)) {
			return (N) d;
		} else 
		return (N)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
