package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.testEntities.*;
import com.k2.FilesystemEntityManager.util.KeyUtil;
import com.k2.Util.StringUtil;
import com.k2.Util.Identity.IdentityUtil;
import com.k2.Util.classes.ClassUtil;


public class KeyUtilTests {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	
	@Test
	public void keyClassTest()
    {
		assertEquals(Long.class, KeyUtil.getKeyClass(Foo.class));
		assertEquals(BarId.class, KeyUtil.getKeyClass(Bar.class));
		assertEquals(long.class, KeyUtil.getKeyClass(Too.class));
    }

	@Test
	public void keyForTest() {
		Foo foo = new Foo(10L)
				.setName("This is a foo");
		
		Bar bar = new Bar("bar 1 !@#$%^&*()", 1)
				.setName("This is bar 1");
		
		Too too = new Too(1L)
				.setFoo(foo)
				.setBar(bar)
				.setName("This is a too");
		
		Serializable fooKeySer = KeyUtil.keyFor(Foo.class, too, ClassUtil.getField(Too.class, "fooId"));
		
		assertNotNull(fooKeySer);
		assertEquals(Long.class, fooKeySer.getClass());
		
		Long fooKey = (Long)fooKeySer;
		
		assertEquals(Long.valueOf(10L), fooKey);
		
		Serializable barKeySer = KeyUtil.keyFor(Bar.class, too, ClassUtil.getField(Too.class, "barAlias"), ClassUtil.getField(Too.class, "barSequence"));
		
		assertNotNull(barKeySer);
		assertEquals(BarId.class, barKeySer.getClass());
		
		BarId barKey = (BarId)barKeySer;
		
		assertEquals("bar 1 !@#$%^&*()", barKey.getAlias());
		assertEquals(Integer.valueOf(1), barKey.getSequence());
		
	}
	
	@Test
	public void keySerializeTest() {

		Foo foo = new Foo(10L)
				.setName("This is a foo");
		
		Bar bar = new Bar("bar 1 !@#$%^&*(){[}]:;'\",<.>/?-_=+", 1) {} 
				.setName("This is bar 1");
		
		Too too = new Too(1L)
				.setFoo(foo)
				.setBar(bar)
				.setName("This is a too");
		
		String fooIdSer = "10";
		String barIdSer = "bar+1+%21%40%23%24%25%5E%26*%28%29%7B%5B%7D%5D%3A%3B%27%22%2C%3C.%3E%2F%3F-_%3D%2B:1";
		String tooIdSer = "1";
		
//		System.out.println(StringUtil.replaceAll("Foo id: {}({}) Serialized: {}", "{}", 
//				IdentityUtil.getId(foo).getClass().getName(), 
//				KeyUtil.toString(IdentityUtil.getId(foo)), 
//				KeyUtil.serialize(IdentityUtil.getId(foo))));
		assertEquals(fooIdSer, KeyUtil.serialize(IdentityUtil.getId(foo)));
//		System.out.println(StringUtil.replaceAll("Bar id: {}({}) Serialized: {}", "{}", 
//				IdentityUtil.getId(bar).getClass().getName(), 
//				KeyUtil.toString(IdentityUtil.getId(bar)), 
//				KeyUtil.serialize(IdentityUtil.getId(bar))));
		assertEquals(barIdSer, KeyUtil.serialize(IdentityUtil.getId(bar)));
//		System.out.println(StringUtil.replaceAll("Too id: {}({}) Serialized: {}", "{}", 
//				IdentityUtil.getId(too).getClass().getName(), 
//				KeyUtil.toString(IdentityUtil.getId(too)), 
//				KeyUtil.serialize(IdentityUtil.getId(too))));
		assertEquals(tooIdSer, KeyUtil.serialize(IdentityUtil.getId(too)));
		
		Serializable fooId = KeyUtil.toKey(Foo.class, fooIdSer);
		assertEquals(Long.class, fooId.getClass());
		assertEquals(Long.valueOf(10), fooId);

		Serializable barId = KeyUtil.toKey(Bar.class, barIdSer);
		assertEquals(BarId.class, barId.getClass());
		BarId bid = (BarId)barId;
		assertEquals("bar 1 !@#$%^&*(){[}]:;'\",<.>/?-_=+", bid.getAlias());
		assertEquals(Integer.valueOf(1), bid.getSequence());
		assertFalse(bid == bar.getKey());
		assertTrue(bid.equals(bar.getKey()));

		Serializable tooId = KeyUtil.toKey(Too.class, tooIdSer);
		assertEquals(Long.class, tooId.getClass());
		assertEquals(Long.valueOf(1), tooId);

	}
	
}
