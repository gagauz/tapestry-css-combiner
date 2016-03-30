package com.ivaga.tapestry.csscombiner.less;

/**
 * A CSS output that has not the layout of a rule like comments and directives.
 */
class CssPlainOutput extends CssOutput {

	private StringBuilder output;

	/**
	 * Create a new output sequence.
	 * 
	 * @param output
	 *            a string builder to hold the plain output.
	 */
	CssPlainOutput(StringBuilder output) {
		this.output = output;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void appendTo(StringBuilder target, LessExtendMap lessExtends, CssFormatter formatter) {
		target.append(output);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean hasContent(LessExtendMap lessExtends) {
		return output.length() > 0;
	}

	/**
	 * Get ever null for this type of CssOutput.
	 * 
	 * @return ever null
	 */
	@Override
    String[] getSelectors() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	StringBuilder getOutput() {
		return output;
	}
}
