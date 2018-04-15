package com.k2.FilesystemEntityManager;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

import com.k2.Expressions.ParameterEvaluator;
import com.k2.Expressions.criteria.CriteriaQueryImpl;
import com.k2.Expressions.criteria.PathEvaluator;
import com.k2.Expressions.criteria.PathImpl;
import com.k2.Expressions.evaluators.SimpleParameterEvaluator;
import com.k2.Expressions.expression.CurrentTime;
import com.k2.Expressions.expression.K2ParameterExpression;
import com.k2.Expressions.predicate.K2Predicate;

public class FemTypedQuery<T> extends SimpleParameterEvaluator implements TypedQuery<T>, ParameterEvaluator, PathEvaluator{
	
	CriteriaQueryImpl<T> qry;
	FilesystemEntityManager fem;

	public FemTypedQuery(FilesystemEntityManager fem, CriteriaQueryImpl<T> qry) {
		this.fem = fem;
		this.qry = qry;
		for (K2ParameterExpression<?> p : qry.getQueryParameters().getParameters()) {
			this.add(p);
		}
	}
	
	public Class<T> getResultType() { return qry.getResultType(); }
	
	@Override
	public int executeUpdate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFirstResult() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FlushModeType getFlushMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getHints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockModeType getLockMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxResults() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public K2ParameterExpression<?> getParameter(String alias) {
		return qry.getQueryParameters().getParamater(alias);
	}

	@Override
	public K2ParameterExpression<?> getParameter(int pos) {
		return qry.getQueryParameters().getParamater(pos);
	}

	@Override
	public <P> K2ParameterExpression<P> getParameter(String alias, Class<P> type) {
		return qry.getQueryParameters().getParamater(alias, type);
	}

	@Override
	public <P> K2ParameterExpression<P> getParameter(int pos, Class<P> type) {
		return qry.getQueryParameters().getParamater(pos, type);
	}

	@Override
	public <P> P getParameterValue(Parameter<P> parm) {
		if (parm instanceof K2ParameterExpression<?>) {
			K2ParameterExpression<P> pe = (K2ParameterExpression<P>)parm;
			return valueOf(pe);
		}
		return null;
	}

	@Override
	public Object getParameterValue(String alias) {
		return getParameterValue(getParameter(alias));
	}

	@Override
	public Object getParameterValue(int pos) {
		return getParameterValue(getParameter(pos));
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		Set<Parameter<?>> s = new HashSet<Parameter<?>>();
		s.addAll(qry.getQueryParameters().getParameters());
		return s;
	}

	@Override
	public boolean isBound(Parameter<?> parm) {
		if (parm instanceof K2ParameterExpression<?>) {
			K2ParameterExpression<?> pe = (K2ParameterExpression<?>)parm;
			return parameterValues.containsKey(pe);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U unwrap(Class<U> cls) {
		if (!cls.isAssignableFrom(FemTypedQuery.class)) throw new FemError("Unsupported TypeQuery class. Expected {}", FemTypedQuery.class.getCanonicalName());
		return (U)this;
	}

	@Override
	public List<T> getResultList() {
		return fem.getResultsList(this);
	}

	@Override
	public T getSingleResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedQuery<T> setFirstResult(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedQuery<T> setFlushMode(FlushModeType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedQuery<T> setHint(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedQuery<T> setLockMode(LockModeType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedQuery<T> setMaxResults(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> TypedQuery<T> setParameter(Parameter<P> parm, P value) {
		if (parm instanceof K2ParameterExpression) {
			set((K2ParameterExpression<P>)parm, value);
		}
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(String alias, Object value) {
		setRawParameter(alias, value);
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(int pos, Object value) {
		K2ParameterExpression<?> pe = getParameter(pos);
		if (pe != null) { 
			setRawParameter(pe, value); 
		} else {
			throw new FemError("No paramter with postion '{}' is defind", pos);
		}
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(Parameter<Calendar> parm, Calendar cal, TemporalType temporalType) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(Parameter<Date> parm, Date date, TemporalType temporalType) {
		K2ParameterExpression<Date> pe = (K2ParameterExpression<Date>)parm;
		switch(temporalType) {
		case DATE:
			setRawParameter(pe, date);
			break;
		case TIME:
			setRawParameter(pe, new Time(date.getTime()));
			break;
		case TIMESTAMP:
			setRawParameter(pe, new Timestamp(date.getTime()));
			break;
		}
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(String arg0, Calendar arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(String alias, Date date, TemporalType temporalType) {
		K2ParameterExpression<Date> fp = getParameter(alias, Date.class);
		if (fp != null) { 
			return setParameter(fp, date, temporalType); 
		} else {
			throw new FemError("No date paramter with alias '{}' is defind", alias);
		}
	}

	@Override
	public TypedQuery<T> setParameter(int arg0, Calendar arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(int pos, Date date, TemporalType temporalType) {
		K2ParameterExpression<Date> fp = getParameter(pos, Date.class);
		if (fp != null) { 
			return setParameter(fp, date, temporalType); 
		} else {
			throw new FemError("No date paramter with position '{}' is defind", pos);
		}
	}

	T matchRoot = null;
	
	public boolean queryMatch(T obj) {
		Predicate p = qry.getRestriction();
		if (p == null) return true;
		if (p instanceof K2Predicate) {
			K2Predicate kp = (K2Predicate)p;
			matchRoot = obj;
			boolean matched = kp.evaluate(this);
			matchRoot = null;
			return matched;
		}
		
		throw new FemError("The FemTypedQuery can only evaluate K2 Predicates");
	}


	@Override
	public <V> V valueOf(PathImpl<V> path) {
		return path.getValueFromRoot(matchRoot);
	}


}
