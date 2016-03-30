package com.ivaga.tapestry.csscombiner.less;

/**
 * A comment in the less file.
 */
class Comment implements Formattable {

	private String msg;

	/**
	 * Create a new instance of Comment.
	 * 
	 * @param msg
	 *            the comment text
	 */
	Comment(String msg) {
		this.msg = msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getType() {
		return COMMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		formatter.comment(msg);
	}

}
