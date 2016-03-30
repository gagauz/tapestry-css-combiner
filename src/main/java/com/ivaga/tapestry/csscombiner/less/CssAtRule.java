package com.ivaga.tapestry.csscombiner.less;

/**
 * A CSS rule that start with an @ character
 */
class CssAtRule extends LessObject implements Formattable {

	private final String css;

	/**
	 * Create CSS at-rule that have no special handling. Known CSS at rules
	 * are @charset, @document, @font-
	 * face, @import, @keyframes, @media, @namespace, @page, @supports
	 * 
	 * @param reader
	 *            the reader with parse position
	 * @param css
	 *            the content of the rule
	 */
	public CssAtRule(LessObject reader, String css) {
		super(reader);
		this.css = css;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getType() {
		return CSS_AT_RULE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		if (css.startsWith("@charset")) {
			if (formatter.isCharsetDirective()) {
				return; // write charset only once
			}
			formatter.setCharsetDirective();
			formatter = formatter.getHeader();
		} else if (css.startsWith("@import")) {
			formatter = formatter.getHeader();
		}
		formatter.getOutput();
		SelectorUtils.appendToWithPlaceHolder(formatter, css, 1, this);
		formatter.newline();
	}
}
