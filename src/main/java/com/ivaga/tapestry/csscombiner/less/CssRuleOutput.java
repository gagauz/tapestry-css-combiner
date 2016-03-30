package com.ivaga.tapestry.csscombiner.less;

/**
 * A CSS output of a single rule.
 */
class CssRuleOutput extends CssOutput {

	private String[] selectors;
	private StringBuilder output;
	private boolean isReference;
	private boolean isConcatExtents;

	/**
	 * Create a instance.
	 * 
	 * @param selectors
	 *            the selectors of the rule
	 * @param output
	 *            a buffer for the content of the rule.
	 * @param isReference
	 *            if this content was loaded via reference
	 */
	CssRuleOutput(String[] selectors, StringBuilder output, boolean isReference) {
		this.selectors = selectors;
		this.output = output;
		this.isReference = isReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void appendTo(StringBuilder target, LessExtendMap lessExtends, CssFormatter formatter) {
		if (hasContent(lessExtends)) {
			formatter.startBlockImpl(selectors);
			target.append(output);
			formatter.endBlockImpl();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean hasContent(LessExtendMap lessExtends) {
		if (output.length() == 0) {
			return false;
		}
		if (!isConcatExtents) {
			isConcatExtents = true;
			selectors = lessExtends.concatenateExtends(selectors, isReference);
		}
		return selectors.length > 0;
	}

	/**
	 * Get the selectors of this rule.
	 * 
	 * @return the selectors
	 */
	@Override
    String[] getSelectors() {
		return selectors;
	}

	/**
	 * Get the output of this rule.
	 * 
	 * @return the output
	 */
	@Override
    StringBuilder getOutput() {
		return output;
	}
}
