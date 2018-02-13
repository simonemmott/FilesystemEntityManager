package com.k2.FilesystemEntityManager.criteria.expression;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class CurrentTime {
	
	private java.util.Date utilDate;
	
	public CurrentTime() {
		utilDate = Calendar.getInstance().getTime();
	}
	
	public Date getCurrentDate() { return new Date(utilDate.getTime()); }
	public Time getCurrentTime() { return new Time(utilDate.getTime()); }
	public Timestamp getCurrentTimestamp() { return new Timestamp(utilDate.getTime()); }

}
