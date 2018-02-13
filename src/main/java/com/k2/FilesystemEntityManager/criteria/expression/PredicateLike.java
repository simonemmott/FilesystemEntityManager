package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.criteria.FemQueryParameters;

public class PredicateLike extends AbstractPredicate implements FemPredicate {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private FemExpression<String> stringExp;
	private FemExpression<String> patternExp;
	private FemExpression<Character> escapeCharExp;
	private String pattern;
	private char escapeChar = '\'';

	public PredicateLike(FemExpression<String> stringExp, FemExpression<String> patternExp) {
		this.stringExp = stringExp;
		this.patternExp = patternExp;
	}

	public PredicateLike(FemExpression<String> stringExp, String pattern) {
		this.stringExp = stringExp;
		this.pattern = pattern;
	}

	public PredicateLike(FemExpression<String> stringExp, FemExpression<String> patternExp, FemExpression<Character> escapeCharExp) {
		this.stringExp = stringExp;
		this.patternExp = patternExp;
		this.escapeCharExp = escapeCharExp;
	}

	public PredicateLike(FemExpression<String> stringExp, FemExpression<String> patternExp, char escapeChar) {
		this.stringExp = stringExp;
		this.patternExp = patternExp;
		this.escapeChar = escapeChar;
	}

	public PredicateLike(FemExpression<String> stringExp, String pattern, FemExpression<Character> escapeCharExp) {
		this.stringExp = stringExp;
		this.pattern = pattern;
		this.escapeCharExp = escapeCharExp;
	}

	public PredicateLike(FemExpression<String> stringExp, String pattern, char escapeChar) {
		this.stringExp = stringExp;
		this.pattern = pattern;
		this.escapeChar = escapeChar;
	}

	@Override
	public boolean evaluate(Object obj) {
		
		char c = (escapeCharExp == null) ? escapeChar : escapeCharExp.getValue(obj);
		if (c != '\'') logger.warn("Custom escape characters '{}'not supported by file system entity manager, using '{}' instead", 
				c,
				escapeChar);
		
		Pattern p = Pattern.compile(((patternExp == null) ? pattern : patternExp.getValue(obj)).replaceAll("%", "(.*)"));
		Matcher m = p.matcher(stringExp.getValue(obj));

		logger.trace("Matching '{}' in '{}'{}", 
				p.pattern(),
				stringExp.getValue(obj),
				(m.matches() ? " [Matched]": ""));
		
		return isNegatedRVal(m.matches());
	}

	@Override
	public void populateParameters(FemQueryParameters parameters) {
		if(stringExp != null && stringExp instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)stringExp);
		if(patternExp != null && patternExp instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)patternExp);
		if(escapeCharExp != null && escapeCharExp instanceof FemParameterExpression<?>) parameters.add((FemParameterExpression<?>)escapeCharExp);
	}

}
