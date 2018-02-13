package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateNull extends AbstractPredicate implements FemPredicate {

	FemExpression<?> expr;
	
	public PredicateNull(FemExpression<?> expr) {
		this.expr = expr;
	}


	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(expr != null && expr instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)expr);
	}

	@Override
	public boolean evaluate(Object obj) {
		Object value = expr.getValue(obj);
		if (value == null) return (isNegated()) ? false: true;
		return (isNegated()) ? true: false;
	}

}
