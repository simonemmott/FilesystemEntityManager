package com.k2.FilesystemEntityManager.criteria;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.ParameterExpression;

import com.k2.Expressions.ParameterEvaluator;
import com.k2.Expressions.expression.CurrentTime;
import com.k2.Expressions.expression.K2ParameterExpression;
import com.k2.Expressions.predicate.K2Predicate;

public class FemQueryParameters implements ParameterEvaluator{

	private Set<K2ParameterExpression<?>> parameters = new HashSet<K2ParameterExpression<?>>();
	
	public void add(K2ParameterExpression<?> parm) {
		if (!parameters.contains(parm)) {
			parameters.add(parm);
			parm.setPosition(parameters.size());
		}
	}
	
	public Set<K2ParameterExpression<?>> getParameters() {
		return parameters;
	}
	
	public void clear() {
		parameters.clear();
	}

	public K2ParameterExpression<?> getParamater(String alias) {
		for (K2ParameterExpression<?> p : parameters) {
			if (p.getAlias() != null && p.getAlias().equals(alias)) return p;
		}
		return null;
	}

	public K2ParameterExpression<?> getParamater(int pos) {
		for (K2ParameterExpression<?> p : parameters) {
			if (p.getPosition() != null && p.getPosition().equals(pos)) return p;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <P> K2ParameterExpression<P> getParamater(String alias, Class<P> type) {
		for (K2ParameterExpression<?> p : parameters) {
			if (p.getAlias() != null && p.getAlias().equals(alias) && p.getJavaType().equals(type)) return (K2ParameterExpression<P>)p;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <P> K2ParameterExpression<P> getParamater(int pos, Class<P> type) {
		for (K2ParameterExpression<?> p : parameters) {
			if (p.getPosition() != null && p.getPosition().equals(pos) && p.getJavaType().equals(type)) return (K2ParameterExpression<P>)p;
		}
		return null;
	}

	@Override
	public boolean checkParametersSet(K2Predicate... predicates) {
		return false;
	}

	@Override
	public CurrentTime getCurrentTime() {
		return null;
	}

	@Override
	public <T> T valueOf(K2ParameterExpression<T> parameterExpression) {
		return null;
	}

}
