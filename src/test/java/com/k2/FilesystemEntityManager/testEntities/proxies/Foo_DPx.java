package com.k2.FilesystemEntityManager.testEntities.proxies;

import com.k2.FilesystemEntityManager.proxy.KeyLinkProxy;
import com.k2.FilesystemEntityManager.proxy.KeyLinkProxyController;
import com.k2.FilesystemEntityManager.testEntities.Foo;

public class Foo_DPx<D> extends Foo implements KeyLinkProxy<D,Foo>{

	private KeyLinkProxyController<D,Foo> dPx;

	public Foo_DPx(KeyLinkProxyController<D, Foo> dPx) { 
		this.dPx = dPx;
	}
	
	@Override
	public KeyLinkProxyController<D, Foo> dPx() { return dPx; }

	@Override
	public Long getId() {
		Foo obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.getId());
	}

	@Override
	public Foo setId(Long id) {
		Foo obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.setId(id));
	}

	@Override
	public String getName() {
		Foo obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.getName());
	}

	@Override
	public Foo setName(String name) {
		Foo obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.setName(name));
	}
	


}
