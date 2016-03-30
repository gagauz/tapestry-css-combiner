package com.ivaga.tapestry.csscombiner.less;

import java.util.ArrayList;

/**
 * A CSS output of a media rule.
 */
class CssMediaOutput extends CssOutput {

	private String[] selectors;

	private ArrayList<CssOutput> results = new ArrayList<>();

	private boolean isReference;

	private LessExtendMap lessExtends;

	/**
	 * Create a instance.
	 * 
	 * @param selectors
	 *            the selectors of the rule
	 * @param output
	 *            a buffer for the content of the rule.
	 * @param isReference
	 *            if this content was loaded via reference
	 * @param lessExtends
	 *            a extends container only for this media rule
	 */
	CssMediaOutput(String[] selectors, StringBuilder output, boolean isReference, LessExtendMap lessExtends) {
		this.selectors = selectors;
		this.results.add(new CssPlainOutput(output));
		this.isReference = isReference;
		this.lessExtends = lessExtends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void appendTo(StringBuilder target, LessExtendMap lessExtends, CssFormatter formatter) {
		if (hasContent(lessExtends)) {
			formatter.startBlockImpl(selectors);
			for (CssOutput cssOutput : results) {
				cssOutput.appendTo(target, this.lessExtends, formatter);
			}
			formatter.endBlockImpl();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean hasContent(LessExtendMap lessExtends) {
		for (CssOutput cssOutput : results) {
			if (cssOutput.hasContent(this.lessExtends)) {
				return true;
			}
		}
		return false;
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
	 * Start a block inside the media
	 * 
	 * @param selectors
	 *            the selectors
	 * @param output
	 *            a buffer for the content of the rule.
	 */
	void startBlock(String[] selectors, StringBuilder output) {
		this.results.add(new CssRuleOutput(selectors, output, isReference));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	StringBuilder getOutput() {
		CssOutput cssOutput = results.get(results.size() - 1);
		if (cssOutput instanceof CssRuleOutput) {
			cssOutput = new CssPlainOutput(new StringBuilder());
			this.results.add(cssOutput);
		}
		return cssOutput.getOutput();
	}
}
