package com.k2.FilesystemEntityManager.criteria.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateTrue extends AbstractPredicate implements Predicate {

	@Override
	public void populateParameters(FemQueryParameters parameters) {}

	@Override
	public boolean evaluate(Object obj) {
		if (this.isNegated()) return false;
		return true;
	}


}
