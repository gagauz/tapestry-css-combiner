package com.ivaga.tapestry.csscombiner.less;

import java.util.Map;

/**
 * A single match of a mixin to a rule.
 */
class MixinMatch {

	private Rule rule;

	private Map<String, Expression> mixinParameters;

	private boolean guard;

	private boolean wasDefault;

	/**
	 * Result of an MixinMatch
	 * 
	 * @param rule
	 *            the mixin
	 * @param mixinParameters
	 *            the calling parameters
	 * @param guard
	 *            if the guard match
	 * @param wasDefault
	 *            if there is a default() guard function
	 */
	MixinMatch(Rule rule, Map<String, Expression> mixinParameters, boolean guard, boolean wasDefault) {
		this.rule = rule;
		this.mixinParameters = mixinParameters;
		this.guard = guard;
		this.wasDefault = wasDefault;
	}

	/**
	 * The rule of the match.
	 * 
	 * @return the rule
	 */
	Rule getRule() {
		return rule;
	}

	/**
	 * The calling parameters
	 * 
	 * @return the parameters
	 */
	Map<String, Expression> getMixinParameters() {
		return mixinParameters;
	}

	/**
	 * if the guard is TRUE
	 * 
	 * @return if true
	 */
	boolean getGuard() {
		return guard;
	}

	/**
	 * If the guard of this mixin use the default function
	 * 
	 * @return true, if default function is used
	 */
	boolean wasDefault() {
		return wasDefault;
	}
}
