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
import com.k2.Util.Identity.IdentityUtil;

public class PredicateIsTrue extends AbstractPredicate implements FemPredicate {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FemExpression<Boolean> bool;

	public PredicateIsTrue(FemExpression<Boolean> bool) {
		this.bool = bool;
	}

	@Override
	public boolean evaluate(Object obj) {
		if (bool == null) return false;
		logger.trace("{}({}).{} = {} returning {}", 
				obj.getClass().getSimpleName(), 
				IdentityUtil.getId(obj), 
				bool.getAlias(), 
				bool.getValue(obj), 
				isNegatedRVal(bool.getValue(obj)));
		return isNegatedRVal(bool.getValue(obj));
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(bool != null && bool instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)bool);
	}


}
