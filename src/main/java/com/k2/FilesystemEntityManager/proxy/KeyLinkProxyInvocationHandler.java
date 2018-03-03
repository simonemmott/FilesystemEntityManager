package com.k2.FilesystemEntityManager.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class KeyLinkProxyInvocationHandler<D,E> implements InvocationHandler {

	KeyLinkProxyController<D,E> klpc;
	
	public KeyLinkProxyInvocationHandler(KeyLinkProxyController<D,E> klpc) {
		this.klpc = klpc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		E target = klpc.unwrap((E)proxy);
		if (target == null) return null;
		return method.invoke(target, args);
	}

}
