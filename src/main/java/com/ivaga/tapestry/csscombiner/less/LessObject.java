package com.ivaga.tapestry.csscombiner.less;

/**
 * A base object for the parser that hold a parse position.
 */
class LessObject {

	String filename;

	int line, column;

	/**
	 * Only used from LessLookAheadReader
	 * 
	 * @param filename
	 *            the name of the less file, can be null if a string is parsed.
	 */
	LessObject(String filename) {
		this.filename = filename;
	}

	/**
	 * Create a new instance with filename, line number and column position from
	 * the LessObject.
	 * 
	 * @param obj
	 *            another LessObject with parse position.
	 */
	LessObject(LessObject obj) {
		this.filename = obj.filename;
		this.line = obj.line;
		this.column = obj.column;
	}

	/**
	 * Create a LessException with filename, line number and column of the
	 * current object.
	 * 
	 * @param msg
	 *            the error message.
	 * @return the exception
	 */

	LessException createException(String msg) {
		LessException lessEx = new LessException(msg);
		lessEx.addPosition(filename, line, column);
		return lessEx;
	}

	/**
	 * Create a LessException with filename, line number and column of the
	 * current object.
	 * 
	 * @param msg
	 *            the error message.
	 * @param cause
	 *            the cause
	 * @return the exception
	 */

	LessException createException(String msg, Throwable cause) {
		LessException lessEx = new LessException(msg, cause);
		lessEx.addPosition(filename, line, column);
		return lessEx;
	}

	/**
	 * If cause is already a LessException then filename, line number and column
	 * of the current object are added to the less stacktrace. With any other
	 * type of exception a new LessException is created.
	 * 
	 * @param cause
	 *            the cause
	 * @return the exception
	 */

	LessException createException(Throwable cause) {
		LessException lessEx = cause.getClass() == LessException.class ? (LessException) cause : new LessException(cause);
		lessEx.addPosition(filename, line, column);
		return lessEx;
	}

	/**
	 * Get the file name in which the current object is define.
	 * 
	 * @return the filename, can be null if a string was parsed.
	 */
	String getFileName() {
		return filename;
	}
}
