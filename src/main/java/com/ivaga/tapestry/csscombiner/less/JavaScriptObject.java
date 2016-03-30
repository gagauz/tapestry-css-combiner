package com.ivaga.tapestry.csscombiner.less;

/**
 * Wrapper class to accessible a variable through scripting engine. Must be
 * public. Lesscss expected that the values are accessible through the method
 * toJS().
 */
public class JavaScriptObject {

	private Object obj;

	/**
	 * New wrapper object.
	 * 
	 * @param obj
	 *            the native value
	 */
	JavaScriptObject(Object obj) {
		this.obj = obj;
	}

	/**
	 * Unwrap method. Must be public.
	 * 
	 * @return the value
	 */
	public Object toJS() {
		return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return obj.toString();
	}
}
