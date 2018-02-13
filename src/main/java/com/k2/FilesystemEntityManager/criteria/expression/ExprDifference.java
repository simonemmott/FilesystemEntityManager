package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprDifference<N extends Number> extends AbstractExpression<N> implements FemExpression<N> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<? extends N> numExp1 = null;
	FemExpression<? extends N> numExp2 = null;
	Number num1 = null;
	Number num2 = null;

	public ExprDifference(FemExpression<? extends N> numExp1, FemExpression<? extends N> numExp2) {
		super(numExp1.getJavaType());
		this.numExp1 = numExp1;
		this.numExp2 = numExp2;
	}

	public ExprDifference(FemExpression<? extends N> numExp1, N num2) {
		super(numExp1.getJavaType());
		this.numExp1 = numExp1;
		this.num2 = num2;
	}

	public ExprDifference(N num1, FemExpression<? extends N> numExp2) {
		super(numExp2.getJavaType());
		this.num1 = num1;
		this.numExp2 = numExp2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getValue(Object obj) {
		Number n1 = num1;
		Number n2 = num2;
		if (n1 == null) n1 = numExp1.getValue(obj);
		if (n2 == null) n2 = numExp2.getValue(obj);
		
		logger.trace("Difference: {} - {} = {}", n1, n2, n1.doubleValue()-n2.doubleValue());
		
		Double d = n1.doubleValue()-n2.doubleValue();
		
		if (this.getJavaType().equals(Integer.class)) {
			return (N) new Integer(d.intValue());
		} else if (this.getJavaType().equals(Long.class)) {
			return (N) new Long(d.longValue());
		} else if (this.getJavaType().equals(Float.class)) {
			return (N) new Float(d.floatValue());
		} else if (this.getJavaType().equals(Double.class)) {
			return (N) d;
		} 
		
		return null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
