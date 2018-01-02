package com.k2.FilesystemEntityManager;

import java.io.File;
import java.io.Serializable;

public class FemMutatedObjectException extends FemException {

	private static final long serialVersionUID = -8320901828413741790L;

	public FemMutatedObjectException(Class<?> cls, Serializable key, File repo, Throwable cause) {
		super("The instance of '"+cls.getCanonicalName()+"("+key+") in the repo '"+repo.getAbsolutePath()+"' has been changed by another process", cause);
	}

	public FemMutatedObjectException(Class<?> cls, Serializable key, File repo) {
		super("The instance of '"+cls.getCanonicalName()+"("+key+") in the repo '"+repo.getAbsolutePath()+"' has been changed by another process");
	}

}
