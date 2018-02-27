package com.k2.FilesystemEntityManager.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.k2.FilesystemEntityManager.util.KeyUtil;

public class KeyLinkProxyControllerImpl<D,E> implements KeyLinkProxyController<D, E> {
	
	private enum State{
		FETCHED,
		UNFTCHED
	}

	private D declaringInstance;
	private EntityManager em;
	private Field[] keyFields;
	private E proxyTo;
	private Class<E> proxyOf;
	private State state = State.UNFTCHED;
	
	KeyLinkProxyControllerImpl(EntityManager em, D declaringInstance, Class<E> proxyOf) {
		this.em = em;
		this.declaringInstance = declaringInstance;
		this.proxyOf = proxyOf;
	}
	
	@Override
	public E unwrap() {
		if (state == State.FETCHED) return proxyTo;
		
		proxyTo = em.find(proxyOf, KeyUtil.keyFor(proxyOf, declaringInstance, keyFields));
		
		state = State.FETCHED;
		
		return proxyTo;
	}

	@Override
	public KeyLinkProxyController<D,E> bind(Field... keyFields) {
		this.keyFields = keyFields;
		return this;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {		
		if (unwrap() == null) return null;
		return method.invoke(proxyTo, args);
	}

}
