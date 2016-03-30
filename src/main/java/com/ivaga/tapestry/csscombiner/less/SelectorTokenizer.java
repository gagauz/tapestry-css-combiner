package com.ivaga.tapestry.csscombiner.less;

/**
 * Split a Selector string in single selectors.
 */
class SelectorTokenizer {

	private String selector;

	private int idx, lastIdx;

	/**
	 * Create a new tokenizer.
	 * 
	 * @param selector
	 *            all selectors
	 */
	SelectorTokenizer init(String selector) {
		this.selector = selector;
		idx = lastIdx = 0;
		return this;
	}

	/**
	 * Get the next selector.
	 * 
	 * @return the next or null
	 */

	String next() {
		if (lastIdx >= selector.length()) {
			return null;
		}
		LOOP: do {
			if (++idx == selector.length()) {
				break LOOP;
			}
			switch (selector.charAt(idx)) {
			case '.':
			case '#':
			case ':':
			case '[':
				break LOOP;
			}
		} while (true);
		String str = selector.substring(lastIdx, idx).trim();
		lastIdx = idx;
		return str;
	}
}
