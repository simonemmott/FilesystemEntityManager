package com.k2.FilesystemEntityManager;

import com.google.gson.annotations.Expose;

public class FemWrapper<T> extends FemOcn{
	@Expose public T obj;
	
	FemWrapper() {}
	
	FemWrapper(T obj) {
		super(0);
		this.obj = obj;
	}
	
	FemWrapper(Integer ocn, T obj) {
		super(ocn);
		this.obj = obj;
	}
}
