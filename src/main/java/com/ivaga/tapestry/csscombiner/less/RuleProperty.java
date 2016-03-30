package com.ivaga.tapestry.csscombiner.less;

/**
 * A single CSS property of a CSS rule in the format:
 * <p>
 * name: value;
 */
class RuleProperty implements Formattable {

	private final String name;

	private final Expression value;

	/**
	 * Create a new property.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value
	 */
	RuleProperty(String name, Expression value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getType() {
		return PROPERTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		try {
			formatter.appendProperty(name, value);
		} catch (Exception ex) {
			throw value.createException(ex);
		}
	}
}
