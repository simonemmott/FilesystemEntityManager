package com.k2.FilesystemEntityManager.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import javax.persistence.EntityManager;

public interface KeyLinkProxyController<D,E> extends LinkProxyController<D,E>, InvocationHandler {
	
	public KeyLinkProxyController<D,E> bind(Field ... keyFields);
	
	public static <A,B> KeyLinkProxyController<A,B> createController(EntityManager em, A declaringInstance, Class<B> linkToClass) {
		return new KeyLinkProxyControllerImpl<A,B>(em, declaringInstance, linkToClass);
	}

}
