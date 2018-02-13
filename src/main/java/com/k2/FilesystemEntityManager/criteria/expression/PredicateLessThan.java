package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateLessThan extends AbstractPredicate implements FemPredicate {
	
	private FemExpression<? extends Comparable<?>> expr1 = null;
	private FemExpression<? extends Comparable<?>> expr2 = null;
	private Comparable<?> comp = null;

	public <Y extends Comparable<? super Y>> PredicateLessThan(FemExpression<? extends Y> expr1, FemExpression<? extends Y> expr2) {
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public <Y extends Comparable<? super Y>> PredicateLessThan(FemExpression<? extends Y> expr, Y comp) {
		this.expr1 = expr;
		this.comp = comp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean evaluate(Object obj) {
		if (expr1 == null || expr1.getValue(obj) == null) return false;
		
		Comparable c1 = expr1.getValue(obj);
		Comparable c2 = (expr2 != null) ? expr2.getValue(obj): comp;
		
		
		return isNegatedRVal(c1.compareTo(c2) < 0);
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(expr1 != null && expr1 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)expr1);
		if(expr2 != null && expr2 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)expr2);
	}

}
