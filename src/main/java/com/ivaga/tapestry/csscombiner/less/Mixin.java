package com.ivaga.tapestry.csscombiner.less;

import java.util.ArrayList;
import java.util.List;

/**
 * The placeholder of a mixin.
 */
class Mixin extends LessObject implements Formattable {

	private final String name;
	private final HashMultimap<String, Rule> mixins;
	private final List<Expression> paramValues;
	private final boolean important;
	private List<MixinMatch> mixinRules;
	private int stackID;

	/**
	 * Create a new instance.
	 * 
	 * @param obj
	 *            another LessObject with parse position.
	 * @param name
	 *            the name of the mixin
	 * @param paramValues
	 *            the parameters.
	 * @param mixins
	 *            map with all mixins
	 */
	Mixin(LessObject obj, String name, Operation paramValues, HashMultimap<String, Rule> mixins) {
		super(obj);
		if (name.endsWith("important")) { // it can be "!importan" or "!
											// important"
			boolean importantTemp = false;
			LOOP: for (int i = name.length() - 10; i >= 0; i--) {
				switch (name.charAt(i)) {
				case ' ':
					break;
				case '!':
					importantTemp = true;
					name = name.substring(0, i).trim();
					break LOOP;
				default:
					break LOOP;
				}
			}
			important = importantTemp;
		} else {
			important = false;
		}
		this.name = name;
		this.paramValues = paramValues == null ? null : paramValues.getOperands();
		this.mixins = mixins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getType() {
		return MIXIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendTo(CssFormatter formatter) {
		try {
			if (important) {
				formatter.incImportant();
			}
			for (MixinMatch match : getRules(formatter)) {
				Rule rule = match.getRule();
				formatter.addMixin(rule, match.getMixinParameters(), rule.getVariables());
				rule.appendPropertiesTo(formatter);
				formatter.removeMixin();
			}
		} catch (LessException ex) {
			ex.addPosition(filename, line, column);
			throw ex;
		} catch (StackOverflowError soe) {
			throw createException("Maximum call stack size exceeded in mixin: " + name, soe);
		} finally {
			if (important) {
				formatter.decImportant();
			}
		}
	}

	/**
	 * Append the rules of the mixins to the formatter.
	 * 
	 * @param parentSelector
	 *            the selectors of the caller
	 * @param formatter
	 *            the formatter
	 */
	void appendSubRules(String[] parentSelector, CssFormatter formatter) {
		try {
			if (important) {
				formatter.incImportant();
			}
			for (MixinMatch match : getRules(formatter)) {
				Rule rule = match.getRule();
				formatter.addMixin(rule, match.getMixinParameters(), rule.getVariables());
				rule.appendMixinsTo(parentSelector, formatter);
				for (Rule subMixin : rule.getSubrules()) {
					if (!subMixin.isMixin() && (parentSelector == null || !subMixin.isInlineRule(formatter))) {
						subMixin.appendTo(parentSelector, formatter);
					}
				}
				formatter.removeMixin();
			}
		} catch (LessException ex) {
			ex.addPosition(filename, line, column);
			throw ex;
		} finally {
			if (important) {
				formatter.decImportant();
			}
		}
	}

	/**
	 * Get the rules of the mixin
	 * 
	 * @param formatter
	 *            the formatter
	 * @return the rules, can be empty if no condition matched but not null
	 * @throws LessException
	 *             if no rule match the name of the mixin.
	 */

	private List<MixinMatch> getRules(CssFormatter formatter) throws LessException {
		if (mixinRules != null && stackID == formatter.stackID()) {
			return mixinRules;
		}
		List<Rule> rules = formatter.getMixin(name);
		if (rules == null) {
			rules = mixins.get(name);
		}
		if (rules == null) {
			int idx = name.indexOf('>'); // mixin with namespace?
			if (idx > 0) {
				String mainName = name.substring(0, idx).trim();
				rules = mixins.get(mainName);
				if (rules != null) {
					rules = rules.get(0).getMixin(name.substring(idx + 1).trim());
				}
			} else {
				idx = name.indexOf('.'); // mixin with namespace?
				if (idx > 0) {
					String mainName = name.substring(0, idx).trim();
					rules = mixins.get(mainName);
					if (rules != null) {
						rules = rules.get(0).getMixin(name.substring(idx).trim());
					}
				}
			}
			if (rules == null) {
				throw createException("Undefine mixin: " + name);
			}
		}
		stackID = formatter.stackID();
		mixinRules = new ArrayList<>();
		boolean paramMatch = false;
		List<Rule> defaultMixins = null;
		for (Rule rule : rules) {
			MixinMatch matching = rule.match(formatter, paramValues, false);
			if (matching != null) {
				paramMatch = true;
				if (matching.getGuard()) {
					mixinRules.add(matching);
				} else if (matching.wasDefault()) {
					if (defaultMixins == null) {
						defaultMixins = new ArrayList<>();
					}
					defaultMixins.add(rule);
				}
			}
		}
		if (!paramMatch) {
			throw createException("No matching definition was found for: " + name);
		}
		if (mixinRules.size() == 0 && defaultMixins != null) {
			for (Rule rule : defaultMixins) {
				MixinMatch matching = rule.match(formatter, paramValues, true);
				if (matching != null && matching.getGuard()) {
					mixinRules.add(matching);
				}
			}
		}
		return mixinRules;
	}

}
