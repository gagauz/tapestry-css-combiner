package com.ivaga.tapestry.csscombiner.less;

import javax.script.SimpleBindings;

/**
 * Bindings to the current less variables
 */
class JavaScriptBindings extends SimpleBindings {

	private CssFormatter formatter;

	/**
	 * Create a new bindings
	 * 
	 * @param formatter
	 *            the CCS target
	 */
	JavaScriptBindings(CssFormatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(Object key) {
		String keyStr = (String) key;
		if (keyStr.startsWith("nashorn.")) {
			return super.containsKey(key);
		}
		Expression var = formatter.getVariable('@' + keyStr);
		return var != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(Object key) {
		String keyStr = (String) key;
		if (keyStr.startsWith("nashorn.")) {
			return super.get(key);
		}
		Expression var = formatter.getVariable('@' + keyStr);
		if (var == null) {
			return null;
		}
		Object obj;
		switch (var.getDataType(formatter)) {
		case Expression.NUMBER:
			obj = new Double(var.doubleValue(formatter));
			break;
		case Expression.BOOLEAN:
			obj = Boolean.valueOf(var.booleanValue(formatter));
			break;
		case Expression.STRING:
		default:
			obj = var.stringValue(formatter);
		}
		return new JavaScriptObject(obj);
	}
}
