package com.ivaga.tapestry.csscombiner.less;

/**
 * An executed less expression with the selector of the caller.
 */
class LessExtendResult {

	private final String[] mainSelector;

	private final String extendingSelector;

	/**
	 * Create a new instance.
	 * 
	 * @param mainSelector
	 *            the main selectors of the extend
	 * @param extendingSelector
	 *            the extends selector
	 */
	LessExtendResult(String[] mainSelector, String extendingSelector) {
		this.mainSelector = mainSelector;
		this.extendingSelector = extendingSelector;
	}

	/**
	 * Get the main selectors.
	 * 
	 * @return the selectors
	 */
	String[] getSelectors() {
		return mainSelector;
	}

	/**
	 * Get the extending selectors.
	 * 
	 * @return the selectors.
	 */
	String getExtendingSelector() {
		return extendingSelector;
	}
}
