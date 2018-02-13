package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateGT extends AbstractPredicate implements FemPredicate {
	
	private FemExpression<? extends Number> value1;
	private Object value2;

	public PredicateGT(FemExpression<? extends Number> expr1, FemExpression<? extends Number> expr2) {
		value1 = expr1;
		value2 = expr2;
	}

	public PredicateGT(FemExpression<? extends Number> expr, Number num) {
		value1 = expr;
		value2 = num;
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(value1 != null && value1 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)value1);
		if(value2 != null && value2 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)value2);
	}

	@Override
	public boolean evaluate(Object obj) {
		if (value1 == null || value1.getValue(obj) == null || value2 == null) return false;
		
		if (value2 instanceof FemExpression) {
			
			FemExpression<?> value2Expr = (FemExpression<?>)value2;
			return isNegatedRVal(value1.getValue(obj).doubleValue() > ((Number) value2Expr.getValue(obj)).doubleValue());
		}
		
		return isNegatedRVal(value1.getValue(obj).doubleValue() > ((Number)value2).doubleValue());
	}

}
