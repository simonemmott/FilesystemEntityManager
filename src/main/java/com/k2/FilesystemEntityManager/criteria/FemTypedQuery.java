package com.k2.FilesystemEntityManager.criteria;

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

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.FilesystemEntityManager;
import com.k2.FilesystemEntityManager.criteria.expression.FemParameterExpression;

public class FemTypedQuery<T> implements TypedQuery<T> {
	
	FemCriteriaQuery<T> qry;
	FilesystemEntityManager fem;

	public FemTypedQuery(FilesystemEntityManager fem, FemCriteriaQuery<T> qry) {
		this.fem = fem;
		this.qry = qry;
	}
	
	public Class<T> getResultType() { return qry.getResultType(); }
	
	public boolean queryMatch(T obj) {
		
		for (FemEvaluator e : qry.getEvaluators()) {
			if (!e.evaluate(obj)) return false;
		}
		
		return true;
	}

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
	public FemParameterExpression<?> getParameter(String alias) {
		return qry.getFemQueryParameters().getParamater(alias);
	}

	@Override
	public FemParameterExpression<?> getParameter(int pos) {
		return qry.getFemQueryParameters().getParamater(pos);
	}

	@Override
	public <P> FemParameterExpression<P> getParameter(String alias, Class<P> type) {
		return qry.getFemQueryParameters().getParamater(alias, type);
	}

	@Override
	public <P> FemParameterExpression<P> getParameter(int pos, Class<P> type) {
		return qry.getFemQueryParameters().getParamater(pos, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P getParameterValue(Parameter<P> parm) {
		if (parm instanceof FemParameterExpression<?>) {
			FemParameterExpression<?> fp = (FemParameterExpression<?>)parm;
			if (fp.getParameterType().isAssignableFrom(parm.getParameterType())) {
				return (P) fp.getValue(null);
			}
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
		s.addAll(qry.getFemQueryParameters().getParameters());
		return s;
	}

	@Override
	public boolean isBound(Parameter<?> parm) {
		if (parm instanceof FemParameterExpression<?>) {
			FemParameterExpression<?> fp = (FemParameterExpression<?>)parm;
			return fp.isBound();
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
		if (parm instanceof FemParameterExpression) {
			FemParameterExpression<P> fp = (FemParameterExpression<P>)parm;
			fp.setValue(value);
		}
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(String alias, Object value) {
		FemParameterExpression<?> fp = getParameter(alias);
		if (fp != null) { 
			fp.setObjectValue(value); 
		} else {
			throw new FemError("No paramter with alias '{}' is defind", alias);
		}
		return this;
	}

	@Override
	public TypedQuery<T> setParameter(int pos, Object value) {
		FemParameterExpression<?> fp = getParameter(pos);
		if (fp != null) { 
			fp.setObjectValue(value); 
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
		FemParameterExpression<Date> fp = (FemParameterExpression<Date>)parm;
		switch(temporalType) {
		case DATE:
			fp.setValue(date);
			break;
		case TIME:
			fp.setValue(new Time(date.getTime()));
			break;
		case TIMESTAMP:
			fp.setValue(new Timestamp(date.getTime()));
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
		FemParameterExpression<Date> fp = getParameter(alias, Date.class);
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
		FemParameterExpression<Date> fp = getParameter(pos, Date.class);
		if (fp != null) { 
			return setParameter(fp, date, temporalType); 
		} else {
			throw new FemError("No date paramter with position '{}' is defind", pos);
		}
	}

}
