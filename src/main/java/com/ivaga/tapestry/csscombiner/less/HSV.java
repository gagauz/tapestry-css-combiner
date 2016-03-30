package com.ivaga.tapestry.csscombiner.less;

/**
 * A HSV color value.
 */
class HSV {

	double h, s, v, a;

	/**
	 * Create a new color value.
	 * 
	 * @param h
	 *            hue value
	 * @param s
	 *            saturation
	 * @param v
	 *            value
	 * @param a
	 *            alpha
	 */
	public HSV(double h, double s, double v, double a) {
		this.h = h;
		this.s = s;
		this.v = v;
		this.a = a;
	}

}
