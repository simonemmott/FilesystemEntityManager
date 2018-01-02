package com.k2.FilesystemEntityManager;

public class CachedObject<T> {
	
	Integer originalOcn;
	FemWrapper<T> wrappedObj;
	
	CachedObject(T obj) {
		originalOcn = 0;
		wrappedObj = new FemWrapper<T>(obj);
	}

	CachedObject(FemWrapper<T> wrappedObj) {
		originalOcn = wrappedObj.ocn;
		this.wrappedObj = wrappedObj;
	}

}
