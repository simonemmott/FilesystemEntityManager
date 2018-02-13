package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateAnd extends AbstractPredicate implements FemPredicate {

	FemPredicate[] predicates = null;
	FemExpression<Boolean> boolExpr1 = null;
	FemExpression<Boolean> boolExpr2 = null;
	
	public PredicateAnd(FemPredicate... predicates) {
		this.predicates = predicates;
	}

	public PredicateAnd(FemExpression<Boolean> boolExpr1, FemExpression<Boolean> boolExpr2) {
		this.boolExpr1 = boolExpr1;
		this.boolExpr2 = boolExpr2;
	}


	@Override
	public List<Expression<Boolean>> getExpressions() {
		List<Expression<Boolean>> e = new ArrayList<Expression<Boolean>>();
		if (predicates == null) {
			e.add(boolExpr1);
			e.add(boolExpr2);
		} else {
			for (FemPredicate p : predicates) e.add(p);
		}
		return e;
	}

	@Override
	public boolean evaluate(Object obj) {
		if (predicates == null) {
			Boolean b1 = boolExpr1.getValue(obj);
			Boolean b2 = boolExpr2.getValue(obj);
			if (b1 != null && b2 != null && b1  && b2) {
					return isNegatedRVal(true);
				} else {
					return isNegatedRVal(false);
				}
			} else {
				for (FemPredicate p : predicates) {
					if (!p.evaluate(obj)) {
						return isNegatedRVal(false);
					}
				}
				return isNegatedRVal(true);
			}
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if (predicates == null) {
			if(boolExpr1 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)boolExpr1);
			if(boolExpr2 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)boolExpr2);

		} else {
			for (FemPredicate p : predicates) {
				p.populateParameters(parameters);
			}
		}
	}

}
