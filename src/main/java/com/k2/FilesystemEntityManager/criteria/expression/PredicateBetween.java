package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateBetween extends AbstractPredicate implements FemPredicate {

	FemExpression<? extends Comparable<?>> check;
	FemExpression<? extends Comparable<?>> beginExpr = null;
	FemExpression<? extends Comparable<?>> endExpr = null;
	@SuppressWarnings("rawtypes")
	Comparable begin = null;
	@SuppressWarnings("rawtypes")
	Comparable end = null;
	
	public <Y extends Comparable<? super Y>> PredicateBetween(FemExpression<? extends Y> check,
			FemExpression<? extends Y> beginExpr, FemExpression<? extends Y> endExpr) {
		this.check = check;
		this.beginExpr = beginExpr;
		this.endExpr = endExpr;
	}

	public <Y extends Comparable<? super Y>> PredicateBetween(FemExpression<? extends Y> check, Y begin, Y end) {
		this.check = check;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(check != null && check instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)check);
		if(beginExpr != null && beginExpr instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)beginExpr);
		if(endExpr != null && endExpr instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)endExpr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean evaluate(Object obj) {
		Comparable<?> checkV = check.getValue(obj);
		if (checkV == null) return false;
		if (begin == null) begin = beginExpr.getValue(obj);
		if (end == null) end = endExpr.getValue(obj);
			
		return isNegatedRVal( begin.compareTo(checkV) <= 0 && end.compareTo(checkV) >= 0);
	}

}
