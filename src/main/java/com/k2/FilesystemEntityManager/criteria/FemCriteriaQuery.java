package com.k2.FilesystemEntityManager.criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.criteria.expression.FemExpression;
import com.k2.FilesystemEntityManager.criteria.expression.FemParameterExpression;
import com.k2.FilesystemEntityManager.criteria.expression.FemPredicate;

public class FemCriteriaQuery<T> implements CriteriaQuery<T> {

	private Class<T> cls;
	private Set<Root<T>> roots = new HashSet<Root<T>>();
	private boolean distinct = false;
	
	public FemCriteriaQuery() {
	}
	
	public FemCriteriaQuery(Class<T> cls) {
		this.cls = cls;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Root<T> from(Class cls) {
		if (this.cls == null) this.cls = cls;
		if (!cls.isAssignableFrom(this.cls)) throw new FemError("Criteria query type missmatch. Expecting '{}' got '{}'", this.cls.getCanonicalName(), cls.getCanonicalName());
		Root<T> root = new FemRoot<T>(cls);
		roots.add(root);
		return root;
	}

	@Override
	public Root from(EntityType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getGroupList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Predicate getGroupRestriction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<T> getResultType() {
		return cls;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set getRoots() {
		return roots;
	}
	
	@Override
	public boolean isDistinct() {
		return distinct;
	}

	@Override
	public Predicate getRestriction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> Subquery<U> subquery(Class<U> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public CriteriaQuery distinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	@Override
	public List getOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	private FemQueryParameters queryParameters = new FemQueryParameters();
	
	FemQueryParameters getFemQueryParameters() {
		return queryParameters;
	}
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		return queryParameters.getParameters();
	}

	@Override
	public CriteriaQuery groupBy(Expression... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery groupBy(List arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery having(Expression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery having(Predicate... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery multiselect(Selection... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery multiselect(List arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery orderBy(Order... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery orderBy(List arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private Selection<T> selection;

	@Override
	public Selection<T> getSelection() {
		return selection;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public CriteriaQuery<T> select(Selection selection) {
		this.selection = selection;
		return this;
	}
	
	private List<FemEvaluator> evaluators = new ArrayList<FemEvaluator>();
	List<FemEvaluator> getEvaluators() { return evaluators; }

	@SuppressWarnings("rawtypes")
	@Override
	public CriteriaQuery<T> where(Expression expr) {
		evaluators.clear();
		queryParameters.clear();
		if (expr instanceof FemParameterExpression) {
			queryParameters.add((FemParameterExpression<?>)expr);
		}
		if (expr instanceof FemPredicate) {
			((FemPredicate)expr).populateParameters(queryParameters);
		}
		evaluators.add((FemExpression)expr);
		return this;
	}

	@Override
	public CriteriaQuery<T> where(Predicate... predicates) {
		evaluators.clear();
		queryParameters.clear();
		for (Predicate p : predicates) {
			if (p instanceof FemPredicate) {
				FemPredicate fp = (FemPredicate)p;
				fp.populateParameters(queryParameters);
				evaluators.add(fp);
			}
		}
		return this;
	}

}
