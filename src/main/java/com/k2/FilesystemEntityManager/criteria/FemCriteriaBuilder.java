package com.k2.FilesystemEntityManager.criteria;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FilesystemEntityManagerFactory;
import com.k2.FilesystemEntityManager.criteria.expression.*;

public class FemCriteriaBuilder implements CriteriaBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FilesystemEntityManagerFactory femf;

	public FemCriteriaBuilder(FilesystemEntityManagerFactory femf) {
		this.femf = femf;
	}

	@Override
	public <N extends Number> FemExpression<N> abs(Expression<N> num) {
		return new ExprAbsolute<N>((FemExpression<N>)num);
	}

	@Override
	public <Y> Expression<Y> all(Subquery<Y> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate and(Predicate... predicates) {
		FemPredicate[] fPredicates = new FemPredicate[predicates.length];
		for (int i=0; i<predicates.length; i++) {
			fPredicates[i] = (FemPredicate)predicates[i];
		}
		return new PredicateAnd(fPredicates);
	}

	@Override
	public FemPredicate and(Expression<Boolean> boolExpr1, Expression<Boolean> boolExpr2) {
		return new PredicateAnd((FemExpression<Boolean>)boolExpr1, (FemExpression<Boolean>)boolExpr2);
	}

	@Override
	public FemPredicate or(Predicate... predicates) {
		FemPredicate[] fPredicates = new FemPredicate[predicates.length];
		for (int i=0; i<predicates.length; i++) {
			fPredicates[i] = (FemPredicate)predicates[i];
		}
		return new PredicateOr(fPredicates);
	}

	@Override
	public FemPredicate or(Expression<Boolean> boolExpr1, Expression<Boolean> boolExpr2) {
		return new PredicateOr((FemExpression<Boolean>)boolExpr1, (FemExpression<Boolean>)boolExpr2);
	}

	@Override
	public <Y> Expression<Y> any(Subquery<Y> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompoundSelection<Object[]> array(Selection<?>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order asc(Expression<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<Double> avg(Expression<N> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate between(Expression<? extends Y> check,
			Expression<? extends Y> begin, Expression<? extends Y> end) {
		return new PredicateBetween((FemExpression<? extends Y>)check, (FemExpression<? extends Y>)begin, (FemExpression<? extends Y>)end);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate between(Expression<? extends Y> check, Y begin, Y end) {
		return new PredicateBetween((FemExpression<? extends Y>)check, begin, end);
	}

	@Override
	public <T> Coalesce<T> coalesce() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Expression<Y> coalesce(Expression<? extends Y> arg0, Expression<? extends Y> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Expression<Y> coalesce(Expression<? extends Y> arg0, Y arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<String> concat(Expression<String> stringExp1, Expression<String> stringExp2) {
		return new ExprConcatenate((FemExpression<String>)stringExp1, (FemExpression<String>)stringExp2);
	}

	@Override
	public FemExpression<String> concat(Expression<String> stringExp1, String string2) {
		return new ExprConcatenate((FemExpression<String>)stringExp1, string2);
	}

	@Override
	public FemExpression<String> concat(String string1, Expression<String> stringExp2) {
		return new ExprConcatenate(string1, (FemExpression<String>)stringExp2);
	}

	@Override
	public FemPredicate conjunction() {
		return new PredicateAnd();
	}

	@Override
	public <Y> CompoundSelection<Y> construct(Class<Y> arg0, Selection<?>... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<Long> count(Expression<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<Long> countDistinct(Expression<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaQuery<Object> createQuery() {
		return new FemCriteriaQuery<Object>();
	}

	@Override
	public <T> CriteriaQuery<T> createQuery(Class<T> cls) {
		return new FemCriteriaQuery<T>(cls);
	}

	@Override
	public CriteriaQuery<Tuple> createTupleQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<Date> currentDate() {
		return new ExprCurrentDate();
	}

	@Override
	public FemExpression<Time> currentTime() {
		return new ExprCurrentTime();
	}

	@Override
	public FemExpression<Timestamp> currentTimestamp() {
		return new ExprCurrentTimestamp();
	}

	@Override
	public Order desc(Expression<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> diff(Expression<? extends N> numExp1, Expression<? extends N> numExp2) {
		return new ExprDifference<N>((FemExpression<? extends N>)numExp1, (FemExpression<? extends N>)numExp2);
	}

	@Override
	public <N extends Number> Expression<N> diff(Expression<? extends N> numExp1, N num2) {
		return new ExprDifference<N>((FemExpression<? extends N>)numExp1, num2);
	}

	@Override
	public <N extends Number> Expression<N> diff(N num1, Expression<? extends N> numExp2) {
		return new ExprDifference<N>(num1, (FemExpression<? extends N>)numExp2);
	}

	@Override
	public FemPredicate disjunction() {
		return new PredicateOr();
	}

	@Override
	public FemPredicate equal(Expression<?> expr1, Expression<?> expr2) {
		return new PredicateEquals((FemExpression<?>)expr1, (FemExpression<?>)expr2);
	}

	@Override
	public FemPredicate equal(Expression<?> expr, Object obj) {
		return new PredicateEquals((FemExpression<?>)expr, obj);
	}

	@Override
	public Predicate exists(Subquery<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Expression<T> function(String arg0, Class<T> arg1, Expression<?>... arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate ge(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateGE((FemExpression<? extends Number>)numExpr1, (FemExpression<? extends Number>)numExpr2);
	}

	@Override
	public FemPredicate ge(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateGE((FemExpression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate greaterThan(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateGreaterThan((FemExpression<? extends Y>)exp1, (FemExpression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate greaterThan(Expression<? extends Y> exp, Y comp) {
		return new PredicateGreaterThan((FemExpression<? extends Y>)exp, comp);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate greaterThanOrEqualTo(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateGreaterThanOrEqualTo((FemExpression<? extends Y>)exp1, (FemExpression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate greaterThanOrEqualTo(Expression<? extends Y> exp, Y comp) {
		return new PredicateGreaterThanOrEqualTo((FemExpression<? extends Y>)exp, comp);
	}

	@Override
	public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate gt(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateGT((FemExpression<? extends Number>)numExpr1, (FemExpression<? extends Number>)numExpr2);
	}

	@Override
	public FemPredicate gt(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateGT((FemExpression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <T> In<T> in(Expression<? extends T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C extends Collection<?>> Predicate isEmpty(Expression<C> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate isFalse(Expression<Boolean> bool) {
		return new PredicateIsFalse((FemExpression<Boolean>)bool);
	}

	@Override
	public <E, C extends Collection<E>> Predicate isMember(Expression<E> arg0, Expression<C> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E, C extends Collection<E>> Predicate isMember(E arg0, Expression<C> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> arg0, Expression<C> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E, C extends Collection<E>> Predicate isNotMember(E arg0, Expression<C> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate isNotNull(Expression<?> expr) {
		return new PredicateNotNull((FemExpression<?>)expr);
	}

	@Override
	public FemPredicate isNull(Expression<?> expr) {
		return new PredicateNull((FemExpression<?>)expr);
	}

	@Override
	public FemPredicate isTrue(Expression<Boolean> bool) {
		return new PredicateIsTrue((FemExpression<Boolean>)bool);
	}

	@Override
	public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemPredicate le(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateLE((FemExpression<? extends Number>)numExpr1, (FemExpression<? extends Number>)numExpr2);
	}

	@Override
	public FemPredicate le(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateLE((FemExpression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <X extends Comparable<? super X>> Expression<X> least(Expression<X> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<Integer> length(Expression<String> string) {
		return new ExprLength((FemExpression<String>)string);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate lessThan(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateLessThan((FemExpression<? extends Y>)exp1, (FemExpression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> exp, Y comp) {
		return new PredicateLessThan((FemExpression<? extends Y>)exp, comp);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate lessThanOrEqualTo(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateLessThanOrEqualTo((FemExpression<? extends Y>)exp1, (FemExpression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> FemPredicate lessThanOrEqualTo(Expression<? extends Y> exp, Y comp) {
		return new PredicateLessThanOrEqualTo((FemExpression<? extends Y>)exp, comp);
	}

	@Override
	public FemPredicate like(Expression<String> string, Expression<String> pattern) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern);
	}

	@Override
	public FemPredicate like(Expression<String> string, String pattern) {
		return new PredicateLike((FemExpression<String>)string, pattern);
	}

	@Override
	public FemPredicate like(Expression<String> string, Expression<String> pattern, Expression<Character> escapeChar) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern, (FemExpression<Character>)escapeChar);
	}

	@Override
	public FemPredicate like(Expression<String> string, Expression<String> pattern, char escapeChar) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern, escapeChar);
	}

	@Override
	public FemPredicate like(Expression<String> string, String pattern, Expression<Character> escapeChar) {
		return new PredicateLike((FemExpression<String>)string, pattern, (FemExpression<Character>)escapeChar);
	}

	@Override
	public FemPredicate like(Expression<String> string, String pattern, char escapeChar) {
		return new PredicateLike((FemExpression<String>)string, pattern, escapeChar);
	}

	@Override
	public <T> FemExpression<T> literal(T literal) {
		return new ExprLiteral<T>(literal);
	}

	@Override
	public FemExpression<Integer> locate(Expression<String> string, Expression<String> pattern) {
		return new ExprLocate((FemExpression<String>)string, (FemExpression<String>)pattern);
	}

	@Override
	public FemExpression<Integer> locate(Expression<String> string, String pattern) {
		return new ExprLocate((FemExpression<String>)string, pattern);
	}

	@Override
	public FemExpression<Integer> locate(Expression<String> string, Expression<String> pattern, Expression<Integer> from) {
		return new ExprLocate((FemExpression<String>)string, (FemExpression<String>)pattern, (FemExpression<Integer>)from);
	}

	@Override
	public FemExpression<Integer> locate(Expression<String> string, String pattern, int from) {
		return new ExprLocate((FemExpression<String>)string, pattern, from);
	}

	@Override
	public FemExpression<String> lower(Expression<String> string) {
		return new ExprLower((FemExpression<String>)string);
	}

	@Override
	public FemPredicate lt(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateLT((FemExpression<? extends Number>)numExpr1, (FemExpression<? extends Number>)numExpr2);
	}

	@Override
	public FemPredicate lt(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateLT((FemExpression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <N extends Number> Expression<N> max(Expression<N> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> min(Expression<N> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<Integer> mod(Expression<Integer> num1, Expression<Integer> num2) {
		return new ExprMod((FemExpression<Integer>)num1, (FemExpression<Integer>)num2);
	}

	@Override
	public FemExpression<Integer> mod(Expression<Integer> num1, Integer num2) {
		return new ExprMod((FemExpression<Integer>)num1, num2);
	}

	@Override
	public FemExpression<Integer> mod(Integer num1, Expression<Integer> num2) {
		return new ExprMod(num1, (FemExpression<Integer>)num2);
	}

	@Override
	public <N extends Number> FemExpression<N> neg(Expression<N> num) {
		return new ExprNeg<N>((FemExpression<N>)num);
	}

	@Override
	public FemPredicate not(Expression<Boolean> bool) {
		return new PredicateNot((FemExpression<Boolean>)bool);
	}

	@Override
	public FemPredicate notEqual(Expression<?> exp1, Expression<?> exp2) {
		return new PredicateEquals((FemExpression<?>)exp1, (FemExpression<?>)exp2).not();
	}

	@Override
	public FemPredicate notEqual(Expression<?> exp1, Object obj) {
		return new PredicateEquals((FemExpression<?>)exp1, obj).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, Expression<String> pattern) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, String pattern) {
		return new PredicateLike((FemExpression<String>)string, pattern).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, Expression<String> pattern, Expression<Character> escapeChar) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern, (FemExpression<Character>)escapeChar).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, Expression<String> pattern, char escapeChar) {
		return new PredicateLike((FemExpression<String>)string, (FemExpression<String>)pattern, escapeChar).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, String pattern, Expression<Character> escapeChar) {
		return new PredicateLike((FemExpression<String>)string, pattern, (FemExpression<Character>)escapeChar).not();
	}

	@Override
	public FemPredicate notLike(Expression<String> string, String pattern, char escapeChar) {
		return new PredicateLike((FemExpression<String>)string, pattern, escapeChar).not();
	}

	@Override
	public <T> FemExpression<T> nullLiteral(Class<T> cls) {
		return new ExprNullLiteral<T>(cls);
	}

	@Override
	public <Y> FemExpression<Y> nullif(Expression<Y> val1, Expression<?> val2) {
		return new ExprNullIf<Y>((FemExpression<Y>) val1, (FemExpression<?>)val2);
	}

	@Override
	public <Y> FemExpression<Y> nullif(Expression<Y> val1, Y val2) {
		return new ExprNullIf<Y>((FemExpression<Y>) val1, val2);
	}

	@Override
	public <T> ParameterExpression<T> parameter(Class<T> cls) {
		return new FemParameterExpression<T>(cls);
	}

	@Override
	public <T> ParameterExpression<T> parameter(Class<T> cls, String alias) {
		return new FemParameterExpression<T>(cls, alias);
	}

	@Override
	public <N extends Number> FemExpression<N> prod(Expression<? extends N> num1, Expression<? extends N> num2) {
		return new ExprProd<N>((FemExpression<? extends N>) num1, (FemExpression<? extends N>) num2);
	}

	@Override
	public <N extends Number> FemExpression<N> prod(Expression<? extends N> num1, N num2) {
		return new ExprProd<N>((FemExpression<? extends N>) num1, num2);
	}

	@Override
	public <N extends Number> FemExpression<N> prod(N num1, Expression<? extends N> num2) {
		return new ExprProd<N>(num1, (FemExpression<? extends N>) num2);
	}

	@Override
	public FemExpression<Number> quot(Expression<? extends Number> num1, Expression<? extends Number> num2) {
		return new ExprQuot<Number>((FemExpression<? extends Number>) num1, (FemExpression<? extends Number>) num2);
	}

	@Override
	public FemExpression<Number> quot(Expression<? extends Number> num1, Number num2) {
		return new ExprQuot<Number>((FemExpression<? extends Number>) num1, num2);
	}

	@Override
	public FemExpression<Number> quot(Number num1, Expression<? extends Number> num2) {
		return new ExprQuot<Number>(num1, (FemExpression<? extends Number>) num2);
	}

	@Override
	public <R> Case<R> selectCase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C extends Collection<?>> Expression<Integer> size(Expression<C> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C extends Collection<?>> Expression<Integer> size(C arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Y> Expression<Y> some(Subquery<Y> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<Double> sqrt(Expression<? extends Number> num) {
		return new ExprSqrt((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<String> substring(Expression<String> string, Expression<Integer> from) {
		return new ExprSubstring((FemExpression<String>)string, (FemExpression<Integer>)from);
	}

	@Override
	public FemExpression<String> substring(Expression<String> string, int from) {
		return new ExprSubstring((FemExpression<String>)string, from);
	}

	@Override
	public FemExpression<String> substring(Expression<String> string, Expression<Integer> from, Expression<Integer> length) {
		return new ExprSubstring((FemExpression<String>)string, (FemExpression<Integer>)from, (FemExpression<Integer>)length);
	}

	@Override
	public FemExpression<String> substring(Expression<String> string, int from, int length) {
		return new ExprSubstring((FemExpression<String>)string, from, length);
	}

	@Override
	public <N extends Number> Expression<N> sum(Expression<N> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> sum(Expression<? extends N> arg0, Expression<? extends N> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> sum(Expression<? extends N> arg0, N arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> sum(N arg0, Expression<? extends N> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<Double> sumAsDouble(Expression<Float> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<Long> sumAsLong(Expression<Integer> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<BigDecimal> toBigDecimal(Expression<? extends Number> num) {
		return new ExprToBigDecimal((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<BigInteger> toBigInteger(Expression<? extends Number> num) {
		return new ExprToBigInteger((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<Double> toDouble(Expression<? extends Number> num) {
		return new ExprToDouble((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<Float> toFloat(Expression<? extends Number> num) {
		return new ExprToFloat((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<Integer> toInteger(Expression<? extends Number> num) {
		return new ExprToInteger((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<Long> toLong(Expression<? extends Number> num) {
		return new ExprToLong((FemExpression<? extends Number>)num);
	}

	@Override
	public FemExpression<String> toString(Expression<Character> character) {
		return new ExprToString((FemExpression<Character>)character);
	}

	@Override
	public <X, T, V extends T> Join<X, V> treat(Join<X, T> arg0, Class<V> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, T, E extends T> CollectionJoin<X, E> treat(CollectionJoin<X, T> arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, T, E extends T> SetJoin<X, E> treat(SetJoin<X, T> arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, T, E extends T> ListJoin<X, E> treat(ListJoin<X, T> arg0, Class<E> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, K, T, V extends T> MapJoin<X, K, V> treat(MapJoin<X, K, T> arg0, Class<V> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, T extends X> Path<T> treat(Path<X> arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, T extends X> Root<T> treat(Root<X> arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<String> trim(Expression<String> string) {
		return new ExprTrim((FemExpression<String>)string);
	}

	@Override
	public FemExpression<String> trim(Trimspec trimSpec, Expression<String> string) {
		return new ExprTrim(trimSpec, (FemExpression<String>)string);
	}

	@Override
	public FemExpression<String> trim(Expression<Character> trimChar, Expression<String> string) {
		return new ExprTrim((FemExpression<Character>)trimChar, (FemExpression<String>)string);
	}

	@Override
	public FemExpression<String> trim(char trimChar, Expression<String> string) {
		return new ExprTrim(trimChar, (FemExpression<String>)string);
	}

	@Override
	public FemExpression<String> trim(Trimspec trimSpec, Expression<Character> trimChar, Expression<String> string) {
		return new ExprTrim(trimSpec, (FemExpression<Character>)trimChar, (FemExpression<String>)string);
	}

	@Override
	public FemExpression<String> trim(Trimspec trimSpec, char trimChar, Expression<String> string) {
		return new ExprTrim(trimSpec, trimChar, (FemExpression<String>)string);
	}

	@Override
	public CompoundSelection<Tuple> tuple(Selection<?>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FemExpression<String> upper(Expression<String> string) {
		return new ExprUpper((FemExpression<String>) string);
	}

	@Override
	public <V, M extends Map<?, V>> Expression<Collection<V>> values(M arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
