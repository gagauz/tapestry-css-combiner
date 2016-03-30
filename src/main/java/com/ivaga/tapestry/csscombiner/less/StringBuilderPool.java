package com.ivaga.tapestry.csscombiner.less;

import java.util.ArrayDeque;

/**
 * A pool for StringBuilders to reduce the allocation of new objects.
 */
final class StringBuilderPool {

	private final ArrayDeque<StringBuilder> pool = new ArrayDeque<StringBuilder>();

	/**
	 * Get a StringBuilder
	 * 
	 * @return a StringBuilder
	 */

	StringBuilder get() {
		if (pool.size() == 0) {
			return new StringBuilder();
		} else {
			StringBuilder builder = pool.pollLast();
			builder.setLength(0);
			return builder;
		}
	}

	/**
	 * Return a StringBuilder to the pool.
	 * 
	 * @param builder
	 *            a StringBuilder
	 */
	void free(StringBuilder builder) {
		pool.addLast(builder);
	}
}
