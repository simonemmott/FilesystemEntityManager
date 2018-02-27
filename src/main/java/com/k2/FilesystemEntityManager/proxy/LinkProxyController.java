package com.k2.FilesystemEntityManager.proxy;

import javax.persistence.EntityManager;

public interface LinkProxyController<D,E> {

	public E unwrap();

	public static <A,B> LinkProxyController<A,B> createController(EntityManager em, A declaringInstance, Class<B> linkToClass) { return null; }

	
}
