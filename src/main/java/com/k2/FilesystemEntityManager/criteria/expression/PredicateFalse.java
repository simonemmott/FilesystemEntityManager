package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateFalse extends AbstractPredicate implements FemPredicate {

	@Override
	public void populateParameters(FemQueryParameters parameters) {}

	@Override
	public List<Expression<Boolean>> getExpressions() {
		List<Expression<Boolean>> e = new ArrayList<Expression<Boolean>>();
		e.add(this);
		return e;
	}

	@Override
	public boolean evaluate(Object obj) {
		return isNegatedRVal(false);
	}


}
