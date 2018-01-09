package com.k2.FilesystemEntityManager;

import com.k2.Util.StringUtil;

/**
 * Instances of this unchecked error are thrown when the file system entity manager identifies an exception condition that should
 * not occur under ordinary conditions. The use of unchecked exceptions prevents unnecessary defensive coding.
 * 
 * @author simon
 *
 */
public class FemError extends Error {

	private static final long serialVersionUID = -6546590991791367338L;

	/**
	 * Create new anonymous unchecked file system entity manager exception 
	 */
	public FemError() {
	}
	/**
	 * Create a new unchecked exception with the given message with all instances of '{}' replaced with the 
	 * given replacements
	 * 
	 * @param message		The message template for the unchecked exception
	 * @param replacements	The replacements for instances of '{}' in the message template
	 */
	public FemError(String message, Object ... replacements) {
		super(StringUtil.replaceAll(message, "{}", replacements));
	}
	/**
	 * Create a new unchecked exceptions for the given throwable cause
	 * 
	 * This is used to convert checked exceptions thrown by the underlying implementation to unchecked exceptions
	 * thrown by the file system entity manager factory. This prevents unnecessary defensive coding of exception handling.
	 * Unchecked exceptions are only thrown for unrecoverable errors.
	 * 
	 * @param cause	The throwable cause that gave rise to this unchecked exception
	 */
	public FemError(Throwable cause) {
		super(cause);
	}
	/**
	 * Create a new unchecked exception for the given throwable cause overriding the message with the given message template 
	 * and replacement values
	 * @param message		The message template
	 * @param cause			The throwable cause that gave rise to this unchecked exception
	 * @param replacements	The values to replace instances of '{}' in the message template
	 */
	public FemError(String message, Throwable cause, Object ... replacements) {
		super(StringUtil.replaceAll(message, "{}", replacements), cause);
	}
	/**
	 * Create a new unchecked exception for the given throwable cause overriding the message with the given message template and
	 * specifying whether the exceptions can be suppressed and whether the stack trace should be writable
	 * @param message			The message template
	 * @param cause				The throwable cause that gave rise to the unchecked exception
	 * @param enableSuppression	Whether or not the unchecked exceptions is suppressable 
	 * @param writableStackTrace	Whether or not the stack trace should be writable
	 * @param replacements		The values to replace instances of '{}' in the message template
	 */
	public FemError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object ... replacements) {
		super(StringUtil.replaceAll(message, "{}", replacements), cause, enableSuppression, writableStackTrace);
	}

}
