package com.ivaga.tapestry.csscombiner.less;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A HashMap that hold multiple values for a single key.
 *
 * @param <K>
 *            The key type
 * @param <V>
 *            the values type
 */
class HashMultimap<K, V> {

	private final HashMap<K, List<V>> map = new HashMap<>();

	private HashMultimap<K, V> parent;

	/**
	 * Default constructor
	 */
	HashMultimap() {
	}

	/**
	 * Constructor with parent
	 * 
	 * @param parent
	 *            parent, will be hold by reference
	 */
	HashMultimap(HashMultimap<K, V> parent) {
		this.parent = parent;
	}

	/**
	 * Add a value to this map. If the map previously contained a mapping for
	 * the key, then there are two values now.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	void add(K key, V value) {
		List<V> rules = map.get(key);
		if (rules == null) {
			rules = new ArrayList<>();
			map.put(key, rules);
		}
		rules.add(value);
	}

	/**
	 * Get all values for the given key. If no key exists then null is return.
	 * 
	 * @param key
	 *            the key
	 * @return the list or null
	 */
	List<V> get(K key) {
		List<V> result = map.get(key);
		if (parent != null) {
			List<V> resultParent = parent.get(key);
			if (result == null) {
				return resultParent;
			} else if (resultParent == null) {
				return result;
			} else {
				result.addAll(resultParent);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return map.toString();
	}
}
