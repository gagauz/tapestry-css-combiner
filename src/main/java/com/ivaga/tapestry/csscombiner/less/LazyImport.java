package com.ivaga.tapestry.csscombiner.less;

import java.net.URL;
import java.util.HashMap;

/**
 * Hold the context for a lazy import for filenames with variables.
 */
class LazyImport extends ValueExpression {

	private final URL baseURL;

	private final HashMap<String, Expression> variables;

	private final Formattable lastRuleBefore;

	/**
	 * Create a new instance.
	 * 
	 * @param obj
	 *            another LessObject with parse position.
	 * @param baseURL
	 *            current baseURL
	 * @param filename
	 *            value of the filename, can contain place holders.
	 * @param variables
	 *            variables
	 * @param lastRuleBefore
	 *            pointer to the rules where the import should be included.
	 */
	LazyImport(LessObject obj, URL baseURL, String filename, HashMap<String, Expression> variables, Formattable lastRuleBefore) {
		super(obj, filename);
		this.baseURL = baseURL;
		this.variables = variables;
		this.lastRuleBefore = lastRuleBefore;
	}

	/**
	 * The base url or the url of the parent less file.
	 * 
	 * @return the url or null
	 */
	URL getBaseUrl() {
		return baseURL;
	}

	/**
	 * Get the variables with default values before the import.
	 * 
	 * @return the variables
	 */
	HashMap<String, Expression> getVariables() {
		return variables;
	}

	/**
	 * Get the last rule before the import. New rules must added after this
	 * position.
	 * 
	 * @return a formattable
	 */
	Formattable lastRuleBefore() {
		return lastRuleBefore;
	}
}
