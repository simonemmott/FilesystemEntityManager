package com.k2.FilesystemEntityManager.criteria.expression;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Util.Identity.IdentityUtil;

public class ExprLength extends AbstractExpression<Integer> implements FemExpression<Integer> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	FemExpression<String> string;

	public ExprLength(FemExpression<String> string) {
		super(Integer.class);
		this.string = string;
	}

	@Override
	public Integer getValue(Object obj) {

		if (string==null) return 0;
		logger.trace("Length: {}({}).{} is {}", 
				obj.getClass().getName(), 
				IdentityUtil.getId(obj), 
				string.getAlias(), 
				string.getValue(obj).length());
		return (string.getValue(obj).length());
	}

	@Override
	public boolean evaluate(Object obj) {
		return false;
	}


}
