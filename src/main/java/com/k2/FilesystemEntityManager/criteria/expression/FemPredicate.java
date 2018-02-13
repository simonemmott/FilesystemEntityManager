package com.k2.FilesystemEntityManager.criteria.expression;

import javax.persistence.criteria.Predicate;
import com.k2.FilesystemEntityManager.criteria.FemEvaluator;
import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public interface FemPredicate extends Predicate, FemExpression<Boolean>, FemEvaluator {

	void populateParameters(FemQueryParameters queryParameters);
	
}
