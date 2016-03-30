package com.ivaga.tapestry.csscombiner.less;

/**
 * A version of the CssFormatter that produce a compressed output.
 */
class CompressCssFormatter extends CssFormatter {

	private boolean wasSemicolon;

	/**
	 * Create an instance.
	 */
	CompressCssFormatter() {
		getFormat().setMinimumIntegerDigits(0);
	}

	/**
	 * Do nothing. {@inheritDoc}
	 */
	@Override
	CssFormatter space() {
		return this;
	}

	/**
	 * Do nothing. {@inheritDoc}
	 */
	@Override
	CssFormatter newline() {
		return this;
	}

	/**
	 * Do nothing. {@inheritDoc}
	 */
	@Override
	void insets() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	CssFormatter comment(String msg) {
		if (msg.startsWith("/*!")) {
			checkSemicolon();
			getOutput().append(msg);
		}
		return this;
	}

	/**
	 * Write a 3 digit color definition if possible. {@inheritDoc}
	 */
	@Override
	CssFormatter appendColor(double color, String hint) {
		if (!inlineMode()) {
			int red = ColorUtils.red(color);
			if (red % 17 == 0) {
				int green = ColorUtils.green(color);
				if (green % 17 == 0) {
					int blue = ColorUtils.blue(color);
					if (blue % 17 == 0) {
						super.append('#')
								.append(Character.forDigit(red / 17, 16))
								.append(Character.forDigit(green / 17, 16))
								.append(Character.forDigit(blue / 17, 16));
						return this;
					}
				}
			}
		}
		return super.appendColor(color, null);
	}

	/**
	 * Remove units if value is zero. {@inheritDoc}
	 */
	@Override
	CssFormatter appendValue(double value, String unit) {
		if (value == 0) {
			switch (unit) {
			case "deg":
			case "s":
				break;
			default:
				super.append('0');
				return this;
			}
		}
		return super.appendValue(value, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void semicolon() {
		wasSemicolon = true;
	}

	/**
	 * Check is a semicolon should be write.
	 */
	private void checkSemicolon() {
		if (wasSemicolon) {
			wasSemicolon = false;
			super.semicolon();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	CssFormatter startBlock(String[] selectors) {
		checkSemicolon();
		return super.startBlock(selectors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void appendProperty(String name, Expression value) {
		checkSemicolon();
		super.appendProperty(name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	CssFormatter endBlock() {
		wasSemicolon = false;
		return super.endBlock();
	}
}
