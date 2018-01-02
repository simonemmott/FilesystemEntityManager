package com.k2.FilesystemEntityManager;

public class FemError extends Error {

	private static final long serialVersionUID = -6546590991791367338L;

	public FemError() {
	}

	public FemError(String message) {
		super(message);
	}

	public FemError(Throwable cause) {
		super(cause);
	}

	public FemError(String message, Throwable cause) {
		super(message, cause);
	}

	public FemError(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
