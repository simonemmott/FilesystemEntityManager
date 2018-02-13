package com.k2.FilesystemEntityManager.criteria;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.criteria.ParameterExpression;

import com.k2.FilesystemEntityManager.criteria.expression.FemParameterExpression;

public class FemQueryParameters {

	private Set<FemParameterExpression<?>> parameters = new HashSet<FemParameterExpression<?>>();
	
	public void add(FemParameterExpression<?> parm) {
		if (!parameters.contains(parm)) {
			parameters.add(parm);
			parm.setPosition(parameters.size());
		}
	}
	
	public Set<ParameterExpression<?>> getParameters() {
		Set<ParameterExpression<?>> ps = new HashSet<ParameterExpression<?>>();
		ps.addAll(parameters);
		return ps;
	}
	
	public void clear() {
		parameters.clear();
	}

	public FemParameterExpression<?> getParamater(String alias) {
		for (FemParameterExpression<?> p : parameters) {
			if (p.getAlias() != null && p.getAlias().equals(alias)) return p;
		}
		return null;
	}

	public FemParameterExpression<?> getParamater(int pos) {
		for (FemParameterExpression<?> p : parameters) {
			if (p.getPosition() != null && p.getPosition().equals(pos)) return p;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <P> FemParameterExpression<P> getParamater(String alias, Class<P> type) {
		for (FemParameterExpression<?> p : parameters) {
			if (p.getAlias() != null && p.getAlias().equals(alias) && p.getJavaType().equals(type)) return (FemParameterExpression<P>)p;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <P> FemParameterExpression<P> getParamater(int pos, Class<P> type) {
		for (FemParameterExpression<?> p : parameters) {
			if (p.getPosition() != null && p.getPosition().equals(pos) && p.getJavaType().equals(type)) return (FemParameterExpression<P>)p;
		}
		return null;
	}

}
