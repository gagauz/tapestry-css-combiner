package com.ivaga.tapestry.csscombiner.less;

import javax.script.SimpleScriptContext;

import java.util.HashMap;

/**
 * A ScriptContext for executing a JavaScript expression.
 */
class JavaScriptContext extends SimpleScriptContext {

	private final JavaScriptExpression expr;

	private final HashMap<String, Object> attributes = new HashMap<>();

	/**
	 * Create a new instance.
	 * 
	 * @param formatter
	 *            the formatter with a reference to the variables of the current
	 *            scope
	 * @param expr
	 *            the JavaScript expression
	 */
	JavaScriptContext(CssFormatter formatter, JavaScriptExpression expr) {
		this.expr = expr;
		engineScope = new JavaScriptBindings(formatter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAttribute(String name) {
		switch (name) {
		case "javax.script.filename":
			return expr.getFileName();
		}
		return attributes.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(String name, Object value, int scope) {
		attributes.put(name, value);
	}
}
