package com.k2.FilesystemEntityManager;

import static org.junit.Assert.*;

import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.FilesystemEntityManager.testEntities.Bar;
import com.k2.FilesystemEntityManager.testEntities.Foo;
import com.k2.FilesystemEntityManager.testEntities.Too;
import com.k2.Util.entity.EntityUtil;
import com.k2.Util.classes.Getter.Type;
import com.k2.Util.entity.EntityCache;
import com.k2.Util.entity.EntityField;
import com.k2.Util.entity.EntityJoin;
import com.k2.Util.entity.EntityLink;


public class EntityUtilTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	
	@SuppressWarnings("unchecked")
	@Test
	public void entityCacheTestTest()
    {
		
		EntityCache<Too> tooCache = EntityUtil.getEntityCache(Too.class);
		
		assertNotNull(tooCache);
		
		assertEquals(Too.class, tooCache.getEntityClass());
		assertEquals("Too", tooCache.getEntityName());
		assertEquals("TOOS", tooCache.getTableName());
		
		assertEquals(5, tooCache.getColumns().size());
		assertEquals(7, tooCache.getFields().size());
		assertEquals(2, tooCache.getLinks().size());
		
		EntityField<Too,Long> too_id = (EntityField<Too, Long>) tooCache.getFieldByAlias("id");
		assertNotNull(too_id);
		assertEquals("id", too_id.getAlias());
		assertEquals("ID", too_id.getColumnName());
		assertEquals(Too.class, too_id.getOnClass());
		assertEquals(long.class, too_id.getType());
		assertEquals("getId", too_id.getGetterMethod().getName());
		assertEquals("setId", too_id.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_id.getGetter().getType());
		assertEquals("id", too_id.getField().getName());
		
		EntityLink<Too,Foo> too_foo = (EntityLink<Too, Foo>) tooCache.getFieldByAlias("foo");
		assertNotNull(too_foo);
		assertEquals("foo", too_foo.getAlias());
		assertNull(too_foo.getColumnName());
		assertEquals(Too.class, too_foo.getOnClass());
		assertEquals(Foo.class, too_foo.getType());
		assertEquals("getFoo", too_foo.getGetterMethod().getName());
		assertEquals("setFoo", too_foo.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_foo.getGetter().getType());
		assertEquals("foo", too_foo.getField().getName());
		
		assertEquals(1, too_foo.getJoins().size());		
		EntityJoin too_foo_ej_1 = too_foo.getJoins().get(0);
		assertNotNull(too_foo_ej_1);
		assertNotNull(too_foo_ej_1.getFromField());
		
		EntityField<Too,Long> too_foo_from = (EntityField<Too, Long>) too_foo_ej_1.getFromField();
		assertEquals("fooId", too_foo_from.getAlias());
		assertEquals("FOO_ID", too_foo_from.getColumnName());
		assertEquals(Too.class, too_foo_from.getOnClass());
		assertEquals(Long.class, too_foo_from.getType());
		assertEquals("getFooId", too_foo_from.getGetterMethod().getName());
		assertEquals("setFooId", too_foo_from.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_foo_from.getGetter().getType());
		assertEquals("fooId", too_foo_from.getField().getName());
		EntityField<Foo,Long> too_foo_to = (EntityField<Foo, Long>) too_foo_ej_1.getToField();
		assertEquals("id", too_foo_to.getAlias());
		assertEquals("ID", too_foo_to.getColumnName());
		assertEquals(Foo.class, too_foo_to.getOnClass());
		assertEquals(Long.class, too_foo_to.getType());
		assertEquals("getId", too_foo_to.getGetterMethod().getName());
		assertEquals("setId", too_foo_to.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_foo_to.getGetter().getType());
		assertEquals("id", too_foo_to.getField().getName());
		
		EntityLink<Too,Bar> too_bar = (EntityLink<Too, Bar>) tooCache.getFieldByAlias("bar");
		assertNotNull(too_bar);
		assertEquals("bar", too_bar.getAlias());
		assertNull(too_bar.getColumnName());
		assertEquals(Too.class, too_bar.getOnClass());
		assertEquals(Bar.class, too_bar.getType());
		assertEquals("getBar", too_bar.getGetterMethod().getName());
		assertEquals("setBar", too_bar.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_bar.getGetter().getType());
		assertEquals("bar", too_bar.getField().getName());
		
		assertEquals(2, too_bar.getJoins().size());		
		EntityJoin too_bar_ej_1 = too_bar.getJoins().get(0);
		assertNotNull(too_bar_ej_1);
		assertNotNull(too_bar_ej_1.getFromField());
		
		EntityField<Too,String> too_bar_ej_1_from = (EntityField<Too, String>) too_bar_ej_1.getFromField();
		assertEquals("barAlias", too_bar_ej_1_from.getAlias());
		assertEquals("BAR_ALIAS", too_bar_ej_1_from.getColumnName());
		assertEquals(Too.class, too_bar_ej_1_from.getOnClass());
		assertEquals(String.class, too_bar_ej_1_from.getType());
		assertEquals("getBarAlias", too_bar_ej_1_from.getGetterMethod().getName());
		assertEquals("setBarAlias", too_bar_ej_1_from.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_bar_ej_1_from.getGetter().getType());
		assertEquals("barAlias", too_bar_ej_1_from.getField().getName());
		EntityField<Bar,String> too_bar_ej_1_to = (EntityField<Bar, String>) too_bar_ej_1.getToField();
		assertEquals("alias", too_bar_ej_1_to.getAlias());
		assertEquals("ALIAS", too_bar_ej_1_to.getColumnName());
		assertEquals(Bar.class, too_bar_ej_1_to.getOnClass());
		assertEquals(String.class, too_bar_ej_1_to.getType());
		assertEquals("getAlias", too_bar_ej_1_to.getGetterMethod().getName());
		assertEquals("setAlias", too_bar_ej_1_to.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_bar_ej_1_to.getGetter().getType());
		assertEquals("alias", too_bar_ej_1_to.getField().getName());
		
		EntityJoin too_bar_ej_2 = too_bar.getJoins().get(1);
		assertNotNull(too_bar_ej_2);
		assertNotNull(too_bar_ej_2.getFromField());
		
		EntityField<Too,Integer> too_bar_ej_2_from = (EntityField<Too, Integer>) too_bar_ej_2.getFromField();
		assertEquals("barSequence", too_bar_ej_2_from.getAlias());
		assertEquals("BAR_SEQUENCE", too_bar_ej_2_from.getColumnName());
		assertEquals(Too.class, too_bar_ej_2_from.getOnClass());
		assertEquals(Integer.class, too_bar_ej_2_from.getType());
		assertEquals("getBarSequence", too_bar_ej_2_from.getGetterMethod().getName());
		assertEquals("setBarSequence", too_bar_ej_2_from.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_bar_ej_2_from.getGetter().getType());
		assertEquals("barSequence", too_bar_ej_2_from.getField().getName());
		EntityField<Bar,Integer> too_bar_ej_2_to = (EntityField<Bar, Integer>) too_bar_ej_2.getToField();
		assertEquals("sequence", too_bar_ej_2_to.getAlias());
		assertEquals("SEQUENCE", too_bar_ej_2_to.getColumnName());
		assertEquals(Bar.class, too_bar_ej_2_to.getOnClass());
		assertEquals(Integer.class, too_bar_ej_2_to.getType());
		assertEquals("getSequence", too_bar_ej_2_to.getGetterMethod().getName());
		assertEquals("setSequence", too_bar_ej_2_to.getSetterMethod().getName());
		assertEquals(Type.METHOD, too_bar_ej_2_to.getGetter().getType());
		assertEquals("sequence", too_bar_ej_2_to.getField().getName());
		
		


    }

	@Test
	public void keyClassTest()
    {
		
		java.lang.reflect.Field too_fooId = EntityUtil.getColumnByAlias(Too.class, "fooId");
		assertNotNull(too_fooId);
    }

}
