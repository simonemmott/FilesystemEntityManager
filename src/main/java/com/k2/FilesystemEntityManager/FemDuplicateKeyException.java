package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

public class FemDuplicateKeyException extends FemException {

	private static final long serialVersionUID = 3893110315990960001L;

	public FemDuplicateKeyException(Class<?> cls, Serializable key, File repo, Throwable cause) {
		super("An instance of '"+cls.getCanonicalName()+"("+key+") already exists in the repo '"+repo.getAbsolutePath()+"'", cause);
	}

	public FemDuplicateKeyException(Class<?> cls, Serializable key, File repo) {
		super("An instance of '"+cls.getCanonicalName()+"("+key+") already exists in the repo '"+repo.getAbsolutePath()+"'");
	}

}
