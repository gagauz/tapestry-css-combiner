package com.ivaga.tapestry.csscombiner.less;

import java.util.HashMap;

/**
 * A interface of for sharing feature between root and rules.
 */
interface FormattableContainer {

	/**
	 * Add a formattable to this container
	 * 
	 * @param formattable
	 *            the formattable object
	 */
	void add(Formattable formattable);

	/**
	 * Get the container for variables of this container.
	 * 
	 * @return the variables
	 */
	HashMap<String, Expression> getVariables();
}
