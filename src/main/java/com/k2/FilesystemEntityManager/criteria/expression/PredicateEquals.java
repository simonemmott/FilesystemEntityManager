package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateEquals extends AbstractPredicate implements FemPredicate {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FemExpression<?> value1;
	private Object value2;

	public PredicateEquals(FemExpression<?> expr1, FemExpression<?> expr2) {
		value1 = expr1;
		value2 = expr2;
	}

	public PredicateEquals(FemExpression<?> expr, Object obj) {
		value1 = expr;
		value2 = obj;
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(value1 != null && value1 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)value1);
		if(value2 != null && value2 instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)value2);
	}

	@Override
	public boolean evaluate(Object obj) {
		if (value1 == null ||  value2 == null) return isNegatedRVal(false);
		Object v1 = value1.getValue(obj);
		if (v1 == null) return isNegatedRVal(false);
		Object v2;
		
		if (value2 instanceof FemExpression) {
			v2 = ((FemExpression<?>)value2).getValue(obj);
		} else {
			v2 = value2;
		}
		
		logger.trace("Equals: '{}' = '{}' [{}]", v1, v2, v1.equals(v2));
		
		return isNegatedRVal(v1.equals(v2));
	}

}
