package com.ivaga.tapestry.csscombiner.less;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap with default values.
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
class DefaultedHashMap<K, V> extends HashMap<K, V> {

	private final Map<K, V> defaultValues;

	/**
	 * Create a new instance. The default values are referenced and not copied.
	 * 
	 * @param defaultValues
	 *            the default values
	 */
	DefaultedHashMap(Map<K, V> defaultValues) {
		this.defaultValues = defaultValues;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(Object key) {
		V value = super.get(key);
		if (value != null) {
			return value;
		}
		return defaultValues.get(key);
	}
}
