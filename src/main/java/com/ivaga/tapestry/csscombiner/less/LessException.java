package com.ivaga.tapestry.csscombiner.less;

/**
 * A exception that occur if some can not parse or converted.
 */
public class LessException extends RuntimeException {

	private String msg;

	/**
	 * Constructs a new less exception with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	LessException(String message) {
		super(message);
		this.msg = super.getMessage();
	}

	/**
	 * Constructs a new runtime exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	LessException(Throwable cause) {
		super(cause);
		this.msg = super.getMessage();
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause
	 */
	LessException(String message, Throwable cause) {
		super(message, cause);
		this.msg = super.getMessage();
	}

	/**
	 * Add a position to the less file stacktrace
	 * 
	 * @param filename
	 *            the less file, can be null if a string was parsed
	 * @param line
	 *            the line number in the less file
	 * @param column
	 *            the column in the less file
	 */
	void addPosition(String filename, int line, int column) {
		StringBuilder builder = new StringBuilder();
		builder.append(" on line ").append(line).append(", column ").append(column);
		if (filename != null) {
			builder.append(", file ").append(filename);
		}
		if (!msg.contains(builder)) {
			msg += "\n\t" + builder;
		}
	}

	/**
	 * The message plus the less file stacktrace. {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return msg;
	}
}
