package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprMod extends AbstractExpression<Integer> implements FemExpression<Integer> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<Integer> num1Expr;
	FemExpression<Integer> num2Expr;
	Integer num1;
	Integer num2;

	public ExprMod(FemExpression<Integer> num1, FemExpression<Integer> num2) {
		super(Integer.class);
		this.num1Expr = num1;
		this.num2Expr = num2;
	}

	public ExprMod(FemExpression<Integer> num1, Integer num2) {
		super(Integer.class);
		this.num1Expr = num1;
		this.num2 = num2;
	}

	public ExprMod(Integer num1, FemExpression<Integer> num2) {
		super(Integer.class);
		this.num1 = num1;
		this.num2Expr = num2;
	}

	@Override
	public Integer getValue(Object obj) {
		
		Integer n1 = (num1Expr == null) ? num1: num1Expr.getValue(obj);
		Integer n2 = (num2Expr == null) ? num2: num2Expr.getValue(obj);
		
		if (n1 == null || n2 == null) return null;
		
		return n1 % n2;

	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
