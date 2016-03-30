package com.ivaga.tapestry.csscombiner.less;

/**
 * An object that can be print as CSS.
 */
interface Formattable {

	static final int PROPERTY = 0;
	static final int RULE = 1;
	static final int MIXIN = 2;
	static final int EXPRESSION = 3;
	static final int COMMENT = 4;
	static final int CSS_AT_RULE = 5;
	static final int EXTENDS = 6;
	static final int REFERENCE_INFO = 7;

	/**
	 * The type of formattable. Can be used in switches
	 * 
	 * @return the type
	 */
	int getType();

	/**
	 * Write the object to the CSS output
	 * 
	 * @param formatter
	 *            the CCS target
	 */
	void appendTo(CssFormatter formatter);
}
