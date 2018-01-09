package com.k2.FilesystemEntityManager;

import com.k2.Util.StringUtil;

/**
 * All checked exceptions thrown by the file system entity manger are super-classes of this checked exception
 * 
 * @author simon
 *
 */
public class FemException extends Exception {

	private static final long serialVersionUID = 5355564467747528843L;

	public FemException() {
	}

	public FemException(String message, Object ... replacements) {
		super(StringUtil.replaceAll(message, "{}", replacements));
	}

	public FemException(Throwable cause) {
		super(cause);
	}

	public FemException(String message, Throwable cause, Object ... replacements) {
		super(StringUtil.replaceAll(message, "{}", replacements), cause);
	}

	public FemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
