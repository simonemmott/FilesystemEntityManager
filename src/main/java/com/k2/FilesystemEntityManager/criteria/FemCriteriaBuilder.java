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

import com.k2.Expressions.expression.*;
import com.k2.Expressions.predicate.*;
import com.k2.FilesystemEntityManager.FilesystemEntityManagerFactory;

public class FemCriteriaBuilder implements CriteriaBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FilesystemEntityManagerFactory femf;

	public FemCriteriaBuilder(FilesystemEntityManagerFactory femf) {
		this.femf = femf;
	}

	@Override
	public <N extends Number> K2Expression<N> abs(Expression<N> num) {
		return new ExprAbsolute<N>((K2Expression<N>)num);
	}

	@Override
	public <Y> Expression<Y> all(Subquery<Y> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K2Predicate and(Predicate... predicates) {
		K2Predicate[] fPredicates = new K2Predicate[predicates.length];
		for (int i=0; i<predicates.length; i++) {
			fPredicates[i] = (K2Predicate)predicates[i];
		}
		return new PredicateAnd(fPredicates);
	}

	@Override
	public K2Predicate and(Expression<Boolean> boolExpr1, Expression<Boolean> boolExpr2) {
		return new PredicateAnd((K2Expression<Boolean>)boolExpr1, (K2Expression<Boolean>)boolExpr2);
	}

	@Override
	public K2Predicate or(Predicate... predicates) {
		K2Predicate[] fPredicates = new K2Predicate[predicates.length];
		for (int i=0; i<predicates.length; i++) {
			fPredicates[i] = (K2Predicate)predicates[i];
		}
		return new PredicateOr(fPredicates);
	}

	@Override
	public K2Predicate or(Expression<Boolean> boolExpr1, Expression<Boolean> boolExpr2) {
		return new PredicateOr((K2Expression<Boolean>)boolExpr1, (K2Expression<Boolean>)boolExpr2);
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
	public <Y extends Comparable<? super Y>> K2Predicate between(Expression<? extends Y> check,
			Expression<? extends Y> begin, Expression<? extends Y> end) {
		return new PredicateBetween((K2Expression<? extends Y>)check, (K2Expression<? extends Y>)begin, (K2Expression<? extends Y>)end);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate between(Expression<? extends Y> check, Y begin, Y end) {
		return new PredicateBetween((K2Expression<? extends Y>)check, begin, end);
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
	public K2Expression<String> concat(Expression<String> stringExp1, Expression<String> stringExp2) {
		return new ExprConcatenate((K2Expression<String>)stringExp1, (K2Expression<String>)stringExp2);
	}

	@Override
	public K2Expression<String> concat(Expression<String> stringExp1, String string2) {
		return new ExprConcatenate((K2Expression<String>)stringExp1, string2);
	}

	@Override
	public K2Expression<String> concat(String string1, Expression<String> stringExp2) {
		return new ExprConcatenate(string1, (K2Expression<String>)stringExp2);
	}

	@Override
	public K2Predicate conjunction() {
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
	public K2Expression<Date> currentDate() {
		return new ExprCurrentSqlDate();
	}

	@Override
	public K2Expression<Time> currentTime() {
		return new ExprCurrentTime();
	}

	@Override
	public K2Expression<Timestamp> currentTimestamp() {
		return new ExprCurrentTimestamp();
	}

	@Override
	public Order desc(Expression<?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends Number> Expression<N> diff(Expression<? extends N> numExp1, Expression<? extends N> numExp2) {
		return new ExprDifference<N>((K2Expression<? extends N>)numExp1, (K2Expression<? extends N>)numExp2);
	}

	@Override
	public <N extends Number> Expression<N> diff(Expression<? extends N> numExp1, N num2) {
		return new ExprDifference<N>((K2Expression<? extends N>)numExp1, num2);
	}

	@Override
	public <N extends Number> Expression<N> diff(N num1, Expression<? extends N> numExp2) {
		return new ExprDifference<N>(num1, (K2Expression<? extends N>)numExp2);
	}

	@Override
	public K2Predicate disjunction() {
		return new PredicateOr();
	}

	@Override
	public K2Predicate equal(Expression<?> expr1, Expression<?> expr2) {
		return new PredicateEquals((K2Expression<?>)expr1, (K2Expression<?>)expr2);
	}

	@Override
	public K2Predicate equal(Expression<?> expr, Object obj) {
		return new PredicateEquals((K2Expression<?>)expr, obj);
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
	public K2Predicate ge(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateGE((K2Expression<? extends Number>)numExpr1, (K2Expression<? extends Number>)numExpr2);
	}

	@Override
	public K2Predicate ge(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateGE((K2Expression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate greaterThan(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateGreaterThan((K2Expression<? extends Y>)exp1, (K2Expression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate greaterThan(Expression<? extends Y> exp, Y comp) {
		return new PredicateGreaterThan((K2Expression<? extends Y>)exp, comp);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate greaterThanOrEqualTo(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateGreaterThanOrEqualTo((K2Expression<? extends Y>)exp1, (K2Expression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate greaterThanOrEqualTo(Expression<? extends Y> exp, Y comp) {
		return new PredicateGreaterThanOrEqualTo((K2Expression<? extends Y>)exp, comp);
	}

	@Override
	public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K2Predicate gt(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateGT((K2Expression<? extends Number>)numExpr1, (K2Expression<? extends Number>)numExpr2);
	}

	@Override
	public K2Predicate gt(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateGT((K2Expression<? extends Number>)numExpr1, num2);
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
	public K2Predicate isFalse(Expression<Boolean> bool) {
		return new PredicateIsFalse((K2Expression<Boolean>)bool);
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
	public K2Predicate isNotNull(Expression<?> expr) {
		return new PredicateNotNull((K2Expression<?>)expr);
	}

	@Override
	public K2Predicate isNull(Expression<?> expr) {
		return new PredicateNull((K2Expression<?>)expr);
	}

	@Override
	public K2Predicate isTrue(Expression<Boolean> bool) {
		return new PredicateIsTrue((K2Expression<Boolean>)bool);
	}

	@Override
	public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K2Predicate le(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateLE((K2Expression<? extends Number>)numExpr1, (K2Expression<? extends Number>)numExpr2);
	}

	@Override
	public K2Predicate le(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateLE((K2Expression<? extends Number>)numExpr1, num2);
	}

	@Override
	public <X extends Comparable<? super X>> Expression<X> least(Expression<X> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K2Expression<Integer> length(Expression<String> string) {
		return new ExprLength((K2Expression<String>)string);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate lessThan(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateLessThan((K2Expression<? extends Y>)exp1, (K2Expression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> exp, Y comp) {
		return new PredicateLessThan((K2Expression<? extends Y>)exp, comp);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate lessThanOrEqualTo(Expression<? extends Y> exp1,
			Expression<? extends Y> exp2) {
		return new PredicateLessThanOrEqualTo((K2Expression<? extends Y>)exp1, (K2Expression<? extends Y>)exp2);
	}

	@Override
	public <Y extends Comparable<? super Y>> K2Predicate lessThanOrEqualTo(Expression<? extends Y> exp, Y comp) {
		return new PredicateLessThanOrEqualTo((K2Expression<? extends Y>)exp, comp);
	}

	@Override
	public K2Predicate like(Expression<String> string, Expression<String> pattern) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern);
	}

	@Override
	public K2Predicate like(Expression<String> string, String pattern) {
		return new PredicateLike((K2Expression<String>)string, pattern);
	}

	@Override
	public K2Predicate like(Expression<String> string, Expression<String> pattern, Expression<Character> escapeChar) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern, (K2Expression<Character>)escapeChar);
	}

	@Override
	public K2Predicate like(Expression<String> string, Expression<String> pattern, char escapeChar) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern, escapeChar);
	}

	@Override
	public K2Predicate like(Expression<String> string, String pattern, Expression<Character> escapeChar) {
		return new PredicateLike((K2Expression<String>)string, pattern, (K2Expression<Character>)escapeChar);
	}

	@Override
	public K2Predicate like(Expression<String> string, String pattern, char escapeChar) {
		return new PredicateLike((K2Expression<String>)string, pattern, escapeChar);
	}

	@Override
	public <T> K2Expression<T> literal(T literal) {
		return new ExprLiteral<T>(literal);
	}

	@Override
	public K2Expression<Integer> locate(Expression<String> string, Expression<String> pattern) {
		return new ExprLocate((K2Expression<String>)string, (K2Expression<String>)pattern);
	}

	@Override
	public K2Expression<Integer> locate(Expression<String> string, String pattern) {
		return new ExprLocate((K2Expression<String>)string, pattern);
	}

	@Override
	public K2Expression<Integer> locate(Expression<String> string, Expression<String> pattern, Expression<Integer> from) {
		return new ExprLocate((K2Expression<String>)string, (K2Expression<String>)pattern, (K2Expression<Integer>)from);
	}

	@Override
	public K2Expression<Integer> locate(Expression<String> string, String pattern, int from) {
		return new ExprLocate((K2Expression<String>)string, pattern, from);
	}

	@Override
	public K2Expression<String> lower(Expression<String> string) {
		return new ExprLower((K2Expression<String>)string);
	}

	@Override
	public K2Predicate lt(Expression<? extends Number> numExpr1, Expression<? extends Number> numExpr2) {
		return new PredicateLT((K2Expression<? extends Number>)numExpr1, (K2Expression<? extends Number>)numExpr2);
	}

	@Override
	public K2Predicate lt(Expression<? extends Number> numExpr1, Number num2) {
		return new PredicateLT((K2Expression<? extends Number>)numExpr1, num2);
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
	public K2Expression<Integer> mod(Expression<Integer> num1, Expression<Integer> num2) {
		return new ExprMod((K2Expression<Integer>)num1, (K2Expression<Integer>)num2);
	}

	@Override
	public K2Expression<Integer> mod(Expression<Integer> num1, Integer num2) {
		return new ExprMod((K2Expression<Integer>)num1, num2);
	}

	@Override
	public K2Expression<Integer> mod(Integer num1, Expression<Integer> num2) {
		return new ExprMod(num1, (K2Expression<Integer>)num2);
	}

	@Override
	public <N extends Number> K2Expression<N> neg(Expression<N> num) {
		return new ExprNeg<N>((K2Expression<N>)num);
	}

	@Override
	public K2Predicate not(Expression<Boolean> bool) {
		return new PredicateNot((K2Expression<Boolean>)bool);
	}

	@Override
	public K2Predicate notEqual(Expression<?> exp1, Expression<?> exp2) {
		return new PredicateEquals((K2Expression<?>)exp1, (K2Expression<?>)exp2).not();
	}

	@Override
	public K2Predicate notEqual(Expression<?> exp1, Object obj) {
		return new PredicateEquals((K2Expression<?>)exp1, obj).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, Expression<String> pattern) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, String pattern) {
		return new PredicateLike((K2Expression<String>)string, pattern).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, Expression<String> pattern, Expression<Character> escapeChar) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern, (K2Expression<Character>)escapeChar).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, Expression<String> pattern, char escapeChar) {
		return new PredicateLike((K2Expression<String>)string, (K2Expression<String>)pattern, escapeChar).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, String pattern, Expression<Character> escapeChar) {
		return new PredicateLike((K2Expression<String>)string, pattern, (K2Expression<Character>)escapeChar).not();
	}

	@Override
	public K2Predicate notLike(Expression<String> string, String pattern, char escapeChar) {
		return new PredicateLike((K2Expression<String>)string, pattern, escapeChar).not();
	}

	@Override
	public <T> K2Expression<T> nullLiteral(Class<T> cls) {
		return new ExprNullLiteral<T>(cls);
	}

	@Override
	public <Y> K2Expression<Y> nullif(Expression<Y> val1, Expression<?> val2) {
		return new ExprNullIf<Y>((K2Expression<Y>) val1, (K2Expression<?>)val2);
	}

	@Override
	public <Y> K2Expression<Y> nullif(Expression<Y> val1, Y val2) {
		return new ExprNullIf<Y>((K2Expression<Y>) val1, val2);
	}

	@Override
	public <T> ParameterExpression<T> parameter(Class<T> cls) {
		return new K2ParameterExpression<T>(cls, "");
	}

	@Override
	public <T> ParameterExpression<T> parameter(Class<T> cls, String alias) {
		return new K2ParameterExpression<T>(cls, alias);
	}

	@Override
	public <N extends Number> K2Expression<N> prod(Expression<? extends N> num1, Expression<? extends N> num2) {
		return new ExprProd<N>((K2Expression<? extends N>) num1, (K2Expression<? extends N>) num2);
	}

	@Override
	public <N extends Number> K2Expression<N> prod(Expression<? extends N> num1, N num2) {
		return new ExprProd<N>((K2Expression<? extends N>) num1, num2);
	}

	@Override
	public <N extends Number> K2Expression<N> prod(N num1, Expression<? extends N> num2) {
		return new ExprProd<N>(num1, (K2Expression<? extends N>) num2);
	}

	@Override
	public K2Expression<Number> quot(Expression<? extends Number> num1, Expression<? extends Number> num2) {
		return new ExprQuot<Number>((K2Expression<? extends Number>) num1, (K2Expression<? extends Number>) num2);
	}

	@Override
	public K2Expression<Number> quot(Expression<? extends Number> num1, Number num2) {
		return new ExprQuot<Number>((K2Expression<? extends Number>) num1, num2);
	}

	@Override
	public K2Expression<Number> quot(Number num1, Expression<? extends Number> num2) {
		return new ExprQuot<Number>(num1, (K2Expression<? extends Number>) num2);
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
	public K2Expression<Double> sqrt(Expression<? extends Number> num) {
		return new ExprSqrt((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<String> substring(Expression<String> string, Expression<Integer> from) {
		return new ExprSubstring((K2Expression<String>)string, (K2Expression<Integer>)from);
	}

	@Override
	public K2Expression<String> substring(Expression<String> string, int from) {
		return new ExprSubstring((K2Expression<String>)string, from);
	}

	@Override
	public K2Expression<String> substring(Expression<String> string, Expression<Integer> from, Expression<Integer> length) {
		return new ExprSubstring((K2Expression<String>)string, (K2Expression<Integer>)from, (K2Expression<Integer>)length);
	}

	@Override
	public K2Expression<String> substring(Expression<String> string, int from, int length) {
		return new ExprSubstring((K2Expression<String>)string, from, length);
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
	public K2Expression<BigDecimal> toBigDecimal(Expression<? extends Number> num) {
		return new ExprToBigDecimal((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<BigInteger> toBigInteger(Expression<? extends Number> num) {
		return new ExprToBigInteger((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<Double> toDouble(Expression<? extends Number> num) {
		return new ExprToDouble((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<Float> toFloat(Expression<? extends Number> num) {
		return new ExprToFloat((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<Integer> toInteger(Expression<? extends Number> num) {
		return new ExprToInteger((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<Long> toLong(Expression<? extends Number> num) {
		return new ExprToLong((K2Expression<? extends Number>)num);
	}

	@Override
	public K2Expression<String> toString(Expression<Character> character) {
		return new ExprToString((K2Expression<Character>)character);
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
	public K2Expression<String> trim(Expression<String> string) {
		return new ExprTrim((K2Expression<String>)string);
	}

	@Override
	public K2Expression<String> trim(Trimspec trimSpec, Expression<String> string) {
		return new ExprTrim(trimSpec, (K2Expression<String>)string);
	}

	@Override
	public K2Expression<String> trim(Expression<Character> trimChar, Expression<String> string) {
		return new ExprTrim((K2Expression<Character>)trimChar, (K2Expression<String>)string);
	}

	@Override
	public K2Expression<String> trim(char trimChar, Expression<String> string) {
		return new ExprTrim(trimChar, (K2Expression<String>)string);
	}

	@Override
	public K2Expression<String> trim(Trimspec trimSpec, Expression<Character> trimChar, Expression<String> string) {
		return new ExprTrim(trimSpec, (K2Expression<Character>)trimChar, (K2Expression<String>)string);
	}

	@Override
	public K2Expression<String> trim(Trimspec trimSpec, char trimChar, Expression<String> string) {
		return new ExprTrim(trimSpec, trimChar, (K2Expression<String>)string);
	}

	@Override
	public CompoundSelection<Tuple> tuple(Selection<?>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K2Expression<String> upper(Expression<String> string) {
		return new ExprUpper((K2Expression<String>) string);
	}

	@Override
	public <V, M extends Map<?, V>> Expression<Collection<V>> values(M arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
