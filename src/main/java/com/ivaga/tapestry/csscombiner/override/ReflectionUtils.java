package com.ivaga.tapestry.csscombiner.override;

import java.lang.reflect.Field;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.services.javascript.StylesheetLink;

public class ReflectionUtils {
	private static Field assetField;

	public static Asset getAsset(StylesheetLink link) {
		try {
			if (null == assetField) {
				assetField = link.getClass().getDeclaredField("asset");
				assetField.setAccessible(true);
			}
			return (Asset) assetField.get(link);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
