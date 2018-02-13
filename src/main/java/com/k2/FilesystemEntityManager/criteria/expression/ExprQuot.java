package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprQuot<Number> extends AbstractExpression<Number> implements FemExpression<Number> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends Number> num1Exp = null;
	FemExpression<? extends Number> num2Exp = null;
	Number num1 = null;
	Number num2 = null;

	public ExprQuot(FemExpression<? extends Number> num1, FemExpression<? extends Number> num2) {
		super(num1.getJavaType());
		this.num1Exp = num1;
		this.num2Exp = num2;
	}

	public ExprQuot(FemExpression<? extends Number> num1, Number num2) {
		super(num1.getJavaType());
		this.num1Exp = num1;
		this.num2 = num2;
	}

	public ExprQuot(Number num1, FemExpression<? extends Number> num2) {
		super(num2.getJavaType());
		this.num1 = num1;
		this.num2Exp = num2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Number getValue(Object obj) {

		Number n1 = (num1Exp == null) ? num1: num1Exp.getValue(obj);
		Number n2 = (num2Exp == null) ? num2: num2Exp.getValue(obj);
		
		if (n1 == null || n2 == null) return (Number)null;
		
		if (getJavaType().equals(Integer.class)) {
			return (Number) new Integer(((Integer)n1) / ((Integer)n2));
		} else if (getJavaType().equals(Long.class)) {
			return (Number) new Long(((Long)n1) / ((Long)n2));
		} else if (getJavaType().equals(Float.class)) {
			return (Number) new Float(((Float)n1) / ((Float)n2));
		} else if (getJavaType().equals(Double.class)) {
			return (Number) new Double(((Double)n1) / ((Double)n2));
		} else 
		return (Number)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
