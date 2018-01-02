package com.k2.FilesystemEntityManager;

public class FemException extends Exception {

	private static final long serialVersionUID = 5355564467747528843L;

	public FemException() {
	}

	public FemException(String message) {
		super(message);
	}

	public FemException(Throwable cause) {
		super(cause);
	}

	public FemException(String message, Throwable cause) {
		super(message, cause);
	}

	public FemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
