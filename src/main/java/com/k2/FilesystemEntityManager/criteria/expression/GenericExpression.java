package com.k2.FilesystemEntityManager.criteria.expression;

public class GenericExpression<X> extends AbstractExpression<X> implements FemExpression<X> {

	FemExpression<?> femExpression;
	
	public GenericExpression(FemExpression<?> femExpression, Class<X> javaType) {
		super(javaType);
		this.femExpression = femExpression;
	}

	@Override
	public boolean evaluate(Object obj) {
		return femExpression.evaluate(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public X getValue(Object obj) {
		return (X) femExpression.getValue(obj);
	}

}
