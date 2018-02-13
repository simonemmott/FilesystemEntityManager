package com.k2.FilesystemEntityManager.criteria.expression;


public class ExprLiteral<T> extends AbstractExpression<T> implements FemExpression<T> {
	
	T literal;

	@SuppressWarnings("unchecked")
	public ExprLiteral(T literal) {
		super((Class<? extends T>) literal.getClass());
		this.literal = literal;
	}

	@Override
	public T getValue(Object obj) {
		return literal;
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
