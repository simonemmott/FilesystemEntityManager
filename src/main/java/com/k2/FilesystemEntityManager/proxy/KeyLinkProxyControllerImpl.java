package com.k2.FilesystemEntityManager.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.k2.Util.KeyUtil;
import com.k2.Util.ObjectUtil;

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
	public E unwrap(E proxy) {
		if (state == State.FETCHED) return proxyTo;
		
		proxyTo = em.find(proxyOf, KeyUtil.keyFor(proxyOf, declaringInstance, keyFields));
		
		state = State.FETCHED;
		
		if (proxyTo != null) 
			ObjectUtil.copy(proxyTo, proxy);
		
		return proxyTo;
	}

	@Override
	public KeyLinkProxyController<D,E> bind(Field... keyFields) {
		this.keyFields = keyFields;
		return this;
	}

}
