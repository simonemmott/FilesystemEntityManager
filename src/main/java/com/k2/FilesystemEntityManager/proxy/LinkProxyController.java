package com.k2.FilesystemEntityManager.proxy;

import javax.persistence.EntityManager;

public interface LinkProxyController<D,E> {

	public E unwrap(E proxy);

	public static <A,B> LinkProxyController<A,B> createController(EntityManager em, A declaringInstance, Class<B> linkToClass) { return null; }

	
}
