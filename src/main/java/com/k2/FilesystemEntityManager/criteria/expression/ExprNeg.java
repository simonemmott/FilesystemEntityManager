package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprNeg<N extends Number> extends AbstractExpression<N> implements FemExpression<N> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<N> num;

	public ExprNeg(FemExpression<N> num) {
		super(num.getJavaType());
		this.num = num;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getValue(Object obj) {

		N n = num.getValue(obj);
		if (n.getClass().equals(Integer.class)) {
			return (N) new Integer(n.intValue() * -1);
		} else if (n.getClass().equals(Long.class)) {
			return (N) new Long(n.longValue() * -1);
		} else if (n.getClass().equals(Float.class)) {
			return (N) new Float(n.floatValue() * -1);
		} else if (n.getClass().equals(Double.class)) {
			return (N) new Double(n.doubleValue() * -1);
		} else 
		return n;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
