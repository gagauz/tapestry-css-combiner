package com.ivaga.tapestry.csscombiner.less;

/**
 * A reference to a variable
 */
class VariableExpression extends Expression {

	/**
	 * Create a new instance.
	 * 
	 * @param obj
	 *            another LessObject with parse position.
	 * @param name
	 *            the name of the variable starts with '@'
	 */
	VariableExpression(LessObject obj, String name) {
		super(obj, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		getValue(formatter).appendTo(formatter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDataType(CssFormatter formatter) {
		return getValue(formatter).getDataType(formatter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double doubleValue(CssFormatter formatter) {
		return getValue(formatter).doubleValue(formatter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean booleanValue(CssFormatter formatter) {
		return getValue(formatter).booleanValue(formatter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String unit(CssFormatter formatter) {
		return getValue(formatter).unit(formatter);
	}

	/**
	 * Get the referencing expression
	 * 
	 * @param formatter
	 *            current formatter with all variables
	 * @return the Expression
	 */
	Expression getValue(CssFormatter formatter) {
		String name = toString();
		Expression value = formatter.getVariable(name);
		if (value != null) {
			return value;
		}
		if (name.startsWith("@@")) {
			name = name.substring(1);
			value = formatter.getVariable(name);
			if (value != null) {
				formatter.setInlineMode(true);
				name = '@' + value.stringValue(formatter);
				formatter.setInlineMode(false);
				value = formatter.getVariable(name);
				if (value != null) {
					return value;
				}
			}
		}
		throw createException("Undefine Variable: " + name);
	}
}
