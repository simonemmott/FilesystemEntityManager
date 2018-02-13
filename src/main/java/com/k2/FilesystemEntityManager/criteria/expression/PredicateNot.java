package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateNot extends AbstractPredicate implements FemPredicate {

	FemExpression<Boolean> expr;
	
	public PredicateNot(FemExpression<Boolean> expr) {
		this.expr = expr;
	}


	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(expr != null && expr instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)expr);
		if(expr != null && expr instanceof FemPredicate) ((FemPredicate)expr).populateParameters(parameters);
	}

	@Override
	public boolean evaluate(Object obj) {
		Boolean b = expr.getValue(obj);
		if (b == null) return false;
		return isNegatedRVal(!b);
	}

}
