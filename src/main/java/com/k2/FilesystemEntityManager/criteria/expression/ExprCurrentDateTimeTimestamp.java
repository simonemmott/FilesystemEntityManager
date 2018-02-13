package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

public abstract class ExprCurrentDateTimeTimestamp<D> extends AbstractExpression<D> implements FemExpression<D> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static ThreadLocal<CurrentTime> localCurrentTimes = new ThreadLocal<CurrentTime>();
	
	private CurrentTime getCurrentDateTimeTimestamp() {
		CurrentTime currentTime = localCurrentTimes.get();
		if (currentTime == null) { 
			currentTime = new CurrentTime();
			localCurrentTimes.set(currentTime);
		}
		return currentTime;
	}

	public ExprCurrentDateTimeTimestamp(Class<D> cls) {
		super(cls);
	}

	Date getCurrentDate() { return getCurrentDateTimeTimestamp().getCurrentDate(); }
	
	Time getCurrentTime() { return getCurrentDateTimeTimestamp().getCurrentTime(); }
	
	Timestamp getCurrentTimestamp() { return getCurrentDateTimeTimestamp().getCurrentTimestamp(); }
	
	/*
	@Override
	public Date getValue(Object obj) {

		if (string==null) return 0;
		logger.trace("Length: {}({}).{} is {}", 
				obj.getClass().getName(), 
				IdentityUtil.getId(obj), 
				string.getAlias(), 
				string.getValue(obj).length());
		return (string.getValue(obj).length());
	}
*/
	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
