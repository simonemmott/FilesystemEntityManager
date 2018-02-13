package com.k2.FilesystemEntityManager.criteria.expression;


public class ExprNullLiteral<T> extends AbstractExpression<T> implements FemExpression<T> {
	
	public ExprNullLiteral(Class<T> cls) {
		super(cls);
	}

	@Override
	public T getValue(Object obj) {
		return (T)null;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
