package com.k2.FilesystemEntityManager.criteria.expression;

import javax.persistence.criteria.Expression;

import com.k2.FilesystemEntityManager.criteria.FemEvaluator;

public interface FemExpression<T> extends Expression<T>, FemEvaluator {
	
	public T getValue(Object obj); 

}
