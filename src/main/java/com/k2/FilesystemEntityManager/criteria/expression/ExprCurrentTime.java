package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.sql.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprCurrentTime extends ExprCurrentDateTimeTimestamp<Time> implements FemExpression<Time> {
	
	public ExprCurrentTime() {
		super(Time.class);
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Time getValue(Object obj) {
		return getCurrentTime();
	}


}
