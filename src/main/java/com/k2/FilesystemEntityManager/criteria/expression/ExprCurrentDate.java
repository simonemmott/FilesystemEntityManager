package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprCurrentDate extends ExprCurrentDateTimeTimestamp<Date> implements FemExpression<Date> {
	
	public ExprCurrentDate() {
		super(Date.class);
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Date getValue(Object obj) {
		return getCurrentDate();
	}


}
