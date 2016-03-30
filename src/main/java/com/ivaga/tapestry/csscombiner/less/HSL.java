package com.ivaga.tapestry.csscombiner.less;

/**
 * A HSL color value.
 */
class HSL {

	double h, s, l, a;

	/**
	 * Create a new color value.
	 * 
	 * @param h
	 *            hue value
	 * @param s
	 *            saturation
	 * @param l
	 *            lightness
	 * @param a
	 *            alpha
	 */
	public HSL(double h, double s, double l, double a) {
		this.h = h;
		this.s = s;
		this.l = l;
		this.a = a;
	}

}
