package com.k2.FilesystemEntityManager.testEntities.proxies;

import com.k2.FilesystemEntityManager.proxy.KeyLinkProxy;
import com.k2.FilesystemEntityManager.proxy.KeyLinkProxyController;
import com.k2.FilesystemEntityManager.testEntities.Bar;

public class Bar_DPx<D> extends Bar implements KeyLinkProxy<D,Bar>{

	private KeyLinkProxyController<D,Bar> dPx;

	public Bar_DPx(KeyLinkProxyController<D, Bar> dPx) { 
		this.dPx = dPx;
	}
	
	@Override
	public KeyLinkProxyController<D, Bar> dPx() { return dPx; }

	@Override
	public String getAlias() {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.getAlias());
	}

	@Override
	public Bar setAlias(String alias) {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.setAlias(alias));
	}

	@Override
	public Integer getSequence() {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.getSequence());
	}

	@Override
	public Bar setSequence(Integer sequence) {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.setSequence(sequence));
	}

	@Override
	public String getName() {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.getName());
	}

	@Override
	public Bar setName(String name) {
		Bar obj = dPx.unwrap();
		if (obj == null) return null;
		return (obj.setName(name));
	}

}
