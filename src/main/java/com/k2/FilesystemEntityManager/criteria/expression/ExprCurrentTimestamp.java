package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprCurrentTimestamp extends ExprCurrentDateTimeTimestamp<Timestamp> implements FemExpression<Timestamp> {
	
	public ExprCurrentTimestamp() {
		super(Timestamp.class);
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Timestamp getValue(Object obj) {
		return getCurrentTimestamp();
	}


}
