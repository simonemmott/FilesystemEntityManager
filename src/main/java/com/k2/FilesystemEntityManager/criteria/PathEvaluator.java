package com.k2.FilesystemEntityManager.criteria;

import com.k2.Expressions.Evaluator;
import com.k2.Expressions.expression.GetterExpression;

/**
 * Getter evaluators provide values for getter expressions
 * 
 * @author simon
 *
 */
public interface PathEvaluator extends Evaluator{

	/**
	 * Get the value for the given getter expression
	 * @param getterExpression	the getter expression for which to return the value
	 * @return	The value of the getter expression as evaluated by this evaluator
	 * @param <T> The type of the value returned
	 */
	public <T> T valueOf(FemPath<T> path);

}
