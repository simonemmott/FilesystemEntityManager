package com.k2.FilesystemEntityManager.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.FemError;
import com.k2.FilesystemEntityManager.testEntities.*;
import com.k2.FilesystemEntityManager.util.EntityUtil;
import com.k2.FilesystemEntityManager.util.cache.EntityLink;
import com.k2.Proxy.AProxy;
import com.k2.Proxy.ProxyFactory;
import com.k2.Util.classes.ClassUtil;

public class ProxyUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static <D,E> void setProxyInLink(EntityManager em, D linkOnObj, String linkFieldAlias) {
		setProxyInLink(em, linkOnObj, ClassUtil.getField(linkOnObj.getClass(), linkFieldAlias));
	}

	public static <D,E> void setProxyInLink(EntityManager em, D linkOnObj, EntityLink<D,E> link) {
		setProxyInLink(em, linkOnObj, link.getField());
	}

	public static <D,E> void setProxyInLink(EntityManager em, D linkOnObj, Field linkField) {
		E proxy = getKeyLinkProxy(em, linkOnObj, linkField);
		try {
			linkField.set(linkOnObj, proxy);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new FemError("Unable to set the proxy for the class {} in linked field {}.{}", e, 
					linkField.getType().getName(), linkOnObj.getClass().getName(), linkField.getName());
		}
	}

	@SuppressWarnings("unchecked")
	public static <D,E> E getKeyLinkProxy(EntityManager em, D linkOnObj, Field linkField) {
		
		Class<D> linkOnClass = (Class<D>) linkOnObj.getClass();
		
		EntityLink<D,E> link = (EntityLink<D, E>) EntityUtil.getEnityLinkByAlias(linkOnClass, linkField.getName());

		KeyLinkProxyController<D,E> klpc = KeyLinkProxyController.createController(em, linkOnObj, link.getType())
				.bind(link.getLinkFromFields());
		
		InvocationHandler handler = new KeyLinkProxyInvocationHandler<D,E>(klpc);

		
		try {

			Class<? extends AProxy<E>> proxyClass = (Class<? extends AProxy<E>>) ProxyFactory.staticFactory().getProxyClass(link.getType());
			AProxy<E> aProxy = proxyClass.newInstance();
			aProxy.setInvocationHandler(handler);
			return (E)aProxy;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new FemError("Unable to create a proxy to {}.", e, link.getType().getName());
		}	

	}

	/*
	@SuppressWarnings("unchecked")
	public static <D,E> Class<? extends E> getKeyLinkProxyClass(Class<D> linkOnClass, Class<E> linkToClass) {
		
		if (linkToClass == Foo.class) return (Class<? extends E>) Foo_DPx.class;
		if (linkToClass == Bar.class) return (Class<? extends E>) Bar_DPx.class;
		logger.warn("No key link proxy class found for link between {} and {}", linkOnClass.getName(), linkToClass.getName());
		return null;
	}
	*/
}
