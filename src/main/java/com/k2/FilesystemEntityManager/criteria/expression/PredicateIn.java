package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateIn extends AbstractPredicate implements FemPredicate {

	FemExpression<?> expr;
	Object[] checks;
	
	public PredicateIn(FemExpression<?> expr, Object[] objects) {
		this.expr = expr;
		this.checks = objects;
	}

	public PredicateIn(FemExpression<?> expr, FemExpression<?>... expressions) {
		this.expr = expr;
		this.checks = expressions;
	}

	public PredicateIn(FemExpression<?> expr, Collection<?> collection) {
		this.expr = expr;
		this.checks = new Object[1];
		checks[0] = collection;
	}
	
	public PredicateIn(FemExpression<?> expr, Expression<Collection<?>> collectionExpr) {
		this.expr = expr;
		this.checks = new Object[1];
		checks[0] = collectionExpr;
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(expr != null && expr instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)expr);
		for (Object check : checks) {
			if(check != null && check instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)check);
		}
	}

	@Override
	public boolean evaluate(Object obj) {
		Object value = expr.getValue(obj);
		if (value == null) return false;
		if (isNegated()) {
			return !evaluateIn(value, obj);
		} else {
			return evaluateIn(value, obj);
		}
	}
	
	private boolean evaluateIn(Object value, Object obj) {
		for (Object check : checks) {
			
			if (check instanceof FemExpression) {
				FemExpression<?> exprCheck = (FemExpression<?>)check;
				Object checkValue = exprCheck.getValue(obj);
				if (checkValue instanceof Collection) {
					for (Object o : (Collection<?>)checkValue) {
						if (value.equals(o)) return true;
					}
				} else {
					if (value.equals(checkValue)) return true;
				}
				break;
			}
			
			if (check instanceof Collection) {
				for (Object o : (Collection<?>)check) {
					if (value.equals(o)) return true;
				}
				break;
			}
			
			if (value.equals(check)) return true;
			
		}
		return false;
	}


}
