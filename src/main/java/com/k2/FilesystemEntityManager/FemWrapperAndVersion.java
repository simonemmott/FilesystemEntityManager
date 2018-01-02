package com.k2.FilesystemEntityManager;

import com.google.gson.annotations.Expose;
import com.k2.Util.Version.Version;

public class FemWrapperAndVersion<T> extends FemWrapper<T> {
	
	
	FemWrapperAndVersion() {}
		
	FemWrapperAndVersion(Integer ocn, T obj) {
		super(ocn, obj);
		version = Version.create();
	}

	FemWrapperAndVersion(T obj) {
		super(obj);
		version = Version.create();
	}

	FemWrapperAndVersion(T obj, Version ver) {
		super(obj);
		version = ver;
	}

	FemWrapperAndVersion(T obj, int major, int minor, int point) {
		super(obj);
		version = Version.create(major, minor, point);
	}

	@Expose public Version version;
}
