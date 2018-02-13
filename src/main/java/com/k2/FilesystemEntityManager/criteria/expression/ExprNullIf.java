package com.k2.FilesystemEntityManager.criteria.expression;

public class ExprNullIf<Y> extends AbstractExpression<Y> implements FemExpression<Y> {
	
	FemExpression<Y> expr1 = null;
	FemExpression<?> expr2 = null;
	Y value2 = null;

	public ExprNullIf(FemExpression<Y> expr1, FemExpression<?> expr2) {
		super(expr1.getJavaType());
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public ExprNullIf(FemExpression<Y> expr1, Y value2) {
		super(expr1.getJavaType());
		this.expr1 = expr1;
		this.value2 = value2;
	}

	@Override
	public Y getValue(Object obj) {
		
		Object val = (expr2 == null) ? value2: expr2.getValue(obj);
		Y y = expr1.getValue(obj);
		if (y == null) return (Y)null;
		if (y.equals(val)) return (Y)null;
		return y;
		
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
