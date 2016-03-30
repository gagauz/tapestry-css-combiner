package com.ivaga.tapestry.csscombiner.less;

/**
 * Container for formatted CSS result.
 */
abstract class CssOutput {

	/**
	 * Write the this output to the target
	 * 
	 * @param target
	 *            the target
	 * @param lessExtends
	 *            all extends in the less
	 * @param formatter
	 *            a formatter
	 */
	abstract void appendTo(StringBuilder target, LessExtendMap lessExtends, CssFormatter formatter);

	/**
	 * If this output has content
	 * 
	 * @param lessExtends
	 *            current extends container
	 * @return true, if there is content
	 */
	abstract boolean hasContent(LessExtendMap lessExtends);

	/**
	 * Get the selectors of this rule.
	 * 
	 * @return the selectors
	 */
	abstract String[] getSelectors();

	/**
	 * Get the native output buffer
	 * 
	 * @return the buffer
	 */
	abstract StringBuilder getOutput();
}
