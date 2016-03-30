package com.ivaga.tapestry.csscombiner.less;

/**
 * Marker in the list of Formatter to mark a reference flag switch.
 */
class ReferenceInfo implements Formattable {

	private final boolean isReference;

	/**
	 * Create a new instance.
	 * 
	 * @param isReference
	 *            true, then the follow content is load via reference
	 */
	ReferenceInfo(boolean isReference) {
		this.isReference = isReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getType() {
		return REFERENCE_INFO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		throw new IllegalStateException();
	}

	/**
	 * If the follow content is loaded via "reference"
	 * 
	 * @return true, if reference
	 */
	boolean isReference() {
		return isReference;
	}
}
