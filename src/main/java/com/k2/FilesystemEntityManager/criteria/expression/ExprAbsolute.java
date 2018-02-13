package com.k2.FilesystemEntityManager.criteria.expression;

public class ExprAbsolute<N extends Number> extends AbstractExpression<N> implements FemExpression<N> {
	
	FemExpression<N> num;

	@SuppressWarnings("unchecked")
	public ExprAbsolute(FemExpression<N> num) {
		super((Class<? extends N>) num.getClass());
		this.num = num;
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getValue(Object obj) {

		N value = num.getValue(obj);
		if (value == null) return null;
		if (num.getJavaType().equals(Integer.class)) {
			return (N)new Integer(Math.abs(value.intValue()));
		} else if (num.getJavaType().equals(Long.class)) {
			return (N)new Long(Math.abs(value.longValue()));
		} else if (num.getJavaType().equals(Float.class)) {
			return (N)new Float(Math.abs(value.floatValue()));
		} else if (num.getJavaType().equals(Double.class)) {
			return (N)new Double(Math.abs(value.doubleValue()));
		}
		
		return value;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
